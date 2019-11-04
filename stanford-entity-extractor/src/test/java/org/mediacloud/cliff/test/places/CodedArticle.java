package org.mediacloud.cliff.test.places;

import java.util.List;

import org.mediacloud.cliff.places.focus.FocusLocation;
import org.mediacloud.cliff.test.util.TestUtils;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class CodedArticle {

    public int mediacloudId;
    public String text;
    public String handCodedPlaceName;
    public String handCodedCountryCode;
    
    public boolean isAboutHandCodedCountry(List<FocusLocation> primaryCountries){
        if(handCodedCountryCode.length()==0 || handCodedCountryCode.equals("None")){  // no places mentioned in article!
            return true;
        } else {
            return TestUtils.isCountryCodeInAboutnessLocationList(handCodedCountryCode, primaryCountries);
        }
    }
    
    public boolean mentionsHandCodedCountry(List<ResolvedLocation> resolvedLocations){
        if(handCodedCountryCode.length()==0){  // no places mentioned in article!
            return true;
        } else {
            return TestUtils.isCountryCodeInResolvedLocations(handCodedCountryCode, resolvedLocations);
        }
    }
    
}
