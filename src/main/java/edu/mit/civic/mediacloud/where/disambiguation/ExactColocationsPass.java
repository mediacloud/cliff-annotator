package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ExactColocationsPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();

        possibilitiesToRemove.clear();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates){
                if(!foundOne && isExactMatch(candidate) && 
                    candidate.geoname.population>0 && inSameCountry(candidate, bestCandidates)){
                    bestCandidates.add(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }

        return possibilitiesToRemove;
    }

    @Override
    public String getDescription() {
        return "Looking for top populated exact match in same countries as best results so far";
    }
    
}
