package org.mediacloud.cliff.orgs;

import java.util.ArrayList;
import java.util.List;

import org.mediacloud.cliff.extractor.OrganizationOccurrence;

public class ResolvedOrganization {

    List<OrganizationOccurrence> occurrences;

    public ResolvedOrganization(OrganizationOccurrence occurrence){
        this.occurrences = new ArrayList<OrganizationOccurrence>();
        this.addOccurrence(occurrence);
    }
   
    public ResolvedOrganization(List<OrganizationOccurrence> occurrences){
        this.occurrences = occurrences;
    }
    
    public void addOccurrence(OrganizationOccurrence occurrence){
        this.occurrences.add(occurrence);
    }
    /*
     * Simple strategy to get the right person name just picks the longest of the set
     */
    public String getName() {
    	String longestName = "";
    	for(OrganizationOccurrence occurrence: occurrences){ 
    		if (occurrence.text.length() > longestName.length()){
    			longestName = occurrence.text;
    		}
    	}
        return longestName;
    }

    public List<OrganizationOccurrence> getOccurrences() {
        return occurrences;
    }

    public Object getOccurenceCount() {
        return occurrences.size();
    }
    
}
