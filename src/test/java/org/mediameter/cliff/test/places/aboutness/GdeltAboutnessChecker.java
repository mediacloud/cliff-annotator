package org.mediameter.cliff.test.places.aboutness;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.test.gdelt.GdeltCsv;
import org.mediameter.cliff.test.gdelt.GdeltEvent;
import org.mediameter.cliff.test.util.FileSystemCache;
import org.mediameter.cliff.test.util.HTMLFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;

/**
 * Load some GDELT daily download files and test our geoparsing against them.  This prints out
 * accuract percentages, so it isn't a unit test per-say, because there isn't a magic threshold.
 * We just want to know how we're doing in comparison.
 * 
 * @author rahulb
 */
public class GdeltAboutnessChecker {

    private static final Logger logger = LoggerFactory.getLogger(GdeltAboutnessChecker.class);

    private static String BASE_DIR = "data/gdelt/";
        
    public GdeltAboutnessChecker(){
    }
    
    public void check() throws Exception {
        FileSystemCache cache = new FileSystemCache("gdelt-articles");
        ArrayList<GdeltEvent> events = GdeltCsv.allEvents(BASE_DIR);
        //TODO: run through events grabbing source text, running that through CLIFF, and checking results
        int mentionedSuccesses = 0;
        int mentionedFailures = 0;
        for(GdeltEvent event:events){
            logger.debug("-------------------------------------------------------------------------------------------");
            try{
                URL url = new URL(event.getSourceUrl());
                String text;
                if(cache.contains(url.toString())){
                    text = cache.get(url.toString());
                    logger.debug("Fetched from cache:"+url.toString());
                } else {
                    HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
                    TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
                    text = ArticleExtractor.INSTANCE.getText(doc);
                    cache.put(url.toString(), text);
                    logger.debug("Fetched from web:"+url.toString());
                }
                if(text.length()<100){
                    logger.debug("  Skipping because it is too short");
                    continue; //assume we didn't fetch/extract it right
                }
                //logger.debug(text);
                ExtractedEntities entities = ParseManager.extractAndResolve(text);
                List<CountryCode> countries = entities.getUniqueCountries();
                if( countries.contains(event.getActor1().getCountryCodeObj()) && countries.contains(event.getActor2().getCountryCodeObj())){
                    mentionedSuccesses = mentionedSuccesses + 1;
                } else {
                    mentionedFailures++;
                    logger.error(" We found "+countries+" - GDELT Says:"+event.getActor1().getCountryCodeObj()+" and "+event.getActor2().getCountryCodeObj());
                }
            } catch(Exception e){
                logger.warn("Skipping url "+event.getSourceUrl()+" because "+e.toString());
            }
        }
        
        double aboutnessSuccess = (double)mentionedSuccesses/(double)(mentionedSuccesses+mentionedFailures); 
        logger.info("Checked "+(mentionedSuccesses+mentionedFailures)+" Articles - Mentions success rate: "+aboutnessSuccess);
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.info("Starting GdeltAboutnessChecker");
        GdeltAboutnessChecker checker = new GdeltAboutnessChecker();
        checker.check();
        ParseManager.logStats();
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        logger.info("Done with GdeltAboutnessChecker ("+elapsedMillis+" milliseconds)");
    }

}
