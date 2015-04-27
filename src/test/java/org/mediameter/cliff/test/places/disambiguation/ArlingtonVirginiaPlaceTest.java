package org.mediameter.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Problem case reported by Guy Demeter
 */
public class ArlingtonVirginiaPlaceTest {

    public void test10kReportMention() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("I am from Arlington, (VA|Virginia).").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 2!",2,results.size());
        assertEquals(TestPlaces.STATE_VIRGINIA,results.get(0).geoname.geonameID);
        assertEquals(TestPlaces.CITY_ARLINGTON_VIRGINIA,results.get(1).geoname.geonameID);
    }    
    
    @Test
    public void testAbbreviation() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("I am from Arlington, VA.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.STATE_VIRGINIA,results.get(0).geoname.geonameID);
    }
    
}
