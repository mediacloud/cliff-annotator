package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopPreferringColocatedPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();

        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            // check for one in the same country
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && inSameCountry(candidate,bestCandidates) ){
                    bestCandidates.add(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
            // still nothing? pick the first city
            if(!foundOne){
                for( ResolvedLocation candidate: candidates) {
                    if(!foundOne && isCity(candidate) ){
                        bestCandidates.add(candidate);
                        possibilitiesToRemove.add(candidates);
                        foundOne = true;
                    }
                }    
            }
            // just pick SOMETHING!
            if(!foundOne){
                ResolvedLocation candidate = candidates.get(0);
                logResolvedLocationInfo(candidate);
                bestCandidates.add(candidate);
                possibilitiesToRemove.add(candidates);
            }
        }

        return possibilitiesToRemove;
    }

    @Override
    public String getDescription() {
        return "Pick the top result, preferrring ones in the a country found already (last ditch effort)";
    }
}
