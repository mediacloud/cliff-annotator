package edu.mit.civic.clavin.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class FuzzyMatchedCountriesPass extends GenericPass {

    private static final Logger logger = LoggerFactory
            .getLogger(FuzzyMatchedCountriesPass.class);

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
                logger.info("  PICKED: "+firstcandidate.location.text+"@"+firstcandidate.location.position);
                logResolvedLocationInfo(firstcandidate);    
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
