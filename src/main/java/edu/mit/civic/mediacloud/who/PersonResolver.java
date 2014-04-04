package edu.mit.civic.mediacloud.who;

import java.util.List;

import edu.mit.civic.mediacloud.extractor.PersonOccurrence;
import edu.mit.civic.mediacloud.who.disambiguation.PersonDisambiguationStrategy;
import edu.mit.civic.mediacloud.who.disambiguation.KindaDumbDisambiguationStrategy;

public class PersonResolver {

    private PersonDisambiguationStrategy disambiguationStrategy;
    
    public PersonResolver(){
        this.disambiguationStrategy = new KindaDumbDisambiguationStrategy();
    }
    
    public List<ResolvedPerson> resolveLocations(List<PersonOccurrence> people){
        return disambiguationStrategy.select(people);
    }
    
}
