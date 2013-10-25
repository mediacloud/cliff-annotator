package edu.mit.civic.mediacloud.test.where;

import java.util.List;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class CodedArticle {

    public int mediacloudId;
    public String text;
    public String handCodedPlaceName;
    public String handCodedCountryCode;
    
    public boolean isAboutHandCodedCountry(List<CountryCode> primaryCountries){
        if(handCodedCountryCode.length()==0 || handCodedCountryCode.equals("None")){  // no places mentioned in article!
            return true;
        } else {
            return TestUtils.isCountryCodeInList(handCodedCountryCode, primaryCountries);
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
