package edu.mit.civic.clavin;

import java.util.List;

import com.berico.clavin.gazetteer.CountryCode;
import com.berico.clavin.resolver.ResolvedLocation;

public class TestUtils {
    
    public static boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        if(placeId==0) return true;
        for(ResolvedLocation location: results){
            if(location.geoname.geonameID==placeId){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInResolvedLocations(String countryAlpha2, List<ResolvedLocation> results){
        if(countryAlpha2.length()==0) return true;
        for(ResolvedLocation location: results){
            if(location.geoname.primaryCountryCode.toString().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInList(String countryAlpha2, List<CountryCode> countryCodes){
        if(countryAlpha2.length()==0) return true;
        for(CountryCode location: countryCodes){
            if(location.toString().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

}
