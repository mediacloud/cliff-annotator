package org.mediameter.people;

import java.util.List;

import org.mediameter.extractor.PersonOccurrence;
import org.mediameter.people.disambiguation.KindaDumbDisambiguationStrategy;
import org.mediameter.people.disambiguation.PersonDisambiguationStrategy;

public class PersonResolver {

    private PersonDisambiguationStrategy disambiguationStrategy;
    
    public PersonResolver(){
        this.disambiguationStrategy = new KindaDumbDisambiguationStrategy();
    }
    
    public List<ResolvedPerson> resolve(List<PersonOccurrence> people){
        return disambiguationStrategy.select(people);
    }
    
}
