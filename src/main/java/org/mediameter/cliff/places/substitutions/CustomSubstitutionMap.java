package org.mediameter.cliff.places.substitutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSubstitutionMap extends AbstractSubstitutionMap {

    private static final Logger logger = LoggerFactory.getLogger(CustomSubstitutionMap.class);
        
    public CustomSubstitutionMap(String fileName){
        try {
            loadFromFile(fileName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Unable to load demonym list! "+e);
        }
    }
    
    private void loadFromFile(String fileName) throws IOException{
        logger.info("Loading custom substitutions from "+fileName);
        map = new HashMap<String,String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));
        // drop the table header rows
        br.readLine();
        // now walk each line
        String row = null;  
        while ((row = br.readLine()) != null) {  
           String[] columns = row.split(",");
           String original = columns[0].toLowerCase().trim();
           String replacement = columns[1].toLowerCase().trim();
           map.put( original, replacement ); 
        }         
        logger.info(this.toString());
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("  " + entry.getKey() + " => " + entry.getValue()+"\n");
        }
        return sb.toString(); 
    }

}

