package edu.mit.civic.clavin.disambiguation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        triggerCount+= possibilitiesToRemove.size();
        for (List<ResolvedLocation> toRemove : possibilitiesToRemove) {
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have " + possibilitiesToDo.size() + " lists to do");
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
    
    protected boolean inSameCountry(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.geoname.primaryCountryCode.equals(item.geoname.primaryCountryCode)){
                return true;
            }
        }
        return false;
    }

    public static void logResolvedLocationInfo(ResolvedLocation resolvedLocation){
        GeoName candidatePlace = resolvedLocation.geoname; 
        logger.info("    "+candidatePlace.geonameID+" "+candidatePlace.name+
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
