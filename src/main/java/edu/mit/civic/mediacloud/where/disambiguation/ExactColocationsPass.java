package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ExactColocationsPass extends GenericPass {

    private static final Logger logger = LoggerFactory
            .getLogger(ExactColocationsPass.class);

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
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
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
