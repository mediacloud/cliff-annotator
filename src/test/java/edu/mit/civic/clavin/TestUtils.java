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
    
}
