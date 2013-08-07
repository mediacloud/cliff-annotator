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
 
    @Test
    public void testMinnesotaExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/minnesota.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, 5037779));   // has Minnesota
    }

    @Test
    public void testUsExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/speech.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, 5037779));   // has Minnesota
        assertTrue(resultsContainsPlaceId(results, 1816670));   // has city of Beijing
    }

    @Test
    public void testCountryExample() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/multi-country.txt");
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        assertTrue(resultsContainsPlaceId(results, 1814991));   // country of China
        assertTrue(resultsContainsPlaceId(results, 1269750));   // country of India
        assertTrue(resultsContainsPlaceId(results, 2077456));   // country of Australia
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