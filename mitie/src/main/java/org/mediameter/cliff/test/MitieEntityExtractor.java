package org.mediameter.cliff.test;

import com.bericotech.clavin.extractor.LocationOccurrence;
import edu.mit.ll.mitie.NamedEntityExtractor;
import org.kohsuke.MetaInfServices;
import org.mediameter.cliff.CliffConfig;
import org.mediameter.cliff.extractor.*;
import org.mediameter.cliff.places.substitutions.Blacklist;
import org.mediameter.cliff.places.substitutions.CustomSubstitutionMap;
import org.mediameter.cliff.places.substitutions.WikipediaDemonymMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.ll.mitie.*;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aaslinger
 * Date: 7/23/15
 * Time: 11:12 AM
 * <p/>
 * Developed By OpenWhere, Inc.
 */
@MetaInfServices(EntityExtractor.class)
public class MitieEntityExtractor implements EntityExtractor {

    public final static Logger logger = LoggerFactory.getLogger(MitieEntityExtractor.class);

    @Override
    public String getName() {
        return "MITIE";
    }

    public static final String CUSTOM_SUBSTITUTION_FILE = "custom-substitutions.csv";
    public static final String LOCATION_BLACKLIST_FILE = "location-blacklist.txt";
    public static final String PERSON_TO_PLACE_FILE = "person-to-place-replacements.csv";

    // the actual named entity recognizer (NER) object
    private NamedEntityExtractor namedEntityRecognizer;

    private WikipediaDemonymMap demonyms;
    private CustomSubstitutionMap customSubstitutions;
    private CustomSubstitutionMap personToPlaceSubstitutions;
    private Blacklist locationBlacklist;

    public void initialize(CliffConfig config) throws ClassCastException, IOException, ClassNotFoundException{

        String model = config.getPropertyByName("mer.mitiePath");
        if(model == null){
            logger.error("No MITIE model configured at {}", model);
        }
        namedEntityRecognizer = new NamedEntityExtractor(model);

        demonyms = new WikipediaDemonymMap();
        customSubstitutions = new CustomSubstitutionMap(CUSTOM_SUBSTITUTION_FILE);
        locationBlacklist = new Blacklist(LOCATION_BLACKLIST_FILE);
        personToPlaceSubstitutions = new CustomSubstitutionMap(PERSON_TO_PLACE_FILE,false);
    }

//
//    /**
//     * Get extracted locations from a plain-text body.
//     *
//     * @param textToParse                      Text content to perform extraction on.
//     * @param manuallyReplaceDemonyms   Can slow down performance quite a bit
//     * @return          All the entities mentioned
//     */
//    @Override
    public ExtractedEntities extractEntities(String textToParse, boolean manuallyReplaceDemonyms) {
        ExtractedEntities entities = new ExtractedEntities();

        if (textToParse==null || textToParse.length()==0){
            logger.warn("input to extractEntities was null or zero!");
            return entities;
        }

        String text = textToParse;
        if(manuallyReplaceDemonyms){    // this is a noticeable performance hit
            logger.debug("Replacing all demonyms by hand");
            text = demonyms.replaceAll(textToParse);
        }

        global g = new global();
        StringVector words = g.tokenize(text);

        StringVector possibleTags = namedEntityRecognizer.getPossibleNerTags();
        EntityMentionVector extractedEntities = namedEntityRecognizer.extractEntities(words);

        if (extractedEntities != null) {
            assignExtractedEntities(entities, text, words, possibleTags, extractedEntities);
        }

        return entities;
    }

    private void assignExtractedEntities(ExtractedEntities entities, String text, StringVector words, StringVector possibleTags, EntityMentionVector extractedEntities) {
        for (int i=0; i < extractedEntities.size(); i++){
            EntityMention extractedEntity = extractedEntities.get(i);
            String entityName = getEntityString(words, extractedEntity);
            String tag = possibleTags.get(extractedEntity.getTag());
            int position = text.indexOf(entityName);
            switch(tag){
                case "PERSON":
                    if(personToPlaceSubstitutions.contains(entityName)){
                        entities.addLocation( getLocationOccurrence(personToPlaceSubstitutions.getSubstitution(entityName), position) );
                        logger.debug("Changed person "+entityName+" to a place");
                    } else {
                        PersonOccurrence person = new PersonOccurrence(entityName, position);
                        entities.addPerson( person );
                    }
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
                default:
                    logger.error("Unknown NER type :"+ tag);
            }
        }
    }


    /**
     * Get extracted locations from a plain-text body.
     *
     * @param sentences                      Text content to perform extraction on.
     * @param manuallyReplaceDemonyms   Can slow down performance quite a bit
     * @return          All the entities mentioned
     */
    @Override
    public ExtractedEntities extractEntitiesFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms) {
        ExtractedEntities entities = new ExtractedEntities();

        if (sentences.length==0){
            logger.warn("input to extractEntities was null or zero!");
            return entities;
        }

        if(manuallyReplaceDemonyms){    // this is a noticeable performance hit
            logger.debug("Replacing all demonyms by hand");
        }



        StringVector possibleTags = namedEntityRecognizer.getPossibleNerTags();

        for(Map s:sentences){
            String storySentencesId = s.get("story_sentences_id").toString();
            String text = s.get("sentence").toString();
            if(manuallyReplaceDemonyms){    // this is a noticeable performance hit
                text = demonyms.replaceAll(text);
            }

            StringVector words = global.tokenize(text);
            EntityMentionVector extractedEntities = namedEntityRecognizer.extractEntities(words);

            if (extractedEntities != null) {
                assignExtractedEntities(entities, text, words, possibleTags, extractedEntities);
            }
        }

        return entities;
    }

    private String getEntityString(StringVector words, EntityMention ent){
        StringBuilder builder = new StringBuilder();
        for(int i = ent.getStart(); i < ent.getEnd(); i++){
            builder.append(words.get(i));
            if(i + 1 < ent.getEnd()){
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private LocationOccurrence getLocationOccurrence(String entityName, int position){
        String fixedName = entityName;
        if (demonyms.contains(entityName)) {
            fixedName = demonyms.getSubstitution(entityName);
            logger.debug("Demonym substitution: "+entityName+" to "+fixedName);
        } else if(customSubstitutions.contains(entityName)) {
            fixedName = customSubstitutions.getSubstitution(entityName);
            logger.debug("Custom substitution: "+entityName+" to "+fixedName);
        }
        return new LocationOccurrence(fixedName, position);
    }
}
