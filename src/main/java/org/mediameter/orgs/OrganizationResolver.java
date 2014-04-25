package org.mediameter.orgs;

import java.util.List;

import org.mediameter.extractor.OrganizationOccurrence;
import org.mediameter.orgs.disambiguation.OrganizationDisambiguationStrategy;
import org.mediameter.orgs.disambiguation.RemoveDuplicatesDisambiguationStrategy;

public class OrganizationResolver {

    private OrganizationDisambiguationStrategy disambiguationStrategy;
    
    public OrganizationResolver(){
        this.disambiguationStrategy = new RemoveDuplicatesDisambiguationStrategy();
    }
    
    public List<ResolvedOrganization> resolve(List<OrganizationOccurrence> organizations){
        return disambiguationStrategy.select(organizations);
    }
}
