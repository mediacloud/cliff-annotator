package edu.mit.civic.mediacloud.places.aboutness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Once we have selected the candidates, we need to pick what country the document is "about".  This
 * is the most naive "Aboutness" strategy; it just picks the most mentioned country.
 * 
 * @author rahulb
 */
public class FrequencyOfMentionAboutnessStrategy implements AboutnessStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FrequencyOfMentionAboutnessStrategy.class);

    @Override
    public List<CountryCode> selectCountries(List<ResolvedLocation> resolvedLocations){
        // count country mentions
        HashMap<CountryCode,Integer> countryCounts = AboutnessUtils.getCountryCounts(resolvedLocations); 
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
        if(primaryCountry!=null) {
        	results.add(primaryCountry);
        	 for(CountryCode countryCode: countryCounts.keySet()){
            	
                if( countryCode != primaryCountry && countryCounts.get(countryCode) == countryCounts.get(primaryCountry) ){
                	results.add(countryCode);
                } 
            }
        }
        return results;
    }
    public List<HashMap> selectStates(List<ResolvedLocation> resolvedLocations){
        // count country mentions
        HashMap<String,HashMap> stateCounts = AboutnessUtils.getStateCounts(resolvedLocations); 
        // find the most mentioned
        HashMap<String, String> primaryState = null;        
        int highestCount = 0;
        for(String stateCode: stateCounts.keySet()){
        	HashMap<String, String> state = stateCounts.get(stateCode);
        	int count = Integer.valueOf(state.get("count"));
            if( (primaryState==null) || count > highestCount ){
            	highestCount = count;
            	primaryState = state;
            }
        }
        logger.info("Found primary state "+primaryState.toString());
        // return results
        List<HashMap> results = new ArrayList<HashMap>();
        if(primaryState!=null) {
        	results.add(primaryState);
        	int primaryStateCount = Integer.valueOf(primaryState.get("count"));
        	for(String stateCode: stateCounts.keySet()){
        		HashMap<String, String> state = stateCounts.get(stateCode);
        		int count = Integer.valueOf(state.get("count"));
        		
                if( state != primaryState && count == primaryStateCount ){
                	results.add(state);
                } 
            }
        }
        return results;
    }
    public List<ResolvedLocation> selectCities(List<ResolvedLocation> resolvedLocations){
        // count city mentions
        HashMap<ResolvedLocation,Integer> cityCounts = AboutnessUtils.getCityCounts(resolvedLocations); 
        // find the most mentioned
        ResolvedLocation primaryCity = null;   
        for(ResolvedLocation city: cityCounts.keySet()){
            if( (primaryCity==null) || (cityCounts.get(city) > cityCounts.get(primaryCity)) ){
            	primaryCity = city;
            } 
        }
        List<ResolvedLocation> results = new ArrayList<ResolvedLocation>();
        if(primaryCity !=null) {
        	results.add(primaryCity);
        	for(ResolvedLocation city: cityCounts.keySet()){
            	
                if( (city != primaryCity && cityCounts.get(city) == cityCounts.get(primaryCity)) ||
                	(city != primaryCity && cityCounts.get(city) > 1)	){
                	results.add(city);
                } 
            }
        }
        
        logger.info("Found primary city "+primaryCity);
        // return results
        
        return results;
    }
    
}
