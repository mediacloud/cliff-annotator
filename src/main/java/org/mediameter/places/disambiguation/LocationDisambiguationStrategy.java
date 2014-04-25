package org.mediameter.places.disambiguation;

import java.util.List;

import org.mediameter.DisambiguationStrategy;
import org.mediameter.places.CustomLuceneLocationResolver;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Wrapper around disambiguation strategies, so we can try and compare different ones
 * 
 * @author rahulb
 */
public interface LocationDisambiguationStrategy extends DisambiguationStrategy {

    /**
     * For each candidate list, select the best candidate.
     * 
     * @param allPossibilities
     *            Set of candidate lists to sort through.
     * @return Set of the best candidate choices.
     */
    public abstract List<ResolvedLocation> select(
            CustomLuceneLocationResolver resolver,
            List<List<ResolvedLocation>> allPossibilities);

    public abstract void logStats();
    
}