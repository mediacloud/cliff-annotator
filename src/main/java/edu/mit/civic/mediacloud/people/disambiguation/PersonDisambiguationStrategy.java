package edu.mit.civic.mediacloud.people.disambiguation;

import java.util.List;

import edu.mit.civic.mediacloud.DisambiguationStrategy;
import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.people.ResolvedPerson;

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