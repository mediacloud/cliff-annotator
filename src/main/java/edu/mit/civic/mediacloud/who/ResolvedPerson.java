package edu.mit.civic.mediacloud.who;

import java.util.ArrayList;
import java.util.List;

import edu.mit.civic.mediacloud.extractor.PersonOccurrence;

public class ResolvedPerson {

    List<PersonOccurrence> occurrences;

    public ResolvedPerson(PersonOccurrence occurrence){
        this.occurrences = new ArrayList<PersonOccurrence>();
        this.addOccurrence(occurrence);
    }
   
    public ResolvedPerson(List<PersonOccurrence> occurrences){
        this.occurrences = occurrences;
    }
    
    public void addOccurrence(PersonOccurrence occurrence){
        this.occurrences.add(occurrence);
    }
    
    public String getName() {
        return occurrences.get(0).text;
    }

    public List<PersonOccurrence> getOccurrences() {
        return occurrences;
    }

    public Object getOccurenceCount() {
        return occurrences.size();
    }
    
}
