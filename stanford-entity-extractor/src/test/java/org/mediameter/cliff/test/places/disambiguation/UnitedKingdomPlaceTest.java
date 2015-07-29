package org.mediameter.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitedKingdomPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(UnitedKingdomPlaceTest.class);

    @Test
    public void testUkExample() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/uk-story.txt", 
                new int[] {TestPlaces.COUNTRY_UK}, logger );
    }
    
}
