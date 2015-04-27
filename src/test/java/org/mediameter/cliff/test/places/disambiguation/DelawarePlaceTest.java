package org.mediameter.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class DelawarePlaceTest {
    
    @Test
    public void testDelawareState() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about Delaware the state.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.STATE_DELAWARE,results.get(0).getGeoname().getGeonameID());
    }

    @Test
    /**
     * Problem case reported by Guy Demeter
     */
    public void test10kReportMention() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("We have holdings in Wilmington,(DE|Delaware)").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 2!",2,results.size());
        assertEquals(TestPlaces.STATE_DELAWARE,results.get(0).getGeoname().getGeonameID());
        assertEquals(TestPlaces.CITY_WILMINGTON_DELAWARE,results.get(1).getGeoname().getGeonameID());
    }    
    
}
