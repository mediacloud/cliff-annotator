package org.mediacloud.cliff.people;

import java.util.List;

import org.mediacloud.cliff.extractor.PersonOccurrence;
import org.mediacloud.cliff.people.disambiguation.KindaDumbDisambiguationStrategy;
import org.mediacloud.cliff.people.disambiguation.PersonDisambiguationStrategy;

public class PersonResolver {

    private PersonDisambiguationStrategy disambiguationStrategy;
    
    public PersonResolver(){
        this.disambiguationStrategy = new KindaDumbDisambiguationStrategy();
    }
    
    public List<ResolvedPerson> resolve(List<PersonOccurrence> people){
        return disambiguationStrategy.select(people);
    }
    
}
