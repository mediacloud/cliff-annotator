package org.mediameter.cliff.test.places;

import java.util.List;

import org.mediameter.cliff.places.focus.AboutnessLocation;
import org.mediameter.cliff.test.util.TestUtils;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class CodedArticle {

    public int mediacloudId;
    public String text;
    public String handCodedPlaceName;
    public String handCodedCountryCode;
    
    public boolean isAboutHandCodedCountry(List<AboutnessLocation> primaryCountries){
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
