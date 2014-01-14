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
            boolean foundCity = false;
            boolean foundAdmin = false;
            
            ResolvedLocation cityCandidate = null;
            ResolvedLocation adminCandidate = null;
            
            
            for( ResolvedLocation candidate: candidates) {
                if(!foundCity && (candidate.geoname.population>0) && 
                        candidate.geoname.featureClass==FeatureClass.P){
                    
                    cityCandidate = candidate;
                    foundCity = true;
                    break;
                }
            }
            for( ResolvedLocation candidate: candidates) {
                if(!foundAdmin && (candidate.geoname.population>0) && 
                        candidate.geoname.featureClass==FeatureClass.A){
                	adminCandidate = candidate;
                    if (foundCity && 
                    	(cityCandidate.geoname.population > adminCandidate.geoname.population ||
                    		cityCandidate.geoname.primaryCountryCode == adminCandidate.geoname.primaryCountryCode)){
                    	bestCandidates.add(cityCandidate);
                    	possibilitiesToRemove.add(candidates);
                    }else{              
                    	bestCandidates.add(adminCandidate);
                    	possibilitiesToRemove.add(candidates);
                    }
                   
                    foundAdmin = true;
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
