package org.mediameter.cliff.test.gdelt;

public class GdeltActor {

    private String countryCode;
    private String admCode;
    private String latitude;
    private String longitude;
    private String featureId;
    private String type;
    private String fullName;

    public GdeltActor(String cc,String ac,String lat,String lng,String fid,String t,String n){
        this.countryCode = cc;
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
