package org.mediacloud.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediacloud.cliff.ParseManager;
import org.mediacloud.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class BangorMainePlaceTest {
    
    @Test
    public void testBangorMaine() throws Exception{
        List<ResolvedLocation> results = ParseManager.extractAndResolve("Near Bangor, Maine, 75 vehicles got tangled up in a series of chain-reaction pileups on a snowy stretch of Interstate 95, injuring at least 17 people.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 2!",2,results.size());
        assertEquals(TestPlaces.STATE_MAINE,results.get(0).getGeoname().getGeonameID());
        assertEquals(TestPlaces.CITY_BANGOR, results.get(1).getGeoname().getGeonameID());
    }

}
