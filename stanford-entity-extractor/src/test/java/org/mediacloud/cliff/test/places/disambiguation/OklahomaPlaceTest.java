package org.mediacloud.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediacloud.cliff.ParseManager;
import org.mediacloud.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class OklahomaPlaceTest {
    
    @Test
    public void testOklahoma() throws Exception {
        List<ResolvedLocation> results = ParseManager.extractAndResolve("Oklahoma say Common Core tests are too costly.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.STATE_OKLAHOMA, results.get(0).getGeoname().getGeonameID());
    }

}
