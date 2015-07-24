package org.mediameter.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests that verify some very specific cases work
 */
public class SaoPauloPlaceTest {
 
    private static final Logger logger = LoggerFactory.getLogger(SaoPauloPlaceTest.class);

    @Test
    public void testSaoPauloAccents() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/sao-paulo.txt",
                new int[] {TestPlaces.CITY_SAO_PAULO}, true, logger);
    }
           
    
}