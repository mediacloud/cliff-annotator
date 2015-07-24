package org.mediameter.cliff.places.substitutions;

import java.util.HashMap;

public abstract class AbstractSubstitutionMap {

    protected HashMap<String,String> map;
    protected boolean ignoreCase = true;
        
    public int getSize(){
        return map.keySet().size();
    }
    
    protected void put(String key, String value){
        String keyToUse = (ignoreCase) ? key.toLowerCase() : key;
        String valueToUse = (ignoreCase) ? value.toLowerCase() : value;
        map.put(keyToUse.trim(), valueToUse.trim());
    }
    
    public boolean contains(String candidate){
        String key = (ignoreCase) ? candidate.toLowerCase() : candidate; 
        return map.keySet().contains(key);
    }

    public String getSubstitution(String candidate) {
        String key = (ignoreCase) ? candidate.toLowerCase() : candidate;
        return map.get(key);
    }
    
    public String substituteIfNeeded(String candidate){
        if (contains(candidate)){
            return getSubstitution(candidate);
        }
        return candidate;
    }
        
}