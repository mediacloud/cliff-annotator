package org.mediameter.cliff.places.disambiguation;

import java.util.ArrayList;
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

    //private static final double EXACT_MATCH_CONFIDENCE = 1.0;

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
    static boolean isExactMatch(ResolvedLocation candidate) {
    	//logger.debug(candidate.getGeoname().name + " EQUALS " + candidate.location.text + " ? " + candidate.getGeoname().name.equals(candidate.location.text));
        return candidate.getGeoname().getName().equalsIgnoreCase(candidate.getLocation().getText());
        // return candidate.confidence==EXACT_MATCH_CONFIDENCE;
    }

    static boolean isExactMatchToAdmin1(ResolvedLocation candidate) {
        //logger.debug(candidate.getGeoname().name + " EQUALS " + candidate.location.text + " ? " + candidate.getGeoname().name.equals(candidate.location.text));
        return candidate.getGeoname().getAdmin1Code().equalsIgnoreCase(candidate.getLocation().getText());
        // return candidate.confidence==EXACT_MATCH_CONFIDENCE;
    }

    protected static List<ResolvedLocation> getExactMatches(List<ResolvedLocation> candidates){
        ArrayList<ResolvedLocation> exactMatches = new ArrayList<ResolvedLocation>();
        for( ResolvedLocation item: candidates){
            if(GenericPass.isExactMatch(item)){
                exactMatches.add(item);
            }
        }
        return exactMatches;
    }

    protected static boolean inSameSuperPlace(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.getGeoname().getAdmin1Code().equals(item.getGeoname().getAdmin1Code())){
                return true;
            }
        }
        return false;
    }
    protected static boolean isCity(ResolvedLocation candidate){
    	return candidate.getGeoname().getPopulation()>0 && candidate.getGeoname().getFeatureClass()==FeatureClass.P;
    
    }
    protected static boolean isAdminRegion(ResolvedLocation candidate){
    	return candidate.getGeoname().getPopulation()>0 && candidate.getGeoname().getFeatureClass()==FeatureClass.A;
    }
    protected static boolean isCountry(ResolvedLocation candidate){
        return candidate.getGeoname().getPopulation()>0 && candidate.getGeoname().getAdmin1Code().equals("00");
    }
    protected ResolvedLocation findFirstCityCandidate(List<ResolvedLocation> candidates, boolean exactMatchRequired){
    	for(ResolvedLocation candidate: candidates) {
            if(isCity(candidate)){
            	if (exactMatchRequired && isExactMatch(candidate)){
            		return candidate;
            	} else if (!exactMatchRequired){
            		return candidate;
            	}
            }
        }
    	return null; 	
    }
    
    protected ResolvedLocation findFirstAdminCandidate(List<ResolvedLocation> candidates, boolean exactMatchRequired){
    	for(ResolvedLocation candidate: candidates) {
            if(isAdminRegion(candidate)){
            	if (exactMatchRequired && isExactMatch(candidate)){
            		return candidate;
            	} else if (!exactMatchRequired){
            		return candidate;
            	}
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
    		return (cityCandidate.getGeoname().getPopulation() > adminCandidate.getGeoname().getPopulation()) ||
    			(cityCandidate.getGeoname().getPrimaryCountryCode() == adminCandidate.getGeoname().getPrimaryCountryCode());
    	}
    }
    
    /**
     * Pick all the candidates that are in the same primary country as any of the places already picked
     * @param candidates
     * @param placesAlreadyPicked
     * @return
     */
    protected List<ResolvedLocation> inSameCountry(List<ResolvedLocation> candidates, 
            List<ResolvedLocation> placesAlreadyPicked,
            boolean citiesOnly,boolean exactMatchesOnly,boolean populatedOnly){
        List<ResolvedLocation> candidatesInSameCountry = new ArrayList<ResolvedLocation>();
        for(ResolvedLocation candidate:candidates){
            if(inSameCountry(candidate,placesAlreadyPicked) &&
                    (!citiesOnly || isCity(candidate)) &&
                    (!exactMatchesOnly || isExactMatch(candidate)) && 
                    (!populatedOnly || isPopulated(candidate))
              ){
                candidatesInSameCountry.add(candidate);
            }
        }
        return candidatesInSameCountry;
    }

    protected static boolean isPopulated(ResolvedLocation candidate) {
        return candidate.getGeoname().getPopulation() > 0;
    }

    /**
     * Return if the candidate is in the same primary country as any of the places already picked
     * @param candidate
     * @param placesAlreadyPicked
     * @return
     */
    protected boolean inSameCountry(ResolvedLocation candidate, List<ResolvedLocation> placesAlreadyPicked){
    	
        for( ResolvedLocation selected: placesAlreadyPicked){
            if(inSameCountry(candidate,selected)){
                return true;
            }
        }
        return false;
    }

    protected boolean inSameCountry(ResolvedLocation loc1, ResolvedLocation loc2) {
        return loc1.getGeoname().getPrimaryCountryCode().equals(loc2.getGeoname().getPrimaryCountryCode());
    }

    protected boolean inSameAdmin1(ResolvedLocation candidate, List<ResolvedLocation> list){
        
        for( ResolvedLocation item: list){
            if(candidate.getGeoname().getAdmin1Code().equals(item.getGeoname().getAdmin1Code())){
                return true;
            }
        }
        return false;
    }

    public static void logSelectedCandidate(ResolvedLocation candidate){
        logger.debug("  PICKED: "+candidate.getLocation().getText()+"@"+candidate.getLocation().getPosition());
    }
    
    public static void logResolvedLocationInfo(ResolvedLocation resolvedLocation){
        GeoName candidatePlace = resolvedLocation.getGeoname(); 
        logger.debug("    "+candidatePlace.getGeonameID()+" "+candidatePlace.getName()+
                ", "+ candidatePlace.getAdmin1Code()+
                ", " + candidatePlace.getPrimaryCountryCode()
                + " / "+resolvedLocation.getConfidence()
                +" / "+candidatePlace.getPopulation() + " / " + candidatePlace.getFeatureClass()
                + " ( isExactMatch="+isExactMatch(resolvedLocation)+" )");
    }

    /**
     * How many times has this pass triggered a disambiguation  
     * @return
     */
    public int getTriggerCount(){
        return triggerCount;
    }
    
}
