package edu.mit.civic.mediacloud.people;

import java.util.List;

import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.people.disambiguation.KindaDumbDisambiguationStrategy;
import edu.mit.civic.mediacloud.people.disambiguation.PersonDisambiguationStrategy;

public class PersonResolver {

    private PersonDisambiguationStrategy disambiguationStrategy;
    
    public PersonResolver(){
        this.disambiguationStrategy = new KindaDumbDisambiguationStrategy();
    }
    
    public List<ResolvedPerson> resolveLocations(List<PersonOccurrence> people){
        return disambiguationStrategy.select(people);
    }
    
}
