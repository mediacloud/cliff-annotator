package edu.mit.civic.mediacloud.where.aboutness;

import java.util.HashMap;
import java.util.List;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class AboutnessUtils {

    public static HashMap<CountryCode,Integer> getCountryCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.primaryCountryCode==CountryCode.NULL){
                continue;
            }
            CountryCode country = resolvedLocation.geoname.primaryCountryCode;
            if(!countryCounts.containsKey(country)){
                countryCounts.put(country, 0);
            }
            countryCounts.put(country, countryCounts.get(country)+1);
        }
        return countryCounts;
    }
    public static HashMap<CountryCode,Integer> getScoredCountryCounts(List<ResolvedLocation> resolvedLocations, String text){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        
        //This is a rough sentence parsing hack to deal with test data - doesn't take into account !? - re-do once we have MediaCloud sentences again
        //For testing different approaches to this strategy
        int headlineAndFirstSentenceIdx = text.indexOf('.');
        int secondSentenceIdx = text.indexOf('.', headlineAndFirstSentenceIdx);
        int thirdSentenceIdx = text.indexOf('.', secondSentenceIdx);
        int fourthSentenceIdx = text.indexOf('.', thirdSentenceIdx);
        int fifthSentenceIdx = text.indexOf('.', fourthSentenceIdx);
        
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.primaryCountryCode==CountryCode.NULL){
                continue;
            }
            int position = resolvedLocation.location.position;
            int percent10 = text.length()/10;
            
            int points = 1;
            if( position <= percent10){
            	points = 2;	
            } 
            
            CountryCode country = resolvedLocation.geoname.primaryCountryCode;
            if(!countryCounts.containsKey(country)){
                countryCounts.put(country, 0);
            }
            countryCounts.put(country, countryCounts.get(country)+points);
        }
        return countryCounts;
    }
}
