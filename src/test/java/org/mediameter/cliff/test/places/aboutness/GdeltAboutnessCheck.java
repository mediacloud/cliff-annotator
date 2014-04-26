package org.mediameter.cliff.test.places.aboutness;

import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.test.gdelt.GdeltCsv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author rahulb
 * 
 */
public class GdeltAboutnessCheck {

    private static final Logger logger = LoggerFactory.getLogger(GdeltAboutnessCheck.class);
        
    public GdeltAboutnessCheck() throws Exception {
        GdeltCsv.allEvents();
    }

    /**
     * The 'locations' field specifies a list of geographic descriptors drawn
     * from a normalized controlled vocabulary that correspond to places
     * mentioned in the article. These tags are hand-assigned by The New York
     * Times Indexing Service.
     * 
     * @param filePath
     * @return
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        GdeltAboutnessCheck checker = new GdeltAboutnessCheck();
        ParseManager.logStats();
    }

}
