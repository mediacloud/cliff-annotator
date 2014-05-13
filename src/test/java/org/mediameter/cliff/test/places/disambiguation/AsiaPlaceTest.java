package org.mediameter.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsiaPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AsiaPlaceTest.class);

    @Test
    public void testAsia() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/asia.txt", 
                new int[] {TestPlaces.REGION_ASIA}, logger );        
    }

}
