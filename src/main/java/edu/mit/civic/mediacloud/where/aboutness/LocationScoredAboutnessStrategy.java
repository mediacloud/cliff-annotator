package edu.mit.civic.mediacloud.where.aboutness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Once we have selected the candidates, we need to pick what country the document is "about".  This
 * strategy scores ResolvedLocaations by their location in the text. Headline or first sentence = 3,
 * second or third sentence = 2, and any other mention = 1x
 * 
 * @author rahulb
 */
@Deprecated
public class LocationScoredAboutnessStrategy implements AboutnessStrategy {

    private static final Logger logger = LoggerFactory.getLogger(LocationScoredAboutnessStrategy.class);

    @Override
    public List<CountryCode> selectCountries(List<ResolvedLocation> resolvedLocations, String text){
        // count country mentions
        HashMap<CountryCode,Integer> countryCounts = AboutnessUtils.getScoredCountryCounts(resolvedLocations, text); 
        // find the most mentioned
        CountryCode primaryCountry = null;        
        for(CountryCode countryCode: countryCounts.keySet()){
            if( (primaryCountry==null) || (countryCounts.get(countryCode) > countryCounts.get(primaryCountry)) ){
                primaryCountry = countryCode;
            }
        }
        logger.info("Found primary country "+primaryCountry);
        // return results
        List<CountryCode> results = new ArrayList<CountryCode>();
        if(primaryCountry!=null) results.add(primaryCountry);
        return results;
    }
    
    public List<String> selectStates(List<ResolvedLocation> resolvedLocations, String text){
        // count country mentions
        HashMap<String,Integer> stateCounts = AboutnessUtils.getScoredStateCounts(resolvedLocations, text); 
        // find the most mentioned
        String primaryState = null;        
        for(String stateCode: stateCounts.keySet()){
            if( (primaryState==null) || (stateCounts.get(stateCode) > stateCounts.get(primaryState)) ){
            	primaryState = stateCode;
            }
        }
        logger.info("Found primary country "+primaryState);
        // return results
        List<String> results = new ArrayList<String>();
        if(primaryState!=null) results.add(primaryState);
        return results;
    }
    //not implemented
    public List<String> selectCities(List<ResolvedLocation> resolvedLocations, String text){
    	return null;
    }
    
}
