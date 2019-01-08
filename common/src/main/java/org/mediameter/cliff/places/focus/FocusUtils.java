package org.mediameter.cliff.places.focus;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mediameter.cliff.places.Adm1GeoNameLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class FocusUtils {

    private static final Logger logger = LoggerFactory.getLogger(FocusUtils.class);

	public static HashMap<GeoName,Integer> getCityCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<GeoName,Integer> cityCounts = new HashMap<GeoName,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.getGeoname().getFeatureClass()!=FeatureClass.P){
                continue;
            }
            Set<GeoName> cityCountKeys = cityCounts.keySet();
            boolean found = false;
           
           
            for (GeoName geoname: cityCountKeys){
            	if (geoname.getGeonameID() == resolvedLocation.getGeoname().getGeonameID()){
            		cityCounts.put(geoname, cityCounts.get(geoname)+1);
            		logger.debug("Adding count to city " + geoname.getAsciiName() + cityCounts.get(geoname));
            		found=true;
            		break;
            	}
            }
            if(!found){
            	cityCounts.put(resolvedLocation.getGeoname(), 1);
            	logger.debug("Adding city " + resolvedLocation.getGeoname().getAsciiName());
            }
            
        }
        return cityCounts;
    }

	public static HashMap<String,Integer> getStateCounts(List<ResolvedLocation> resolvedLocations){     
	    HashMap<String,Integer> stateCounts = new HashMap<String,Integer>();
	    for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.getGeoname().getPrimaryCountryCode()==CountryCode.NULL){
                continue;
            }
            CountryCode country = resolvedLocation.getGeoname().getPrimaryCountryCode();
            String adm1Code = resolvedLocation.getGeoname().getAdmin1Code();
            String key = Adm1GeoNameLookup.getKey(country, adm1Code);
            if(!Adm1GeoNameLookup.isValid(key)){    // skip things that aren't actually ADM1 codes
                continue;
            }
            if(!stateCounts.containsKey(key)){
                stateCounts.put(key, 0);
            }
            stateCounts.put(key, stateCounts.get(key)+1);
        }
        return stateCounts;
    }

	public static HashMap<CountryCode,Integer> getCountryCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.getGeoname().getPrimaryCountryCode()==CountryCode.NULL){
                continue;
            }
            CountryCode country = resolvedLocation.getGeoname().getPrimaryCountryCode();
            if(!countryCounts.containsKey(country)){
                countryCounts.put(country, 0);
            }
            countryCounts.put(country, countryCounts.get(country)+1);
        }
        return countryCounts;
    }
	
    public static HashMap<String,Integer> getScoredStateCounts(List<ResolvedLocation> resolvedLocations, String text){     
        HashMap<String,Integer> stateCounts = new HashMap<String,Integer>();
        
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.getGeoname().getAdmin1Code()==null){
                continue;
            }
            int position = resolvedLocation.getLocation().getPosition();
            int percent10 = text.length()/10;
            
            int points = 1;
            if( position <= percent10){
            	points = 2;	
            } 
            
            String state = resolvedLocation.getGeoname().getAdmin1Code();
            if(!stateCounts.containsKey(state)){
            	stateCounts.put(state, 0);
            }
            stateCounts.put(state, stateCounts.get(state)+points);
        }
        return stateCounts;
    }
}
