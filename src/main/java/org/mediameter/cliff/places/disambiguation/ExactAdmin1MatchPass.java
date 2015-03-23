package org.mediameter.cliff.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class ExactAdmin1MatchPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            if(containsPopulatedCityExactMatch(candidates)){
                continue;
            }
            ResolvedLocation firstCandidate = candidates.get(0);
            if(isExactMatch(firstCandidate) &&
                    firstCandidate.geoname.population>0 && 
                    firstCandidate.geoname.featureCode.equals(FeatureCode.ADM1)){
                bestCandidates.add(firstCandidate);
                possibilitiesToRemove.add(candidates);
            }
        }
        return possibilitiesToRemove;
    }

    /**
     * Tuned to skip tiny cities that are populated to solve the Oklahoma problem
     * and the Sao Paulo problem.
     * @param candidates
     * @return
     */
    private boolean containsPopulatedCityExactMatch(List<ResolvedLocation> candidates) {
        for(ResolvedLocation loc:candidates){
            if(loc.geoname.population>10000 && isCity(loc) && isExactMatch(loc)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Pick states (Admin1) that are an exact match to help colocation step";
    }
    
}
