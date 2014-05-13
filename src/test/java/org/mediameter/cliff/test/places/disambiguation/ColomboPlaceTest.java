package org.mediameter.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediameter.cliff.test.util.TestPlaces;
import org.mediameter.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColomboPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ColomboPlaceTest.class);

    @Test
    public void testColombo() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/autralia-sri-lanka.txt", 
                new int[] {TestPlaces.COUNTRY_AUSTRALIA,TestPlaces.CITY_CAIRNS, TestPlaces.COUNTRY_SRI_LANKA}, true, logger );        
    }

}
