package org.mediameter.cliff.test.places.focus;

import java.io.File;
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
import org.mediameter.cliff.extractor.StanfordNamedEntityExtractor;
import org.mediameter.cliff.places.focus.FocusLocation;
import org.mediameter.cliff.places.focus.FocusStrategy;
import org.mediameter.cliff.places.substitutions.AbstractSubstitutionMap;
import org.mediameter.cliff.places.substitutions.CustomSubstitutionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

/**
 * Print out the accuracy of our Aboutness algorithm against the NYT Not a unit
 * test, because we don't have a threshold at which this is "correct". To run this,
 * Unzip some of the NYT corpus to the "data" folder, so you end up with something like
 * "data/nyt/1987/01/01".  This will walk anything under "data/nyt" and test it.
 * 
 * @author rahulb
 * 
 */
public class NYTFocusChecker {

    private static final Logger logger = LoggerFactory.getLogger(NYTFocusChecker.class);

    private static final String NYT_BASE_DIR = "data/nyt/";
    
    private NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser();
    
    private int articlesWithLocations = 0;
    private int articlesWeGotRight = 0;
    private int focusArticlesWeGotRight = 0;

    private AbstractSubstitutionMap customSubstitutions = new CustomSubstitutionMap(StanfordNamedEntityExtractor.CUSTOM_SUBSTITUTION_FILE); 
    
    public NYTFocusChecker(){
    }
    
    public void check() throws IOException {
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(NYT_BASE_DIR), fileProcessor);
        double success = (double)articlesWeGotRight/(double)articlesWithLocations; 
        double aboutnessSuccess = (double)focusArticlesWeGotRight/(double)articlesWithLocations; 
        logger.info("Checked "+articlesWithLocations+" Articles - Base success rate: "+success);
        logger.info("Checked "+articlesWithLocations+" Articles - Aboutness success rate: "+aboutnessSuccess);        
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs)
                throws IOException {
            logger.info("--------------------------------------------------------------------------------");
            logger.info("Visiting file "+aFile);
            if( aFile.getFileName().toString().endsWith(".xml") ) {
                NYTCorpusDocument doc = parser.parseNYTCorpusDocumentFromFile(new File(aFile.toString()), false);
                logger.info("  "+doc.getHeadline());
                if(doc.getLocations().size()>0){
                    articlesWithLocations++;
                    // load the document and geolocate the places NYT tagged
                    List<ResolvedLocation> rawResolvedLocations = new ArrayList<ResolvedLocation>();
                    List<LocationOccurrence> locationOccurrences = new ArrayList<LocationOccurrence>();
                    try {
                        for (String locationName: doc.getLocations()){
                            if(customSubstitutions.contains(locationName)){
                                locationName = customSubstitutions.getSubstitution(locationName); 
                            }
                            locationOccurrences.add( new LocationOccurrence(locationName,0) );
                            rawResolvedLocations.addAll( ParseManager.extractAndResolve(locationName).getResolvedLocations() );
                        }
                        List<ResolvedLocation> resolvedLocations;
                        resolvedLocations = ParseManager.getResolver().resolveLocations(locationOccurrences,false);
                        resolvedLocations.addAll(rawResolvedLocations);
                        List<GeoName> countriesTheyCoded = ExtractedEntities.getUniqueCountryGeoNames(resolvedLocations);
                   
                        // now geoparse it ourselves and see 
                        List<CountryCode> countriesWeFound = ParseManager.extractAndResolve(doc.getHeadline() + " " + doc.getBody()).getUniqueCountries();
                        if(countriesWeFound.size()>0){
                            boolean allMatched = true;
                            for(GeoName countryTheyCoded:countriesTheyCoded){
                                if(!countriesWeFound.contains(countryTheyCoded)){
                                    allMatched = false;
                                }
                            }
                            if(allMatched){
                                articlesWeGotRight++;
                            } else {
                                logger.warn("We found "+countriesWeFound+" they found "+countriesTheyCoded+" from ("+doc.getLocations()+")");
                                //logger.info("TC:" + doc.getTaxonomicClassifiers());
                            }
                        }
                        
                        //also have a measure for making sure the main "about" country is included in their list of countries
                        FocusStrategy aboutness = ParseManager.getFocusStrategy();
                        List<FocusLocation> ourAboutnessCountries = aboutness.selectCountries(resolvedLocations);
                        List<GeoName> ourAboutnessGeoNames = new ArrayList<GeoName>();
                        for(FocusLocation aboutLocation: ourAboutnessCountries){
                            ourAboutnessGeoNames.add(aboutLocation.getGeoName());
                        }
                        if(ourAboutnessCountries.size()>0){
                            boolean allMatched = true;
                            for(GeoName aboutnessGeoName:ourAboutnessGeoNames){
                                if(!countriesTheyCoded.contains(aboutnessGeoName)){
                                    allMatched = false;
                                }
                            }
                            if(allMatched){
                                focusArticlesWeGotRight++;
                            } else {
                                logger.warn("We found "+ourAboutnessCountries+" they found "+countriesTheyCoded+" from ("+doc.getLocations()+")");
                                //logger.info("TC:" + doc.getTaxonomicClassifiers());
                            }
                        }
                        
                    } catch (Exception e) {
                        logger.error("Lucene Resolving Error: "+e.toString());
                    }
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
        long startTime = System.currentTimeMillis();
        logger.info("Starting NYTFocusChecker");
        NYTFocusChecker checker = new NYTFocusChecker();
        checker.check();
        ParseManager.logStats();
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        logger.info("Done with NYTFocusChecker ("+elapsedMillis+" milliseconds)");
    }

}
