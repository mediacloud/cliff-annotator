package org.mediameter.cliff.test.reuters;

import java.util.HashMap;
import java.util.List;

import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.places.substitutions.CustomSubstitutionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;

public class RegionSubstitutionMap extends CustomSubstitutionMap {

    private static final Logger logger = LoggerFactory.getLogger(RegionSubstitutionMap.class);

    private HashMap<String,CountryCode> regionToCountryCode; 
    
    public RegionSubstitutionMap(String fileName) throws Exception{
        super(fileName,"\t",';',false,false);
        createCountryCodeMap();
    }
    
    private void createCountryCodeMap() throws Exception{
        regionToCountryCode = new HashMap<String,CountryCode>();
        for(String region:map.keySet()){
            ExtractedEntities entities = ParseManager.extractAndResolve(map.get(region));
            List<CountryCode> countryCodes = entities.getUniqueCountries();
            if(countryCodes.size()>0){
                regionToCountryCode.put(region,countryCodes.get(0));    // does this make sense?
                logger.debug("  Added country "+countryCodes.get(0)+" found for region "+region);
            } else {
                logger.warn("  No country found for region "+region);
            }
        }
    }

    public CountryCode getCountryCode(String region){
        return regionToCountryCode.get(region);
    }
    
}

