package org.mediameter.cliff.test.places.aboutness;

import java.util.ArrayList;

import org.mediameter.cliff.test.gdelt.GdeltCsv;
import org.mediameter.cliff.test.gdelt.GdeltEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load some GDELT daily download files and test our geoparsing against them.  This prints out
 * accuract percentages, so it isn't a unit test per-say, because there isn't a magic threshold.
 * We just want to know how we're doing in comparison.
 * 
 * @author rahulb
 */
public class GdeltAboutnessCheck {

    private static final Logger logger = LoggerFactory.getLogger(GdeltAboutnessCheck.class);

    private static String BASE_DIR = "data/gdelt/";
        
    public GdeltAboutnessCheck() throws Exception {
        ArrayList<GdeltEvent> events = GdeltCsv.allEvents(BASE_DIR);
        //TODO: run through events grabbing source text, running that through CLIFF, and checking results
    }

    public static void main(String[] args) throws Exception {
        logger.info("Starting GdeltAboutnessCheck");
        GdeltAboutnessCheck checker = new GdeltAboutnessCheck();
        //ParseManager.logStats();
        logger.info("Done with GdeltAboutnessCheck");
    }

}
