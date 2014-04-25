package org.mediameter.orgs.disambiguation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mediameter.extractor.OrganizationOccurrence;
import org.mediameter.orgs.ResolvedOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use simple case-insensitive text matching to match person occurrences
 */
public class RemoveDuplicatesDisambiguationStrategy implements OrganizationDisambiguationStrategy {

    private static final Logger logger = LoggerFactory
            .getLogger(RemoveDuplicatesDisambiguationStrategy.class);

    public RemoveDuplicatesDisambiguationStrategy() {
    }

    @Override
    public List<ResolvedOrganization> select(List<OrganizationOccurrence> allPossibilities) {
        ArrayList<ResolvedOrganization> bestCandidates = new ArrayList<ResolvedOrganization>();
        for(OrganizationOccurrence occurrence: allPossibilities){
        	if (bestCandidates.size() == 0){
        		bestCandidates.add(new ResolvedOrganization(occurrence));
        		continue;
        	}
        	boolean added = false;
        	
        	for(ResolvedOrganization alreadyAdded: bestCandidates){
        		String newDude= occurrence.text.toLowerCase(Locale.US);
        		String oldDude = alreadyAdded.getName().toLowerCase(Locale.US);
        		if (newDude.equals(oldDude)){
        			logger.debug(alreadyAdded.getName() + " is a duplicate of " + occurrence.text);
        			alreadyAdded.addOccurrence(occurrence);
        			added = true;
        			break;
        		}
        	}
        	if (!added){
        		bestCandidates.add(new ResolvedOrganization(occurrence));
        	}
        	
        }
        return bestCandidates;
    }

    @Override
    public void logStats() {
        return;        
    }
}