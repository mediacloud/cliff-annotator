package edu.mit.civic.clavin.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopPreferringColocatedPass extends GenericPass {

    private static final Logger logger = LoggerFactory
            .getLogger(TopPreferringColocatedPass.class);

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
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
            if(!foundOne){
                ResolvedLocation candidate = candidates.get(0);
                bestCandidates.add(candidate);
                logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                logResolvedLocationInfo(candidate);
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
