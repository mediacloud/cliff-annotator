package org.mediameter.cliff.test.places.disambiguation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.util.TestPlaces;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class AmazonPlaceTest {
    
    @Test
    public void testAmazon(){
        List<ResolvedLocation> results = ParseManager.extractAndResolve("This is about the Amazon.").getResolvedLocations();
        assertEquals("Found "+results.size()+" places, should have been 1!",1,results.size());
        assertEquals(TestPlaces.AMAZON,results.get(0).geoname.geonameID);
    }

}
