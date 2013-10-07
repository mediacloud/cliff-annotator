package edu.mit.civic.clavin.resolver;

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
}
