package org.mediameter.cliff.places.focus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mediameter.cliff.places.Adm1GeoNameLookup;
import org.mediameter.cliff.places.CountryGeoNameLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
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
    public List<AboutnessLocation> selectCountries(List<ResolvedLocation> resolvedLocations){
        List<AboutnessLocation> results = new ArrayList<AboutnessLocation>();
        // count country mentions
        HashMap<CountryCode,Integer> countryCounts = AboutnessUtils.getCountryCounts(resolvedLocations); 
        if(countryCounts.size()==0){
            return results;
        }
        // find the most mentioned
        CountryCode primaryCountry = null;        
        for(CountryCode countryCode: countryCounts.keySet()){
            if( (primaryCountry==null) || (countryCounts.get(countryCode) > countryCounts.get(primaryCountry)) ){
                primaryCountry = countryCode;
            }
        }
        logger.info("Found primary country "+primaryCountry);
        // return results
        if(primaryCountry!=null) {
            results.add( new AboutnessLocation(
                    CountryGeoNameLookup.lookup( primaryCountry.name()),countryCounts.get(primaryCountry) ) 
            );
        	 for(CountryCode countryCode: countryCounts.keySet()){
                if( countryCode != primaryCountry && countryCounts.get(countryCode) == countryCounts.get(primaryCountry) ){
                    results.add( new AboutnessLocation(
                            CountryGeoNameLookup.lookup( countryCode.name()),countryCounts.get(countryCode) ) 
                    );
                } 
            }
        }
        return results;
    }

    @Override
    public List<AboutnessLocation> selectStates(List<ResolvedLocation> resolvedLocations){
        List<AboutnessLocation> results = new ArrayList<AboutnessLocation>();
        // count state mentions
        HashMap<String,Integer> stateCounts = AboutnessUtils.getStateCounts(resolvedLocations);
        if(stateCounts.size()==0){
            return results;
        }
        // find the most mentioned
        String primaryState = null;        
        int highestCount = 0;
        for(String stateCode: stateCounts.keySet()){
            int count = stateCounts.get(stateCode);
            if( (primaryState==null) || count > highestCount ){
            	highestCount = count;
            	primaryState = stateCode;
            }
        }
        logger.info("Found primary state "+primaryState.toString());
        // return results
        if(primaryState!=null) {
            int primaryStateCount = stateCounts.get(primaryState);
        	results.add( new AboutnessLocation( 
        	        Adm1GeoNameLookup.lookup(primaryState), primaryStateCount ) );
        	for(String stateCode: stateCounts.keySet()){
        		int count = stateCounts.get(stateCode);        		
                if( stateCode != primaryState && count == primaryStateCount ){
                    results.add( new AboutnessLocation( 
                            Adm1GeoNameLookup.lookup(stateCode), count ) );
                } 
            }
        }
        return results;
    }
    
    @Override
    public List<AboutnessLocation> selectCities(List<ResolvedLocation> resolvedLocations){
        List<AboutnessLocation> results = new ArrayList<AboutnessLocation>();
        // count state mentions
        HashMap<GeoName,Integer> cityCounts = AboutnessUtils.getCityCounts(resolvedLocations); 
        if(cityCounts.size()==0){
            return results;
        }
        // find the most mentioned
        GeoName primaryCity = null;       
        int highestCount = 0;
        for(GeoName geoname: cityCounts.keySet()){
            int count = cityCounts.get(geoname);
            if( (primaryCity==null) || count > highestCount ){
                highestCount = count;
                primaryCity = geoname;
            }
        }
        logger.info("Found primary city "+primaryCity.toString());
        // return results
        if(primaryCity!=null) {
            int primaryCityCount = cityCounts.get(primaryCity);
            results.add( new AboutnessLocation( primaryCity, primaryCityCount ) );
            for(GeoName city: cityCounts.keySet()){
                int count = cityCounts.get(city);             
                if( (city != primaryCity && count == primaryCityCount ) || ((city != primaryCity && count > 1)) ){
                    results.add( new AboutnessLocation( city, count ) );
                } 
            }
        }
        return results;
    }
    
}
