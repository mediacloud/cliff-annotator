package org.mediameter.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitedStatesPlaceTest {

    private static final Logger logger = LoggerFactory.getLogger(UnitedStatesPlaceTest.class);

    @Test
    public void testUsExample() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/us-story.txt", 
                new int[] {TestPlaces.COUNTRY_US}, logger );
    }

}
