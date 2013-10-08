package edu.mit.civic.clavin.disambiguation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class TopAdminPopulatedPass extends GenericPass {

    private static final Logger logger = LoggerFactory
            .getLogger(TopAdminPopulatedPass.class);

    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && (candidate.geoname.population>0) && 
                        (candidate.geoname.featureClass==FeatureClass.A || candidate.geoname.featureClass==FeatureClass.P)){
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
        return "Pick the top Admin Region or Populated Place remaining";
    }
    
}
