package org.mediacloud.cliff.places.substitutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blacklist {

    private static final Logger logger = LoggerFactory.getLogger(Blacklist.class);
    
    private ArrayList<String> list; 
    
    public Blacklist(String fileName){
        try {
            loadFromFile(fileName);
        } catch (IOException e) {
            logger.error("Unable to load blacklist! "+e);
        }
    }
    
    protected void loadFromFile(String fileName) throws IOException{
        logger.info("Loading blacklist from "+fileName);
        list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));
        // now walk each line
        String row = null;  
        while ((row = br.readLine()) != null) {
            if(row.length()==0) continue;
            list.add(row.trim().toLowerCase()); 
        }
        logger.trace(this.toString());
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (String item:list) {
            sb.append("  " + item+"\n");
        }
        return sb.toString(); 
    }

    public boolean contains(String str) {
        return list.contains(str.toLowerCase());
    }

}

