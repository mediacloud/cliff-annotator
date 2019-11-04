package org.mediacloud.cliff.extractor;

import com.bericotech.clavin.extractor.LocationOccurrence;

public class SentenceLocationOccurrence extends LocationOccurrence{
    
    public final String storySentenceId;    // too big to store as a number
    
    public SentenceLocationOccurrence(String text,String sentenceId) {
        super(text,0);
        this.storySentenceId= sentenceId; 
    }

}