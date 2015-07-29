package org.mediameter.cliff.people.disambiguation;

import java.util.List;

import org.mediameter.cliff.DisambiguationStrategy;
import org.mediameter.cliff.extractor.PersonOccurrence;
import org.mediameter.cliff.people.ResolvedPerson;

/**
 * Wrapper around disambiguation strategies, so we can try and compare different ones
 * 
 * @author rahulb
 */
public interface PersonDisambiguationStrategy extends DisambiguationStrategy {

    /**
     * For each candidate list, select the best candidate.
     * 
     * @param allPossibilities
     *            Set of candidates to sort through.
     * @return Set of the best candidate choices.
     */
    public abstract List<ResolvedPerson> select(List<PersonOccurrence> allPossibilities);

    public abstract void logStats();
    
}