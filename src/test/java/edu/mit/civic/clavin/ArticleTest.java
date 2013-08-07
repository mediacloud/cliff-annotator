package edu.mit.civic.clavin;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.util.TextUtils;

import edu.mit.civic.clavin.server.ParseManager;

/**
 */
public class ArticleTest {
 
    private static final int STATUE_OF_MINNESOTA = 5037779;
    private static final int CITY_OF_BEIJING = 1816670;
    private static final int COUNTRY_OF_CHINA = 1814991;
    private static final int COUNTRY_OF_INDIA = 1269750;
    private static final int COUNTRY_OF_AUSTRALIA = 2077456;

    @Test
    public void testMinnesotaExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/minnesota.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, STATUE_OF_MINNESOTA));
    }

    @Test
    public void testUsExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/speech.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, STATUE_OF_MINNESOTA));
        assertTrue(resultsContainsPlaceId(results, CITY_OF_BEIJING));
    }

    @Test
    public void testCountryExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/multi-country.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_CHINA));
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_INDIA));
        assertTrue(resultsContainsPlaceId(results, COUNTRY_OF_AUSTRALIA));
    }

    private boolean resultsContainsPlaceId(List<ResolvedLocation> results, int placeId){
        for(ResolvedLocation location: results){
            if(location.getPlace().getId()==placeId){
                return true;
            }
        }
        return false;
    }
    
}