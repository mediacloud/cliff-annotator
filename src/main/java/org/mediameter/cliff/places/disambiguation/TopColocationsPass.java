package org.mediameter.cliff.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopColocationsPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();

        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && 
                        (candidate.getGeoname().getFeatureClass()==FeatureClass.A || candidate.getGeoname().getFeatureClass()==FeatureClass.P) &&
                        inSameCountry(candidate,bestCandidates)){
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
        return "Pick the top Admin Region or Populated Place remaining that is in a country we found already";
    }
}
