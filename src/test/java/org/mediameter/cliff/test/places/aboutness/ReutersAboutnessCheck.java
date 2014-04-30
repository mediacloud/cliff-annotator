package org.mediameter.cliff.test.places.aboutness;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.places.aboutness.AboutnessStrategy;
import org.mediameter.cliff.test.reuters.RegionSubstitutionMap;
import org.mediameter.cliff.test.reuters.ReutersCorpusDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;

/**
 * Load some of the Reuters corpus and check against their geographic tagging.  This prints out
 * accuracy percentages, so it isn't a unit test per-say, because there isn't a magic threshold.
 * We just want to know how we're doing in comparison.
 * 
 * @author rahulb
 */
public class ReutersAboutnessCheck {

    private static final Logger logger = LoggerFactory.getLogger(ReutersAboutnessCheck.class);

    public static final String REGIONS_FILE = "reuters_region_codes.txt";

    private static String BASE_DIR = "data/reuters/";

    private int articlesWithLocations = 0;
    private int aboutnessArticlesWeGotRight = 0;
    private int mentionsArticlesWeGotRight = 0;
    
    private RegionSubstitutionMap substitutions;
    
    public ReutersAboutnessCheck() throws IOException{
        substitutions = new RegionSubstitutionMap(REGIONS_FILE);
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(BASE_DIR), fileProcessor);
        double success = (double)mentionsArticlesWeGotRight/(double)articlesWithLocations; 
        double aboutnessSuccess = (double)aboutnessArticlesWeGotRight/(double)articlesWithLocations; 
        logger.info("Checked "+articlesWithLocations+" Articles - Base success rate: "+success);
        logger.info("Checked "+articlesWithLocations+" Articles - Aboutness success rate: "+aboutnessSuccess);
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
                        List<CountryCode> countriesTheyCoded = doc.getCountryCodeObjects();
                        logger.info(doc.getId()+": "+countriesTheyCoded);
                        List<CountryCode> ourMentionedCountries = entities.getUniqueCountries();

                        // check to make sure we found all the countries they coded
                        if(ourMentionedCountries.size()>0){
                            boolean allMatched = true;
                            for(CountryCode countryTheyCoded:countriesTheyCoded){
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
                        AboutnessStrategy aboutness = ParseManager.getAboutness();
                        List<CountryCode> ourAboutnessCountries = aboutness.selectCountries(entities.getResolvedLocations());
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
                                logger.warn(doc.getId()+": about "+ourAboutnessCountries+" they found "+countriesTheyCoded);
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
        logger.info("Starting ReutersAboutnessCheck");
        ReutersAboutnessCheck checker = new ReutersAboutnessCheck();
        ParseManager.logStats();
        logger.info("Done with ReutersAboutnessCheck");
    }

}
