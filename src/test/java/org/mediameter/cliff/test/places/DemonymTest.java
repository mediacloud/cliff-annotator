package org.mediameter.cliff.test.places;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Tests that verify some demonym-tests
 */
public class DemonymTest {
 
    private static final int COUNTRY_CHINA = 1814991;
    private static final int COUNTRY_AUSTRALIA = 2077456;
    private static final int COUNTRY_INDONESIA = 1643084;
    private static final int COUNTRY_US = 6252001;

    @Test
    public void testChinese(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about a Chinese person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(COUNTRY_CHINA,results.get(0).geoname.geonameID);
    }

    @Test
    public void testAmerican(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an American person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(COUNTRY_US,results.get(0).geoname.geonameID);
    }

    @Test
    public void testIndonesian(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about an Indonesian person.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(COUNTRY_INDONESIA,results.get(0).geoname.geonameID);
    }
    
}