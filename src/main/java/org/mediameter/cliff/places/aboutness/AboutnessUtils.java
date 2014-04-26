package org.mediameter.cliff.places.aboutness;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class AboutnessUtils {

	public static HashMap<ResolvedLocation,Integer> getCityCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<ResolvedLocation,Integer> cityCounts = new HashMap<ResolvedLocation,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.featureClass!=FeatureClass.P){
                continue;
            }
            Set<ResolvedLocation> cityCountKeys = cityCounts.keySet();
            boolean found = false;
           
           
            for (ResolvedLocation cityKey: cityCountKeys){
            	if (cityKey.geoname.geonameID == resolvedLocation.geoname.geonameID){
            		cityCounts.put(cityKey, cityCounts.get(cityKey)+1);
            		System.out.println("Adding count to city " + cityKey.geoname.asciiName + cityCounts.get(cityKey));
            		found=true;
            		break;
            	}
            }
            if(!found){
            	cityCounts.put(resolvedLocation, 1);
            	System.out.println("Adding city " + resolvedLocation.geoname.asciiName);
            }
            
        }
        return cityCounts;
    }

	public static HashMap<String,HashMap<String, String>> getStateCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<String,HashMap<String, String>> stateCounts = new HashMap<String,HashMap<String, String>>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.admin1Code==null){
                continue;
            }
            String stateCode = resolvedLocation.geoname.admin1Code;
           
            
            if(!stateCounts.containsKey(stateCode)){
            	 HashMap<String, String> state = new HashMap<String, String>();
                 state.put("stateCode", stateCode);
                 state.put("countryCode", resolvedLocation.geoname.primaryCountryCode.toString());                 
                 state.put("count", "1");
                 stateCounts.put(stateCode, state);
            } else{
            	HashMap<String, String> state = stateCounts.get(stateCode);
            	int currentCount = Integer.parseInt( state.get("count") );
            	state.put("count", String.valueOf( ++currentCount ));
            	stateCounts.put(stateCode, state);
            }
      
        }
        return stateCounts;
    }
	public static HashMap<CountryCode,Integer> getCountryCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.primaryCountryCode==CountryCode.NULL){
                continue;
            }
            CountryCode country = resolvedLocation.geoname.primaryCountryCode;
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
            if(resolvedLocation.geoname.admin1Code==null){
                continue;
            }
            int position = resolvedLocation.location.position;
            int percent10 = text.length()/10;
            
            int points = 1;
            if( position <= percent10){
            	points = 2;	
            } 
            
            String state = resolvedLocation.geoname.admin1Code;
            if(!stateCounts.containsKey(state)){
            	stateCounts.put(state, 0);
            }
            stateCounts.put(state, stateCounts.get(state)+points);
        }
        return stateCounts;
    }
}
