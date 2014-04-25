package org.mediameter.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopAdminPopulatedPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        
        /*Logic is now to compare the City place with the Admin/State place. 
         * If City has larger population then choose it. If the City and State are in the same country, 
         * then choose the city (this will favor Paris the city over Paris the district in France). 
         * If the City has lower population and is not in same country then choose the state.
         */
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
        	
            List<ResolvedLocation> exactMatches = getExactMatches(candidates); 
            if(exactMatches.size()>0){
                ResolvedLocation cityCandidate = findFirstCityCandidate(exactMatches,true);
                ResolvedLocation adminCandidate = findFirstAdminCandidate(exactMatches,true);

                if (chooseCityOverAdmin(cityCandidate, adminCandidate)){
                    bestCandidates.add(cityCandidate);
                    possibilitiesToRemove.add(candidates);
                }else if (adminCandidate != null){              
                    bestCandidates.add(adminCandidate);
                    possibilitiesToRemove.add(candidates);
                }
            } else {
            
                ResolvedLocation cityCandidate = findFirstCityCandidate(candidates,false);
                ResolvedLocation adminCandidate = findFirstAdminCandidate(candidates,false);
    
                if (chooseCityOverAdmin(cityCandidate, adminCandidate)){
                	bestCandidates.add(cityCandidate);
                	possibilitiesToRemove.add(candidates);
                }else if (adminCandidate != null){              
                	bestCandidates.add(adminCandidate);
                	possibilitiesToRemove.add(candidates);
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
