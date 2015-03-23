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
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates){
                if(!foundOne && isExactMatch(candidate) && 
                		candidate.geoname.population>0 && 
                		(bestCandidates.size() == 0 || 
                			(bestCandidates.size() != 0 && inSameCountry(candidate, bestCandidates)))){
                    logger.trace("  "+candidate.geoname.geonameID+"  "+candidate.geoname.name + " is in "+candidate.geoname.primaryCountryCode);
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
