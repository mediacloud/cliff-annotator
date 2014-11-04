package org.mediameter.cliff.places.focus;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;

public class FocusLocation {

    private int score;
    private GeoName geoName;
    
    public FocusLocation(GeoName geoName, int score){
        this.geoName = geoName;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public GeoName getGeoName() {
        return geoName;
    }
    
    public CountryCode getPrimaryCountryCode(){
        return geoName.getPrimaryCountryCode();
    }
    
}
