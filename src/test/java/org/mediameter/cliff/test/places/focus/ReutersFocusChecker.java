package org.mediameter.cliff.test.places.focus;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.places.CountryGeoNameLookup;
import org.mediameter.cliff.places.focus.FocusLocation;
import org.mediameter.cliff.places.focus.FocusStrategy;
import org.mediameter.cliff.test.reuters.RegionSubstitutionMap;
import org.mediameter.cliff.test.reuters.ReutersCorpusDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;

/**
 * Load some of the Reuters corpus and check against their geographic tagging.  This prints out
 * accuracy percentages, so it isn't a unit test per-say, because there isn't a magic threshold.
 * We just want to know how we're doing in comparison.
 * 
 * @author rahulb
 */
public class ReutersFocusChecker {

    private static final Logger logger = LoggerFactory.getLogger(ReutersFocusChecker.class);

    public static final String REGIONS_FILE = "reuters_region_codes.txt";

    private static String BASE_DIR = "data/reuters/";

    private int articlesWithLocations = 0;
    private int focusArticlesWeGotRight = 0;
    private int mentionsArticlesWeGotRight = 0;
    
    private RegionSubstitutionMap substitutions;
    
    public ReutersFocusChecker() throws Exception {
        substitutions = new RegionSubstitutionMap(REGIONS_FILE);
    }

    public void check() throws IOException{
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(BASE_DIR), fileProcessor);
        double success = (double)mentionsArticlesWeGotRight/(double)articlesWithLocations; 
        double focusSuccess = (double)focusArticlesWeGotRight/(double)articlesWithLocations; 
        logger.info("Checked "+articlesWithLocations+" Articles - Base success rate: "+success);
        logger.info("Checked "+articlesWithLocations+" Articles - Aboutness success rate: "+focusSuccess);
    }
    
    private final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs)
                throws IOException {
            logger.info("--------------------------------------------------------------------------------");
            if( aFile.getFileName().toString().endsWith(".xml") ) {
                ReutersCorpusDocument doc;
                try {
                    
                    doc = ReutersCorpusDocument.fromFile(aFile.toString(),substitutions);
                    if(doc.hasCodedCountries()){
                        ExtractedEntities entities =  ParseManager.extractAndResolve(doc.getCompiledText());
                        
                        logger.info("Checking file "+aFile);
                        articlesWithLocations++;
                        List<GeoName> countriesTheyCoded = new ArrayList<GeoName>();
                        for(CountryCode countryCode:doc.getCountryCodeObjects()){
                            countriesTheyCoded.add( CountryGeoNameLookup.lookup(countryCode.name()) );
                        }
                        logger.info(doc.getId()+": "+countriesTheyCoded);
                        List<GeoName> ourMentionedCountries = entities.getUniqueCountryGeoNames();

                        // check to make sure we found all the countries they coded
                        if(ourMentionedCountries.size()>0){
                            boolean allMatched = true;
                            for(GeoName countryTheyCoded:countriesTheyCoded){
                                if(!ourMentionedCountries.contains(countryTheyCoded)){
                                    allMatched = false;
                                }
                            }
                            if(allMatched){
                                mentionsArticlesWeGotRight++;
                            } else {
                                logger.warn(doc.getId()+": mentions "+ourMentionedCountries+" they coded "+countriesTheyCoded);
                            }
                        }

                        //also have a measure for making sure the main "about" country is included in their list of countries
                        FocusStrategy focus = ParseManager.getFocusStrategy();
                        List<FocusLocation> ourAboutnessCountries = focus.selectCountries(entities.getResolvedLocations());
                        List<GeoName> ourAboutnessGeoNames = new ArrayList<GeoName>();
                        for(FocusLocation aboutLocation: ourAboutnessCountries){
                            ourAboutnessGeoNames.add(aboutLocation.getGeoName());
                        }
                        if(ourAboutnessGeoNames.size()>0){
                            boolean allMatched = true;
                            for(GeoName focusGeoName:ourAboutnessGeoNames){
                                if(!countriesTheyCoded.contains(focusGeoName)){
                                    allMatched = false;
                                }
                            }
                            if(allMatched){
                                focusArticlesWeGotRight++;
                            } else {
                                logger.warn(doc.getId()+": about "+ourAboutnessGeoNames+" they found "+countriesTheyCoded);
                            }
                        }
                        
                    }
                } catch (Exception e) {
                    logger.info("skipping it becuase "+e.toString());
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path aDir,
                BasicFileAttributes aAttrs) throws IOException {
            logger.info("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }
    
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.info("Starting ReutersFocusChecker");
        ReutersFocusChecker checker = new ReutersFocusChecker();
        checker.check();
        ParseManager.logStats();
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        logger.info("Done with ReutersFocusChecker ("+elapsedMillis+" milliseconds)");
    }

}
