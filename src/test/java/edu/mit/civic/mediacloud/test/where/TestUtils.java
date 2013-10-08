package edu.mit.civic.mediacloud.test.where;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestUtils {
    
    private static Gson gson = new Gson();

    public static String NYT_JSON_PATH = "src/test/resources/sample-docs/nyt.json";
    public static String HUFFPO_JSON_PATH = "src/test/resources/sample-docs/huffpo.json";
    public static String BBC_JSON_PATH = "src/test/resources/sample-docs/bbc.json";
    
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

    public static List<CodedArticle> loadExamplesFromFile(String filename) throws Exception {
        Type listType = new TypeToken<List<CodedArticle>>() {}.getType();
        String json = FileUtils.readFileToString(new File(filename));
        List<CodedArticle> articles = gson.fromJson(json, listType);
        return articles;
    }

}
