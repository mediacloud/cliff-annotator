package org.mediameter.cliff.places;

public class UnknownGeoNameIdException extends Exception {

    private int geoNameId;
    
    public UnknownGeoNameIdException(int geoNameId){
        this.geoNameId = geoNameId;
    }
    
    public int getGeoNameId(){
        return geoNameId;
    }
    
}
