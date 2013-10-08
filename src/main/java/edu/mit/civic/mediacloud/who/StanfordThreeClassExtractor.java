package edu.mit.civic.mediacloud.who;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

/**
 */
public class StanfordThreeClassExtractor{

    // the actual named entity recognizer (NER) object
    private AbstractSequenceClassifier<CoreMap> namedEntityRecognizer;
    
    
    /**
     * Default constructor. Instantiates a {@link StanfordThreeClassExtractor}
     * with the standard English language model
     * 
     * @throws ClassCastException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StanfordThreeClassExtractor() throws ClassCastException, IOException, ClassNotFoundException {
        this("all.3class.distsim.crf.ser.gz", "all.3class.distsim.prop" );
    }
    
    /**
     * Builds a {@link StanfordThreeClassExtractor} by instantiating the 
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
    public StanfordThreeClassExtractor(String NERmodel, String NERprop) throws IOException, ClassCastException, ClassNotFoundException {
        
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
    public List<PersonOccurrence> extractPeopleNames(String text) {
        if (text == null)
            throw new IllegalArgumentException("text input to extractPeopleNames should not be null");

        List<PersonOccurrence> extractedPeople = new ArrayList<PersonOccurrence>();

        // extract entities as <Entity Type, Start Index, Stop Index>
        List<Triple<String, Integer, Integer>> extractedEntities = 
                namedEntityRecognizer.classifyToCharacterOffsets(text);

        if (extractedEntities != null) {
            for (Triple<String, Integer, Integer> extractedEntity : extractedEntities) {
                if (extractedEntity.first.equalsIgnoreCase("PERSON")) {
                    PersonOccurrence person = new PersonOccurrence(
                            text.substring(extractedEntity.second(), extractedEntity.third()), 
                            extractedEntity.second());
                    extractedPeople.add(person);
                }
            }
        }

        return extractedPeople;
    }
}
