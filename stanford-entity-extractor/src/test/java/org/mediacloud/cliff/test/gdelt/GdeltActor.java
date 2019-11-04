package org.mediacloud.cliff.test.gdelt;

import org.mediacloud.cliff.util.ISO3166Utils;
import org.mediacloud.cliff.util.UnknownCountryException;

import com.bericotech.clavin.gazetteer.CountryCode;

public class GdeltActor {

    private String countryCode;

    public GdeltActor(String cc) throws UnknownCountryException{
        this.countryCode = ISO3166Utils.alpha3toAlpha2(cc);
    }
    
    public CountryCode getCountryCodeObj(){
        return CountryCode.valueOf(this.countryCode);
    }
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString(){
       return this.getCountryCodeObj().toString(); 
    }
    
}
