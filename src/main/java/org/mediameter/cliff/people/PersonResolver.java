package org.mediameter.cliff.people;

import java.util.List;

import org.mediameter.cliff.extractor.PersonOccurrence;
import org.mediameter.cliff.people.disambiguation.KindaDumbDisambiguationStrategy;
import org.mediameter.cliff.people.disambiguation.PersonDisambiguationStrategy;

public class PersonResolver {

    private PersonDisambiguationStrategy disambiguationStrategy;
    
    public PersonResolver(){
        this.disambiguationStrategy = new KindaDumbDisambiguationStrategy();
    }
    
    public List<ResolvedPerson> resolve(List<PersonOccurrence> people){
        return disambiguationStrategy.select(people);
    }
    
}
