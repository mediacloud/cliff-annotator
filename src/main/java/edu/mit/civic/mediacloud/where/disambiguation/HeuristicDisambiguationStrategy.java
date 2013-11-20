package edu.mit.civic.mediacloud.where.disambiguation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

import edu.mit.civic.mediacloud.where.CustomLuceneLocationResolver;

/**
 * Employ a variety of heuristics for picking the best candidate, based on what
 * might work better for news articles where we care about what _country_ is
 * being report on.
 * 
 * This is originally modeled on the common colocation + cooccurance strategy.
 * 
 * Failures I've noticed: Africa Del. "Rocky Mountains" Fla doesn't give you
 * Florida names ("Bristol Palin", "Chad")
 */
public class HeuristicDisambiguationStrategy implements DisambiguationStrategy {

    private static final Logger logger = LoggerFactory
            .getLogger(HeuristicDisambiguationStrategy.class);

    private MultiplePassChain chain;    // keep this around so we can track stats

    public HeuristicDisambiguationStrategy() {
        // set up which passes and the order for disambiguating
        chain = new MultiplePassChain();
        chain.add(new LargeAreasPass());
        chain.add(new FuzzyMatchedCountriesPass());
        chain.add(new ExactColocationsPass());
        chain.add(new TopColocationsPass());
        chain.add(new TopAdminPopulatedPass());
        chain.add(new TopPreferringColocatedPass());
    }

    @Override
    public List<ResolvedLocation> select(CustomLuceneLocationResolver resolver, List<List<ResolvedLocation>> allPossibilities) {
            
        logger.debug("Starting with "+allPossibilities.size()+" lists to do:");
        // print all of them
        for( List<ResolvedLocation> candidates: allPossibilities){
            ResolvedLocation firstCandidate = candidates.get(0);
            logger.debug("  Location: "+firstCandidate.location.text+"@"+firstCandidate.location.position);
            for( ResolvedLocation candidate: candidates){
                GenericPass.logResolvedLocationInfo(candidate);
            }
        }

        // all this does is run the chain we set up already
        List<ResolvedLocation> bestCandidates = chain.disambiguate(allPossibilities);
        
        return bestCandidates;
    }

    @Override
    public void logStats() {
        chain.logPassTriggerStats();        
    }
}