package edu.mit.civic.clavin;

import java.util.List;

import com.berico.clavin.resolver.ResolvedLocation;

public class TestUtils {
    
    public static boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        for(ResolvedLocation location: results){
            if(location.geoname.geonameID==placeId){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInResolvedLocations(List<ResolvedLocation> results, String countryAlpha2){
        for(ResolvedLocation location: results){
            if(location.geoname.primaryCountryCode.toString().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

}
