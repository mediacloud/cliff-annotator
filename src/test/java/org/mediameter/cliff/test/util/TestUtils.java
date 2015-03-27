package org.mediameter.cliff.test.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.places.focus.FocusLocation;
import org.mediameter.cliff.test.places.CodedArticle;
import org.slf4j.Logger;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.util.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestUtils {
    
    private static Gson gson = new Gson();

    public static String NYT_JSON_PATH = "src/test/resources/sample-docs/nyt.json";
    public static String HUFFPO_JSON_PATH = "src/test/resources/sample-docs/huffpo.json";
    public static String BBC_JSON_PATH = "src/test/resources/sample-docs/bbc.json";

    public static void verifyPlacesInFile(String pathToFile, int[] places, Logger logger) throws Exception{
        TestUtils.verifyPlacesInFile(pathToFile, places, false,logger);
    }
    
    public static void verifyPlacesInFile(String pathToFile, int[] places, boolean andNoOthers, Logger logger) throws Exception{
        verifyPlacesInFile(pathToFile, places, andNoOthers, logger, false);
    }
    
    public static void verifyPlacesInFile(String pathToFile, int[] places, boolean andNoOthers, Logger logger, boolean replaceDemonyms) throws Exception{
        logger.info("Looking for "+Arrays.toString(places)+" in "+pathToFile);
        File inputFile = new File(pathToFile);
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.extractAndResolve(inputString,replaceDemonyms).getResolvedLocations();
        for(ResolvedLocation resolvedLocation: results){ logger.info("  "+resolvedLocation.toString()); }
        for(int placeId: places){
            assertTrue("Didn't find "+placeId+" in list of places ("+places.length+" places found)",TestUtils.resultsContainsPlaceId(results, placeId));
        }
        if(andNoOthers){
            for(ResolvedLocation resolvedLocation: results){
                assertTrue("Found a place that we weren't supposed to! "+resolvedLocation,intArrayContains(places,resolvedLocation.geoname.geonameID));
            }
        }
    }
    
    public static boolean intArrayContains(int[] array, int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        if(placeId==0) return true;
        for(ResolvedLocation location: results){
            if(location.geoname.geonameID==placeId){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInResolvedLocations(String countryAlpha2, List<ResolvedLocation> results){
        if(countryAlpha2.length()==0) return true;
        for(ResolvedLocation location: results){
            if(location.geoname.primaryCountryCode.toString().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInList(String countryAlpha2, List<CountryCode> countryCodes){
        if(countryAlpha2.length()==0) return true;
        for(CountryCode location: countryCodes){
            if(location.toString().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

    public static boolean isCountryCodeInAboutnessLocationList(String countryAlpha2, List<FocusLocation> countryCodes){
        if(countryAlpha2.length()==0) return true;
        for(FocusLocation location: countryCodes){
            if(location.getPrimaryCountryCode().name().equals(countryAlpha2)){
                return true;
            }
        }
        return false;
    }

    public static List<CodedArticle> loadExamplesFromFile(String filename) throws Exception {
        Type listType = new TypeToken<List<CodedArticle>>() {}.getType();
        String json = FileUtils.readFileToString(new File(filename));
        List<CodedArticle> articles = gson.fromJson(json, listType);
        return articles;
    }

}
