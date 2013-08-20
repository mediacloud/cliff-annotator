package edu.mit.civic.clavin;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.util.TextUtils;

import edu.mit.civic.clavin.server.ParseManager;

/**
 * These out the Strategy we've created for figuring out what country a news story is in.
 */
public class SpecificCaseTest {
 
    private static final Logger logger = LoggerFactory.getLogger(SpecificCaseTest.class);

    private static final int STATE_MINNESOTA = 5037779;
    private static final int CITY_BEIJING = 2038349;
    private static final int COUNTRY_CHINA = 1814991;
    private static final int COUNTRY_INDIA = 1269750;
    private static final int COUNTRY_AUSTRALIA = 2077456;
    private static final int COUNTRY_UK = 2635167;
    private static final int COUNTRY_US = 6252001;
    private static final int REGION_ASIA = 6255147;
    private static final int REGION_MIDDLE_EAST = 6269133;

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
        logger.info("Looking for "+Arrays.toString(places)+" in "+pathToFile);
        File inputFile = new File(pathToFile);
        String inputString = TextUtils.fileToString(inputFile);
        List<ResolvedLocation> results = ParseManager.locateRaw(inputString);
        //for(ResolvedLocation resolvedLocation: results){ logger.info("  "+resolvedLocation.toString()); }
        for(int placeId: places){
            assertTrue(TestUtils.resultsContainsPlaceId(results, placeId));
        }
    }
    
}