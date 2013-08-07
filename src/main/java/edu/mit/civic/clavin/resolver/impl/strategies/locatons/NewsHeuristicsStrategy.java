package edu.mit.civic.clavin.resolver.impl.strategies.locatons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.Options;
import com.berico.clavin.extractor.CoordinateOccurrence;
import com.berico.clavin.gazetteer.FeatureClass;
import com.berico.clavin.gazetteer.Place;
import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.resolver.impl.LocationCandidateSelectionStrategy;

/**
 * Employ a variety of heuristics for picking the best candidate, based on what might work
 * better for news articles where we care about what _country_ is being report on.
 */
public class NewsHeuristicsStrategy implements LocationCandidateSelectionStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsHeuristicsStrategy.class);
    
    private static final List<Integer> PLACE_ID_BLACKLIST = new ArrayList<Integer>(
                new Integer(3313437)    //???
            );    
    
    /**
     * For each candidate list, select the best candidate.
     * @param allPossibilities Set of candidate lists to sort through.
     * @param cooccurringCoordinates Coordinates that occurred within the document.
     * @param options Options to help configure the optimization strategy.
     * @return Set of the best candidate choices.
     */
    public List<ResolvedLocation> select(
            List<List<ResolvedLocation>> allPossibilities,
            Collection<CoordinateOccurrence<?>> cooccurringCoordinates, 
            Options options)
            throws Exception {
            
        List<ResolvedLocation> bestCandidates = new ArrayList<ResolvedLocation>();
        List<List<ResolvedLocation>> possibilitiesToDo = new ArrayList<List<ResolvedLocation>>();
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>(); 
        
        logger.debug("Starting with "+possibilitiesToDo.size()+" lists to do:");
        // print all of them
        for( List<ResolvedLocation> locationCandidates: allPossibilities){
            ResolvedLocation firstCandidate = locationCandidates.get(0);
            logger.debug("  Location: "+firstCandidate.getLocation().getText()+"@"+firstCandidate.getLocation().getPosition());
            for( ResolvedLocation locationCandidate: locationCandidates){
                logResolvedLocationInfo(locationCandidate);
            }
        }

        logger.debug("Pass 1: Looking for unique exact matches");
        for( List<ResolvedLocation> locationCandidates: allPossibilities){
            List<ResolvedLocation> perfectMatchCandidates = new ArrayList<ResolvedLocation>();
            for( ResolvedLocation locationCandidate: locationCandidates){
                if(locationCandidate.getConfidence()==0.0){
                    perfectMatchCandidates.add(locationCandidate);
                }
            }
            if(perfectMatchCandidates.size()==1){   // ok if the same place shows up twice - we want to know that!
                ResolvedLocation candidate = perfectMatchCandidates.get(0);
                bestCandidates.add(candidate);
                logger.debug("  PICKED: "+candidate.getLocation().getText()+"@"+candidate.getLocation().getPosition());
                logResolvedLocationInfo(candidate);
            } else {
                possibilitiesToDo.add(locationCandidates);
            }
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.debug("Pass 2: Looking for top populated exact match in same countries as best results so far");
        possibilitiesToRemove.clear();
        for( List<ResolvedLocation> locationCandidates: possibilitiesToDo){
            ResolvedLocation firstLocationCandidate = locationCandidates.get(0);
            if(firstLocationCandidate.getConfidence()==0.0 && 
                    firstLocationCandidate.getPlace().getPopulation()>0 && 
                    inSameCountry(firstLocationCandidate, bestCandidates)){
                bestCandidates.add(firstLocationCandidate);
                logger.debug("  PICKED: "+firstLocationCandidate.getLocation().getText()+"@"+firstLocationCandidate.getLocation().getPosition());
                logResolvedLocationInfo(firstLocationCandidate);
                possibilitiesToRemove.add(locationCandidates);
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.debug("Pass 3: Looking for first populated exact matches in same super places as best results so far");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> locationCandidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation locationCandidate: locationCandidates) {
                if(!foundOne && locationCandidate.getConfidence()==0.0 && 
                        locationCandidate.getPlace().getPopulation()>0 && 
                        inSameSuperPlace(locationCandidate, bestCandidates)){
                    bestCandidates.add(locationCandidate);
                    logger.debug("  PICKED: "+locationCandidate.getLocation().getText()+"@"+locationCandidate.getLocation().getPosition());
                    logResolvedLocationInfo(locationCandidate);
                    possibilitiesToRemove.add(locationCandidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");

        logger.debug("Pass 4: Pick countries that might not be an exact match");
        possibilitiesToRemove.clear();
        for( List<ResolvedLocation> locationCandidates: possibilitiesToDo){
            ResolvedLocation firstLocationCandidate = locationCandidates.get(0);
            if(firstLocationCandidate.getPlace().getPopulation()>0 && 
                    firstLocationCandidate.getPlace().getSuperPlaces().get(0).getName().equals("00")){
                bestCandidates.add(firstLocationCandidate);
                logger.debug("  PICKED: "+firstLocationCandidate.getLocation().getText()+"@"+firstLocationCandidate.getLocation().getPosition());
                logResolvedLocationInfo(firstLocationCandidate);
                possibilitiesToRemove.add(locationCandidates);
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");        
        
        logger.debug("Pass 5: Pick the top populated exact match");
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> locationCandidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation locationCandidate: locationCandidates) {
                if(!foundOne && locationCandidate.getConfidence()==0.0 && 
                        locationCandidate.getPlace().getPopulation()>0){
                    bestCandidates.add(locationCandidate);
                    logger.debug("  PICKED: "+locationCandidate.getLocation().getText()+"@"+locationCandidate.getLocation().getPosition());
                    logResolvedLocationInfo(locationCandidate);
                    possibilitiesToRemove.add(locationCandidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");
        
        logger.debug("Pass 6: Pick the top Admin Region of Populated Place");   //TODO
        possibilitiesToRemove.clear(); 
        for( List<ResolvedLocation> locationCandidates: possibilitiesToDo){
            boolean foundOne = false;
            for( ResolvedLocation locationCandidate: locationCandidates) {
                if(!foundOne && 
                        (locationCandidate.getPlace().getFeatureClass()==FeatureClass.A || locationCandidate.getPlace().getFeatureClass()==FeatureClass.P)){
                    bestCandidates.add(locationCandidate);
                    logger.debug("  PICKED: "+locationCandidate.getLocation().getText()+"@"+locationCandidate.getLocation().getPosition());
                    logResolvedLocationInfo(locationCandidate);
                    possibilitiesToRemove.add(locationCandidates);
                    foundOne = true;
                }
            }
        }
        for (List<ResolvedLocation> toRemove: possibilitiesToRemove){
            possibilitiesToDo.remove(toRemove);
        }
        logger.debug("Still have "+possibilitiesToDo.size()+" lists to do");
        
        return bestCandidates;
    }

    private boolean inSameSuperPlace(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.getPlace().getSuperPlaces().get(0).getId().equals(item.getPlace().getSuperPlaces().get(0).getId())){
                return true;
            }
        }
        return false;
    }
    
    private boolean inSameCountry(ResolvedLocation candidate, List<ResolvedLocation> list){
        for( ResolvedLocation item: list){
            if(candidate.getPlace().getPrimaryCountryCode()==item.getPlace().getPrimaryCountryCode()){
                return true;
            }
        }
        return false;
    }
    
    private void logResolvedLocationInfo(ResolvedLocation resolvedLocation){
        Place candidatePlace = resolvedLocation.getPlace(); 
        logger.debug("    "+candidatePlace.getId()+" "+candidatePlace.getName()+
                ", "+ candidatePlace.getSuperPlaces().get(0).getName()+
                ", " + candidatePlace.getPrimaryCountryCode()
                + " / "+resolvedLocation.getConfidence()
                +" / "+candidatePlace.getPopulation() + " / " + candidatePlace.getFeatureClass());
    }
    
    private boolean onPlaceIdBlacklist(int placeId){
        // TODO: make this work
        return PLACE_ID_BLACKLIST.contains(new Integer(placeId));
    }
    
}
