package org.mediameter.cliff.test.places;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.util.TextUtils;

/**
 * Tests that verify some very specific cases work
 */
public class SpecificCaseTest {
 
    private static final Logger logger = LoggerFactory.getLogger(SpecificCaseTest.class);

    private static final int STATE_MINNESOTA = 5037779;
    private static final int CITY_BEIJING = 1816670;
    private static final int COUNTRY_CHINA = 1814991;
    private static final int COUNTRY_INDIA = 1269750;
    private static final int COUNTRY_AUSTRALIA = 2077456;
    private static final int COUNTRY_UK = 2635167;
    private static final int COUNTRY_US = 6252001;
    private static final int REGION_ASIA = 6255147;
    private static final int REGION_MIDDLE_EAST = 6269133;
    private static final int CITY_LONDON = 2643743;
    private static final int PLACE_RUSSEL_SQ_LONDON = 6954795;
    
    @Test
    public void testRussellSq() throws Exception {
        // picks the right Russel Sq (the one in GB) after we find London in the article
        verifyPlacesInFile("src/test/resources/sample-docs/russel-sq-london.txt",
                new int[] {CITY_LONDON, PLACE_RUSSEL_SQ_LONDON}, true);
    }
    
    @Test
    public void testMiddleEastExample() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/middle-east.txt", 
                new int[] {REGION_MIDDLE_EAST} );        
    }
    
    @Test
    public void testAsia() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/asia.txt", 
                new int[] {REGION_ASIA} );        
    }

    @Test
    public void testMinnesotaExample() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/minnesota.txt", 
                new int[] {STATE_MINNESOTA} );
        verifyPlacesInFile("src/test/resources/sample-docs/speech.txt", 
                new int[] {STATE_MINNESOTA,CITY_BEIJING} );
    }

    @Test
    public void testUsExample() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/us-story.txt", 
                new int[] {COUNTRY_US} );
    }

    @Test
    public void testUkExample() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/uk-story.txt", 
                new int[] {COUNTRY_UK} );
    }

    @Test
    public void testCountryExample() throws Exception {
        verifyPlacesInFile("src/test/resources/sample-docs/multi-country.txt", 
                new int[] {COUNTRY_CHINA,COUNTRY_INDIA,COUNTRY_AUSTRALIA} );
    }
    
    private void verifyPlacesInFile(String pathToFile, int[] places) throws Exception{
        verifyPlacesInFile(pathToFile, places, false);
    }
    
    private void verifyPlacesInFile(String pathToFile, int[] places, boolean andNoOthers) throws Exception{
        logger.info("Looking for "+Arrays.toString(places)+" in "+pathToFile);
        File inputFile = new File(pathToFile);
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.extractAndResolve(inputString).getResolvedLocations();
        //for(ResolvedLocation resolvedLocation: results){ logger.info("  "+resolvedLocation.toString()); }
        for(int placeId: places){
            assertTrue("Didn't find "+placeId+" in list of places ("+places.length+" places found)",TestUtils.resultsContainsPlaceId(results, placeId));
        }
        if(andNoOthers){
            assertTrue("There are some results that were unexecpted! Found "+results.size()+" but expected "+places.length+".",results.size()==places.length);
        }
    }
    
}