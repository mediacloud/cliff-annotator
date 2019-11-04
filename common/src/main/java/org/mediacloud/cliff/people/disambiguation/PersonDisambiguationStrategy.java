package org.mediacloud.cliff.people.disambiguation;

import java.util.List;

import org.mediacloud.cliff.DisambiguationStrategy;
import org.mediacloud.cliff.extractor.PersonOccurrence;
import org.mediacloud.cliff.people.ResolvedPerson;

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