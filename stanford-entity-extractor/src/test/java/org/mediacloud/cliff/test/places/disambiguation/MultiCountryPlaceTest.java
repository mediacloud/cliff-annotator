package org.mediacloud.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediacloud.cliff.test.util.TestPlaces;
import org.mediacloud.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiCountryPlaceTest {

    private static final Logger logger = LoggerFactory.getLogger(MultiCountryPlaceTest.class);

    @Test
    public void testCountryExample() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/multi-country.txt", 
                new int[] {TestPlaces.COUNTRY_CHINA,TestPlaces.COUNTRY_INDIA,TestPlaces.COUNTRY_AUSTRALIA,
                           TestPlaces.COUNTRY_US, TestPlaces.COUNTRY_RUSSIA},logger );
    }

}
