package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopAdminPopulatedPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            
            //Prioritize populated city names first, then prioritize populated states/admin regions
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && (candidate.geoname.population>0) && 
                        candidate.geoname.featureClass==FeatureClass.P){
                    bestCandidates.add(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                    break;
                }
            }
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && (candidate.geoname.population>0) && 
                        candidate.geoname.featureClass==FeatureClass.A){
                    bestCandidates.add(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                    break;
                }
            }
        }

        return possibilitiesToRemove;
    }

    @Override
    public String getDescription() {
        return "Pick the top Admin Region or Populated Place remaining";
    }
    
}
