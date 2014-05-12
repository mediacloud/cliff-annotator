package org.mediameter.cliff.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.mediameter.cliff.places.substitutions.AbstractSubstitutionMap;
import org.mediameter.cliff.places.substitutions.Blacklist;
import org.mediameter.cliff.places.substitutions.CustomSubstitutionMap;
import org.mediameter.cliff.places.substitutions.WikipediaDemonymMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.extractor.LocationOccurrence;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

/**
 */
public class StanfordNamedEntityExtractor{

    public final static Logger logger = LoggerFactory.getLogger(StanfordNamedEntityExtractor.class);

    public static final String CUSTOM_SUBSTITUTION_FILE = "custom-substitutions.csv";
    public static final String LOCATION_BLACKLIST_FILE = "location-blacklist.txt";

    // the actual named entity recognizer (NER) object
    private AbstractSequenceClassifier<CoreMap> namedEntityRecognizer;
    
    private static final boolean SUBSITUE_DEMONYMS = true; 
    
    private AbstractSubstitutionMap demonyms;
    private AbstractSubstitutionMap customSubstitutions;
    private Blacklist locationBlacklist;
    
    /**
     * Default constructor. Instantiates a {@link StanfordNamedEntityExtractor}
     * with the standard English language model
     * 
     * @throws ClassCastException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StanfordNamedEntityExtractor() throws ClassCastException, IOException, ClassNotFoundException {
        //this("english.all.3class.distsim.crf.ser.gz", "english.all.3class.distsim.prop" );
        this("english.conll.4class.distsim.crf.ser.gz", "english.conll.4class.distsim.prop");
        demonyms = new WikipediaDemonymMap();
        customSubstitutions = new CustomSubstitutionMap(CUSTOM_SUBSTITUTION_FILE);
        locationBlacklist = new Blacklist(LOCATION_BLACKLIST_FILE);
    }
    
    /**
     * Builds a {@link StanfordNamedEntityExtractor} by instantiating the 
     * Stanford NER named entity recognizer with a specified 
     * language model.
     * 
     * @param NERmodel                      path to Stanford NER language model
     * @param NERprop                       path to property file for Stanford NER language model
     * @throws IOException 
     * @throws ClassNotFoundException 
     * @throws ClassCastException 
     */
    //@SuppressWarnings("unchecked")
    public StanfordNamedEntityExtractor(String NERmodel, String NERprop) throws IOException, ClassCastException, ClassNotFoundException {

        InputStream mpis = this.getClass().getClassLoader().getResourceAsStream("models/" + NERprop);
        Properties mp = new Properties();
        mp.load(mpis);
        
        
        namedEntityRecognizer = (AbstractSequenceClassifier<CoreMap>) 
                CRFClassifier.getJarClassifier("/models/" + NERmodel, mp);
                        
    }

    /**
     * Get extracted locations from a plain-text body.
     * 
     * @param text      Text content to perform extraction on.
     * @return          List of Location Occurrences.
     */
    public ExtractedEntities extractEntities(String text) {
        if (text == null)
            throw new IllegalArgumentException("text input to extractEntities should not be null");

        ExtractedEntities entities = new ExtractedEntities();
        
        // extract entities as <Entity Type, Start Index, Stop Index>
        List<Triple<String, Integer, Integer>> extractedEntities = 
                namedEntityRecognizer.classifyToCharacterOffsets(text);

        if (extractedEntities != null) {
            for (Triple<String, Integer, Integer> extractedEntity : extractedEntities) {
                String entityName = text.substring(extractedEntity.second(), extractedEntity.third());
                int position = extractedEntity.second();
                switch(extractedEntity.first){
                case "PERSON":
                    PersonOccurrence person = new PersonOccurrence(entityName, position);
                    entities.addPerson( person );
                    break;
                case "LOCATION":
                    if(!locationBlacklist.contains(entityName)){
                        entities.addLocation( getLocationOccurrence(entityName, position) );
                    } else {
                       logger.debug("Ignored blacklisted location "+entityName);
                    }
                    break;
                case "ORGANIZATION":
                    OrganizationOccurrence organization = new OrganizationOccurrence(entityName, position);
                    entities.addOrganization( organization );
                    break;
                case "MISC":
                    if (SUBSITUE_DEMONYMS && demonyms.contains(entityName)) {
                        logger.debug("Found and adding a MISC demonym "+entityName);
                        entities.addLocation( getLocationOccurrence(entityName, position) );
                    }
                    break;
                default:
                    logger.error("Unknown NER type :"+ extractedEntity.first);
                }
            }
        }

        return entities;
    }
    
    private LocationOccurrence getLocationOccurrence(String entityName, int position){
        String fixedName = entityName;
        if (SUBSITUE_DEMONYMS && demonyms.contains(entityName)) {
            fixedName = demonyms.getSubstitution(entityName); 
            logger.debug("Demonym substitution: "+entityName+" to "+fixedName);
        } else if(customSubstitutions.contains(entityName)) {
            fixedName = customSubstitutions.getSubstitution(entityName);
            logger.debug("Custom substitution: "+entityName+" to "+fixedName);
        }
        return new LocationOccurrence(fixedName, position);
    }
    
}
