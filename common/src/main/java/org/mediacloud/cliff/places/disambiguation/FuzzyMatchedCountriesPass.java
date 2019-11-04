package org.mediacloud.cliff.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class FuzzyMatchedCountriesPass extends GenericPass {

    //private static final Logger logger = LoggerFactory.getLogger(FuzzyMatchedCountriesPass.class);

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            ResolvedLocation countryCandidate = findFirstCountryCandidate(candidates,false);
            if (countryCandidate!=null){
                bestCandidates.add(countryCandidate);
                possibilitiesToRemove.add(candidates);
            }
        }
        return possibilitiesToRemove;
    }

    /**
     * Find the first country after any Hypsographic Features (T)
     * @param candidates
     * @param exactMatchRequired
     * @return
     */
    protected ResolvedLocation findFirstCountryCandidate(List<ResolvedLocation> candidates, boolean exactMatchRequired){
        boolean keepGoing = true;
        for(ResolvedLocation candidate: candidates) {
            if(keepGoing){
                if(candidate.getGeoname().getFeatureClass().equals(FeatureClass.T)){
                    // skip large territories that appear ahead of countries in results (ie. Indian Subcontinent!)
                    continue;
                }
                if(isCountry(candidate)){
                    if (exactMatchRequired && isExactMatch(candidate)){
                        return candidate;
                    } else if (!exactMatchRequired){
                        return candidate;
                    }                 
                } else{
                    keepGoing = false;
                }
            }
        }
        return null;    
    }
    
    @Override
    public String getDescription() {
        return "Pick countries that might not be an exact match";
    }
    
}