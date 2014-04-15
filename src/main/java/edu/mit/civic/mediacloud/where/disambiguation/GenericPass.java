package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Wrapper around the concept that we can disambiguate ResolvedLocations in passes, building
 * on the confidence in disambiguation results from preceeding passes. 
 * @author rahulb
 */
public abstract class GenericPass {

    private static final Logger logger = LoggerFactory.getLogger(GenericPass.class);

    private static final double EXACT_MATCH_CONFIDENCE = 1.0;

    private int triggerCount = 0;
    
    public void execute(List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        if(possibilitiesToDo.size()==0){    // bail if there is nothing to disambiguate
            return;
        }
        List<List<ResolvedLocation>> possibilitiesToRemove = disambiguate(
                possibilitiesToDo, bestCandidates);
        for(ResolvedLocation pickedCandidate: bestCandidates){
            logSelectedCandidate(pickedCandidate);
            logResolvedLocationInfo(pickedCandidate);
        }
        triggerCount+= possibilitiesToRemove.size();
        for (List<ResolvedLocation> toRemove : possibilitiesToRemove) {
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have " + possibilitiesToDo.size() + " lists to do");
    }

    abstract public String getDescription();
    
    abstract protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates);

    /**
     * This version of CLAVIN doesn't appear to fill in the confidence correctly
     * - it says 1.0 for everything. So we need a workaround to see if something
     * is an exact match.
     * 
     * @param candidate
     * @return
     */
    protected boolean isExactMatch(ResolvedLocation candidate) {
        return candidate.geoname.name.equals(candidate.location.text);
        // return candidate.confidence==EXACT_MATCH_CONFIDENCE;
    }

    protected boolean inSameSuperPlace(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.geoname.admin1Code.equals(item.geoname.admin1Code)){
                return true;
            }
        }
        return false;
    }
    protected boolean isCity(ResolvedLocation candidate){
    	return candidate.geoname.population>0 && candidate.geoname.featureClass==FeatureClass.P;
    
    }
    protected boolean isAdminRegion(ResolvedLocation candidate){
    	return candidate.geoname.population>0 && candidate.geoname.featureClass==FeatureClass.A;
    }
    protected ResolvedLocation findFirstCityCandidate(List<ResolvedLocation> candidates){
    	for(ResolvedLocation candidate: candidates) {
            if(isCity(candidate)){
                return candidate;
            }
        }
    	return null; 	
    }
    protected ResolvedLocation findFirstAdminCandidate(List<ResolvedLocation> candidates){
    	for(ResolvedLocation candidate: candidates) {
            if(isAdminRegion(candidate)){
                return candidate;
            }
        }
    	return null; 	
    }
    /* Logic is now to compare the City place with the Admin/State place. 
     * If City has larger population then choose it. If the City and State are in the same country, 
     * then choose the city (this will favor Paris the city over Paris the district in France). 
     * If the City has lower population and is not in same country then choose the state.
     */
    protected boolean chooseCityOverAdmin(ResolvedLocation cityCandidate, ResolvedLocation adminCandidate){
    	if (cityCandidate == null){
    		return false;
    	} else if (adminCandidate == null){
    		return true;
    	} else {
    		return cityCandidate.geoname.population > adminCandidate.geoname.population ||
    			cityCandidate.geoname.primaryCountryCode == adminCandidate.geoname.primaryCountryCode;
    	}
    }
    
	
    protected boolean inSameCountry(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.geoname.primaryCountryCode.equals(item.geoname.primaryCountryCode)){
                return true;
            }
        }
        return false;
    }

    public static void logSelectedCandidate(ResolvedLocation candidate){
        logger.debug("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
    }
    
    public static void logResolvedLocationInfo(ResolvedLocation resolvedLocation){
        GeoName candidatePlace = resolvedLocation.geoname; 
        logger.debug("    "+candidatePlace.geonameID+" "+candidatePlace.name+
                ", "+ candidatePlace.admin1Code+
                ", " + candidatePlace.primaryCountryCode
                + " / "+resolvedLocation.confidence
                +" / "+candidatePlace.population + " / " + candidatePlace.featureClass);
    }

    /**
     * How many times has this pass triggered a disambiguation  
     * @return
     */
    public int getTriggerCount(){
        return triggerCount;
    }
    
}
