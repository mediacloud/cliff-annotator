package org.mediacloud.cliff.test.places.disambiguation;

import org.junit.Test;
import org.mediacloud.cliff.test.util.TestPlaces;
import org.mediacloud.cliff.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiddleEastPlaceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MiddleEastPlaceTest.class);
    
    @Test
    public void testMiddleEastExample() throws Exception {
        TestUtils.verifyPlacesInFile("src/test/resources/sample-docs/middle-east.txt", 
                new int[] {TestPlaces.REGION_MIDDLE_EAST},logger);        
    }

}
