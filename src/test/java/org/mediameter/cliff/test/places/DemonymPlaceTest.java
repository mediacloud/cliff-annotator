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
                new int[] {TestPlaces.PROVINCE_BALI,TestPlaces.COUNTRY_INDONESIA,TestPlaces.COUNTRY_SINGAPORE, TestPlaces.COUNTRY_ENGLAND}, true, logger, true);
    }
    
    @Test
    public void testChinese() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about a Chinese person.", true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_CHINA,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    public void testAustralian() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an Australian person.", true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_AUSTRALIA,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    public void testAmerican() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an American person.", true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_US,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    public void testIndonesian() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an Indonesian person.",true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_INDONESIA,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    public void testEuropean() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about a European person.",true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.CONTINENT_EUROPE,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    public void testAfghanistan() throws Exception{
        List<ResolvedLocation> results;
        results = ParseManager.extractAndResolve("Afghanistan").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_AFGHANISTAN,results.get(0).getGeoname().getGeonameID());
        results = ParseManager.extractAndResolve("Afghanistan",true).getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.COUNTRY_AFGHANISTAN,results.get(0).getGeoname().getGeonameID());
    }

}
