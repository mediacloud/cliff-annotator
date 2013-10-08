package edu.mit.civic.clavin.disambiguation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

import edu.mit.civic.clavin.resolver.lucene.CustomLuceneLocationResolver;

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

    private MultiplePassChain chain;

    public HeuristicDisambiguationStrategy() {
        chain = new MultiplePassChain();
        chain.add(new LargeAreasPass());
        chain.add(new FuzzyMatchedCountriesPass());
        chain.add(new ExactColocationsPass());
        chain.add(new TopColocationsPass());
        chain.add(new TopAdminPopulatedPass());
        chain.add(new TopPreferringColocatedPass());
    }

    /* (non-Javadoc)
     * @see edu.mit.civic.clavin.disambiguation.DisambiguationStrategy#select(edu.mit.civic.clavin.resolver.lucene.CustomLuceneLocationResolver, java.util.List)
     */
    @Override
    public List<ResolvedLocation> select(CustomLuceneLocationResolver resolver, List<List<ResolvedLocation>> allPossibilities) {
            
        logger.info("Starting with "+allPossibilities.size()+" lists to do:");
        // print all of them
        for( List<ResolvedLocation> candidates: allPossibilities){
            ResolvedLocation firstCandidate = candidates.get(0);
            logger.info("  Location: "+firstCandidate.location.text+"@"+firstCandidate.location.position);
            for( ResolvedLocation candidate: candidates){
                GenericPass.logResolvedLocationInfo(candidate);
            }
        }
        
        List<ResolvedLocation> bestCandidates = chain.disambiguate(allPossibilities);
        
        return bestCandidates;
    }

    @Override
    public void logStats() {
        chain.logPassTriggerStats();        
    }
}