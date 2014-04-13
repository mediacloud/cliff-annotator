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
    /*
     * Simple strategy to get the right person name just picks the longest of the set
     */
    public String getName() {
    	String longestName = "";
    	for(PersonOccurrence occurrence: occurrences){ 
    		if (occurrence.text.length() > longestName.length()){
    			longestName = occurrence.text;
    		}
    	}
        return longestName;
    }

    public List<PersonOccurrence> getOccurrences() {
        return occurrences;
    }

    public Object getOccurenceCount() {
        return occurrences.size();
    }
    
}
