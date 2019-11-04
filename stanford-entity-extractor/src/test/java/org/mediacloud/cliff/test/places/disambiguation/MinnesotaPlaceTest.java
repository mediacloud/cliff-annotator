package org.mediacloud.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediacloud.cliff.test.util.TestPlaces;
import org.mediacloud.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinnesotaPlaceTest {

    private static final Logger logger = LoggerFactory.getLogger(MinnesotaPlaceTest.class);

    @Test
    public void testMinnesotaExample() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/minnesota.txt", 
                new int[] {TestPlaces.STATE_MINNESOTA}, logger );
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/speech.txt", 
                new int[] {TestPlaces.STATE_MINNESOTA,TestPlaces.CITY_BEIJING},logger );
    }
    
}
