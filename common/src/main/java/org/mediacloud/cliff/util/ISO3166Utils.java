package org.mediacloud.cliff.util;

import com.neovisionaries.i18n.CountryCode;

/**
 * Helpers to work with ISO3166 country codes and ids.
 *  
 * @author rahulb
 */
public class ISO3166Utils {
    
    public static String alpha3toAlpha2(String alpha3) throws UnknownCountryException {
        if(alpha3.length()==0) return " ";
        CountryCode countryCode = CountryCode.getByCode(alpha3);
        if(null==countryCode){
            throw new UnknownCountryException("Can't find country "+alpha3, alpha3);
        }
        return countryCode.getAlpha2();
    }
}