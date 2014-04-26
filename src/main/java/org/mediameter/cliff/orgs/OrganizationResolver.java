package org.mediameter.cliff.orgs;

import java.util.List;

import org.mediameter.cliff.extractor.OrganizationOccurrence;
import org.mediameter.cliff.orgs.disambiguation.OrganizationDisambiguationStrategy;
import org.mediameter.cliff.orgs.disambiguation.RemoveDuplicatesDisambiguationStrategy;

public class OrganizationResolver {

    private OrganizationDisambiguationStrategy disambiguationStrategy;
    
    public OrganizationResolver(){
        this.disambiguationStrategy = new RemoveDuplicatesDisambiguationStrategy();
    }
    
    public List<ResolvedOrganization> resolve(List<OrganizationOccurrence> organizations){
        return disambiguationStrategy.select(organizations);
    }
}
