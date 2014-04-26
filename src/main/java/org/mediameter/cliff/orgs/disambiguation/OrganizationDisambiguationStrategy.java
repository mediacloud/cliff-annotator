package org.mediameter.cliff.orgs.disambiguation;

import java.util.List;

import org.mediameter.cliff.DisambiguationStrategy;
import org.mediameter.cliff.extractor.OrganizationOccurrence;
import org.mediameter.cliff.orgs.ResolvedOrganization;

/**
 * Wrapper around disambiguation strategies, so we can try and compare different ones
 * 
 * @author rahulb
 */
public interface OrganizationDisambiguationStrategy extends DisambiguationStrategy {

    /**
     * For each candidate list, select the best candidate.
     * 
     * @param allPossibilities
     *            Set of candidates to sort through.
     * @return Set of the best candidate choices.
     */
    public abstract List<ResolvedOrganization> select(List<OrganizationOccurrence> allPossibilities);

    public abstract void logStats();
    
}