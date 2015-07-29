package org.mediameter.cliff.util;

public class UnknownCountryException extends Exception {

    private String alpha3;
    
    public UnknownCountryException(String string, String alpha3) {
        this.alpha3 = alpha3;
    }
    
    public String getCountryCode(){
        return this.alpha3;
    }

}
