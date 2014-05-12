package org.mediameter.cliff.test.gdelt;

import org.mediameter.cliff.util.ISO3166Utils;
import org.mediameter.cliff.util.UnknownCountryException;

import com.bericotech.clavin.gazetteer.CountryCode;

public class GdeltActor {

    private String countryCode;
    private String admCode;
    private String latitude;
    private String longitude;
    private String featureId;
    private String type;
    private String fullName;

    public GdeltActor(String cc,String ac,String lat,String lng,String fid,String t,String n) throws UnknownCountryException{
        this.countryCode = ISO3166Utils.alpha3toAlpha2(cc);
        this.admCode = ac;
        this.latitude = lat;
        this.longitude = lng;
        this.featureId = fid;
        this.type = t;
        this.fullName = n;
    }
    
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public CountryCode getCountryCodeObj(){
        return CountryCode.valueOf(this.countryCode);
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getAdmCode() {
        return admCode;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getFeatureId() {
        return featureId;
    }
    public String getType() {
        return type;
    }
    public String getFullName() {
        return fullName;
    }
    
}
