package org.mediameter.cliff.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ExactColocationsPass extends GenericPass {

    private static final Logger logger = LoggerFactory.getLogger(ExactColocationsPass.class);

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        possibilitiesToRemove.clear();

        if(bestCandidates.size() == 0){
            return possibilitiesToRemove;
        }
        
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            ResolvedLocation candidateToPick = null;
            List<ResolvedLocation> colocatedExactCityCandidates = inSameCountry(candidates, bestCandidates,true,true,true);
            logger.debug("  Found "+colocatedExactCityCandidates.size()+" colocations");
            if(colocatedExactCityCandidates.size()==1){
                candidateToPick = colocatedExactCityCandidates.get(0);
            }else if (colocatedExactCityCandidates.size()>1){
                List<ResolvedLocation> shareCountryAndAdm1 = inSameCountryAndAdm1(colocatedExactCityCandidates,bestCandidates);
                if(shareCountryAndAdm1.size()>0){
                    candidateToPick = shareCountryAndAdm1.get(0);
                } else {
                    candidateToPick = colocatedExactCityCandidates.get(0);
                }
            }
            if(candidateToPick!=null){
                logger.debug("  "+candidateToPick.geoname.geonameID+"  "+candidateToPick.geoname.name + " is in "+candidateToPick.geoname.primaryCountryCode);
                bestCandidates.add(candidateToPick);
                possibilitiesToRemove.add(candidates);
            }
        }

        return possibilitiesToRemove;
    }

    /**
     * 
     * @param colocatedExactCityCandidates
     * @param alreadyPicked
     * @return
     */
    private List<ResolvedLocation> inSameCountryAndAdm1(
            List<ResolvedLocation> candidates,
            List<ResolvedLocation> alreadyPicked) {
        List<ResolvedLocation> colocations = new ArrayList<ResolvedLocation>();
        for(ResolvedLocation pickedLocation:alreadyPicked){
            for(ResolvedLocation candidate:candidates){
                if(isSameCountryAndAdm1(candidate, pickedLocation)){
                    colocations.add(candidate);
                }
            }
        }
        return colocations;
    }

    private boolean isSameCountryAndAdm1(ResolvedLocation place1, ResolvedLocation place2) {
        return place1.geoname.admin1Code.equals(place2.geoname.admin1Code);
    }

    @Override
    public String getDescription() {
        return "Looking for top populated city exact match in same countries/states as best results so far";
    }
    
}
