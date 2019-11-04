package org.mediacloud.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediacloud.cliff.test.util.TestPlaces;
import org.mediacloud.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LondonRusselSqPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LondonRusselSqPlaceTest.class);
    
    @Test
    public void testRussellSq() throws Exception {
        // picks the right Russel Sq (the one in GB) after we find London in the article
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/russel-sq-london.txt",
                new int[] {TestPlaces.CITY_LONDON, TestPlaces.PLACE_RUSSEL_SQ_LONDON}, true, logger);
    }

}
