package edu.mit.civic.clavin.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class LargeAreasPass extends GenericPass {

    private static final Logger logger = LoggerFactory
            .getLogger(LargeAreasPass.class);

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        for (List<ResolvedLocation> candidates : possibilitiesToDo) {
            boolean foundOne = false;
            for (ResolvedLocation candidate : candidates) {
                if (!foundOne
                        && isExactMatch(candidate)
                        && candidate.geoname.primaryCountryCode == CountryCode.NULL
                        && candidate.geoname.featureClass == FeatureClass.L) {
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: " + candidate.location.text + "@"
                            + candidate.location.position);
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
        return "Pick large areas";
    }
    
}
