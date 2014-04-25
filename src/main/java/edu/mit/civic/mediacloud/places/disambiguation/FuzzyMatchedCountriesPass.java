package edu.mit.civic.mediacloud.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class FuzzyMatchedCountriesPass extends GenericPass {

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            ResolvedLocation firstcandidate = candidates.get(0);
            if(firstcandidate.geoname.population>0 && 
                    firstcandidate.geoname.admin1Code.equals("00")){
                bestCandidates.add(firstcandidate);
                possibilitiesToRemove.add(candidates);
            }
        }
        return possibilitiesToRemove;
    }

    @Override
    public String getDescription() {
        return "Pick countries that might not be an exact match";
    }
    
}
