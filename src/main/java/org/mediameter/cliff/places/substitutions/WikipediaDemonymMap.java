package org.mediameter.cliff.places.substitutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikipediaDemonymMap extends AbstractSubstitutionMap {

    private static final Logger logger = LoggerFactory.getLogger(WikipediaDemonymMap.class);

    public static final String RESOURCE_NAME = "wikipedia-demonyms.tsv";
    
    public WikipediaDemonymMap(){
        this.ignoreCase = false;
        try {
            loadFromFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Unable to load demonym list! "+e);
        }
    }
    
    private void loadFromFile() throws IOException{
        logger.info("Loading demonyms from "+RESOURCE_NAME);
        map = new HashMap<String,String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)));
        // drop the two table header rows
        br.readLine();
        br.readLine();
        // now walk each line
        String row = null;  
        while ((row = br.readLine()) != null) {  
            // parse country demonym info out of line
           String[] columns = row.split("\t");
           String countryName = columns[0];
           String[] adjectivals = columns[1].split(", ");
           ArrayList<String> demonyms = new ArrayList<String>(); 
           for(int c=2;c<columns.length;c++){
               demonyms.addAll( Arrays.asList(columns[c].split(", ")) ); 
           }
           demonyms.addAll( Arrays.asList(adjectivals) );
           // add demonyms to map
           for(String demonym:demonyms){
               put(demonym.trim(), countryName.trim());
               logger.trace("added "+demonym+" to "+countryName);
           }
        }         
    }

    /**
     * HACK: this a a big performance hit
     * @param textToParse
     * @return
     */
    public String replaceAll(String textToParse) {
        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys,Collections.reverseOrder());  // important to do plurals right

        for(String key:keys){
            String value = map.get(key);
            if(textToParse.contains(key)){
                logger.debug("    substituting demonym: "+key+" -> "+value);
                textToParse = textToParse.replaceAll(key, value);
            }            
        }
        return textToParse;
    }

}

