package edu.mit.civic.clavin.resolver.lucene;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.gazetteer.FeatureClass;
import com.berico.clavin.gazetteer.GeoName;
import com.berico.clavin.resolver.ResolvedLocation;

/**
 * Employ a variety of heuristics for picking the best candidate, based on what might work
 * better for news articles where we care about what _country_ is being report on.
 * 
 * This is originally modeled on the common colocation + cooccurance strategy.
 * 
 * Noted Failures: ?
 */
public class NewsHeuristicsStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsHeuristicsStrategy.class);
    
    private static final double EXACT_MATCH_CONFIDENCE = 1.0;
    
    /**
     * For each candidate list, select the best candidate.
     * @param allPossibilities Set of candidate lists to sort through.
     * @return Set of the best candidate choices.
     */
    public static List<ResolvedLocation> select(List<List<ResolvedLocation>> allPossibilities) {
            
        List<ResolvedLocation> bestCandidates = new ArrayList<ResolvedLocation>();
        List<List<ResolvedLocation>> possibilitiesToDo = new ArrayList<List<ResolvedLocation>>();
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>(); 
        
        logger.info("Starting with "+possibilitiesToDo.size()+" lists to do:");
        // print all of them
        for( List<ResolvedLocation> candidates: allPossibilities){
            ResolvedLocation firstCandidate = candidates.get(0);
            logger.info("  Location: "+firstCandidate.location.text+"@"+firstCandidate.location.position);
            for( ResolvedLocation candidate: candidates){
                logResolvedLocationInfo(candidate);
            }
        }

        logger.info("Pass 1: Looking for unique exact matches");
        for( List<ResolvedLocation> candidates: allPossibilities){
            List<ResolvedLocation> perfectMatchCandidates = new ArrayList<ResolvedLocation>();
            for( ResolvedLocation candidate: candidates){
                if(candidate.confidence==EXACT_MATCH_CONFIDENCE && candidate.geoname.population>0 && 
                        (candidate.geoname.featureClass==FeatureClass.A || candidate.geoname.featureClass==FeatureClass.P)){
                    perfectMatchCandidates.add(candidate);
                }
            }
            if(perfectMatchCandidates.size()==1){   // ok if the same place shows up twice - we want to know that!
                ResolvedLocation candidate = perfectMatchCandidates.get(0);
                bestCandidates.add(candidate);
                logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                logResolvedLocationInfo(candidate);
            } else {
                possibilitiesToDo.add(candidates);
            }
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.info("Pass 2: Pick countries that might not be an exact match");
        possibilitiesToRemove.clear();
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
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");        
        
        logger.info("Pass 3: Looking for top populated exact match in same countries as best results so far");
        possibilitiesToRemove.clear();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            ResolvedLocation firstcandidate = candidates.get(0);
            if(firstcandidate.confidence==EXACT_MATCH_CONFIDENCE && 
                    firstcandidate.geoname.population>0 && 
                    inSameCountry(firstcandidate, bestCandidates)){
                bestCandidates.add(firstcandidate);
                logger.info("  PICKED: "+firstcandidate.location.text+"@"+firstcandidate.location.position);
                logResolvedLocationInfo(firstcandidate);
                possibilitiesToRemove.add(candidates);
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.info("Pass 4: Looking for first populated exact matches in same super places as best results so far");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && candidate.confidence==EXACT_MATCH_CONFIDENCE && 
                        candidate.geoname.population>0 && 
                        inSameSuperPlace(candidate, bestCandidates)){
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.info("Pass 5: Pick the top populated exact match"); // this helps us catch countries that are NOT exact matches (ie. China=>Republic of China)
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && candidate.confidence==EXACT_MATCH_CONFIDENCE && 
                        candidate.geoname.population>0){
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");
        
        logger.info("Pass 6: Pick the top Admin Region or Populated Place remaining that is in a country we found already");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && 
                        (candidate.geoname.featureClass==FeatureClass.A || candidate.geoname.featureClass==FeatureClass.P) &&
                        inSameCountry(candidate,bestCandidates)){
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.info("Pass 7: Pick the top Admin Region or Populated Place remaining");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && 
                        (candidate.geoname.featureClass==FeatureClass.A || candidate.geoname.featureClass==FeatureClass.P)){
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.info("Pass 8: Pick the top place remaining that is in the same super place we found already");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation candidate: candidates) {
                if(!foundOne && inSameSuperPlace(candidate,bestCandidates)){
                    bestCandidates.add(candidate);
                    logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");
        
        logger.info("Pass 9: Pick the top result (last ditch effort)");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            ResolvedLocation candidate = candidates.get(0); 
            bestCandidates.add(candidate);
            logger.info("  PICKED: "+candidate.location.text+"@"+candidate.location.position);
                    logResolvedLocationInfo(candidate);
                    possibilitiesToRemove.add(candidates);
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.info("Still have "+possibilitiesToDo.size()+" lists to do");
        
        return bestCandidates;
    }

    static private boolean inSameSuperPlace(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.geoname.admin1Code.equals(item.geoname.admin1Code)){
                return true;
            }
        }
        return false;
    }
    
    static private boolean inSameCountry(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.geoname.primaryCountryCode.equals(item.geoname.primaryCountryCode)){
                return true;
            }
        }
        return false;
    }
    
    static private void logResolvedLocationInfo(ResolvedLocation resolvedLocation){
        GeoName candidatePlace = resolvedLocation.geoname; 
        logger.info("    "+candidatePlace.geonameID+" "+candidatePlace.name+
                ", "+ candidatePlace.admin1Code+
                ", " + candidatePlace.primaryCountryCode
                + " / "+resolvedLocation.confidence
                +" / "+candidatePlace.population + " / " + candidatePlace.featureClass);
    }
        
}