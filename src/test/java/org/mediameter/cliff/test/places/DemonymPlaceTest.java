package org.mediameter.cliff.test.places;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class DemonymPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DemonymPlaceTest.class);

    @Test
    public void testDemonymInArticle() throws Exception{
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/demonym-article.txt",
                new int[] {TestPlaces.PROVINCE_BALI,TestPlaces.COUNTRY_INDONESIA,TestPlaces.COUNTRY_SINGAPORE}, true, logger);
    }
    
    @Test
    public void testChinese(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about a Chinese person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_CHINA,results.get(0).geoname.geonameID);
    }

    @Test
    public void testAustralian(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an Australian person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_AUSTRALIA,results.get(0).geoname.geonameID);
    }

    @Test
    public void testAmerican(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an American person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_US,results.get(0).geoname.geonameID);
    }

    @Test
    public void testIndonesian(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an Indonesian person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_INDONESIA,results.get(0).geoname.geonameID);
    }

}
