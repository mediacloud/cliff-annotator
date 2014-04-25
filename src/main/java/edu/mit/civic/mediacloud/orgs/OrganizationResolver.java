package edu.mit.civic.mediacloud.orgs;

import java.util.List;

import edu.mit.civic.mediacloud.extractor.OrganizationOccurrence;
import edu.mit.civic.mediacloud.orgs.disambiguate.OrganizationDisambiguationStrategy;
import edu.mit.civic.mediacloud.orgs.disambiguate.RemoveDuplicatesDisambiguationStrategy;

public class OrganizationResolver {

    private OrganizationDisambiguationStrategy disambiguationStrategy;
    
    public OrganizationResolver(){
        this.disambiguationStrategy = new RemoveDuplicatesDisambiguationStrategy();
    }
    
    public List<ResolvedOrganization> resolve(List<OrganizationOccurrence> organizations){
        return disambiguationStrategy.select(organizations);
    }
}
