package edu.mit.civic.mediacloud.test.where;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

import edu.mit.civic.mediacloud.ParseManager;
import edu.mit.civic.mediacloud.extractor.ExtractedEntities;
import edu.mit.civic.mediacloud.where.aboutness.AboutnessStrategy;
import edu.mit.civic.mediacloud.where.substitutions.AbstractSubstitutionMap;
import edu.mit.civic.mediacloud.where.substitutions.CustomSubstitutionMap;

/**
 * Print out the accuracy of our Aboutness algorithm against the NYT Not a unit
 * test, because we don't have a threshold at which this is "correct"
 * 
 * @author rahulb
 * 
 */
public class NYTAboutnessCheck {

    private static final Logger logger = LoggerFactory
            .getLogger(HandCodedDisambiguationTest.class);

    private static final String NYT_BASE_DIR = "data/nyt/";
    
    private NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser();
    
    private int articlesWithLocations = 0;
    private int articlesWeGotRight = 0;
    private int aboutnessArticlesWeGotRight = 0;

    private AbstractSubstitutionMap customSubstitutions = new CustomSubstitutionMap(); 
    
    public NYTAboutnessCheck() throws IOException {
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(NYT_BASE_DIR), fileProcessor);
        double success = (double)articlesWeGotRight/(double)articlesWithLocations; 
        double aboutnessSuccess = (double)aboutnessArticlesWeGotRight/(double)articlesWithLocations; 
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
                    for (String locationName: doc.getLocations()){
                        if(customSubstitutions.contains(locationName)){
                            locationName = customSubstitutions.getSubstitution(locationName); 
                        }
                        locationOccurrences.add( new LocationOccurrence(locationName,0) );
                        rawResolvedLocations.addAll( ParseManager.extractAndResolve(locationName).getResolvedLocations() );
                    }
                    try {
                        List<ResolvedLocation> resolvedLocations;
                        resolvedLocations = ParseManager.getResolver().resolveLocations(locationOccurrences,false);
                        resolvedLocations.addAll(rawResolvedLocations);
                        List<CountryCode> countriesTheyCoded = ExtractedEntities.getUniqueCountries(resolvedLocations);
                        // now geoparse it ourselves
                        List<CountryCode> countriesWeFound = ParseManager.extractAndResolve(doc.getHeadline() + " " + doc.getBody()).getUniqueCountries();
                       
                        if(countriesWeFound.size()>0){
                            boolean allMatched = true;
                            for(CountryCode countryTheyCoded:countriesTheyCoded){
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
                        AboutnessStrategy aboutness = ParseManager.getAboutness();
                        List<CountryCode> ourAboutnessCountries = aboutness.selectCountries(resolvedLocations, doc.getHeadline() + " " + doc.getBody());
                        if(ourAboutnessCountries.size()>0){
                            boolean allMatched = true;
                            for(CountryCode aboutnessCountry:ourAboutnessCountries){
                                if(!countriesTheyCoded.contains(aboutnessCountry)){
                                    allMatched = false;
                                }
                            }
                            if(allMatched){
                                aboutnessArticlesWeGotRight++;
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
     * The ���locations��� field specifies a list of geographic descriptors drawn
     * from a normalized controlled vocabulary that correspond to places
     * mentioned in the article. These tags are hand-assigned by The New York
     * Times Indexing Service.
     * 
     * @param filePath
     * @return
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        NYTAboutnessCheck checker = new NYTAboutnessCheck();
        ParseManager.logStats();
    }

}
