package org.mediacloud.cliff.orgs;

import java.util.List;

import org.mediacloud.cliff.extractor.OrganizationOccurrence;
import org.mediacloud.cliff.orgs.disambiguation.OrganizationDisambiguationStrategy;
import org.mediacloud.cliff.orgs.disambiguation.RemoveDuplicatesDisambiguationStrategy;

public class OrganizationResolver {

    private OrganizationDisambiguationStrategy disambiguationStrategy;
    
    public OrganizationResolver(){
        this.disambiguationStrategy = new RemoveDuplicatesDisambiguationStrategy();
    }
    
    public List<ResolvedOrganization> resolve(List<OrganizationOccurrence> organizations){
        return disambiguationStrategy.select(organizations);
    }
}
