package org.mediameter.cliff.util;

import java.util.List;
import java.util.Map;

import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.extractor.OrganizationOccurrence;
import org.mediameter.cliff.extractor.PersonOccurrence;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.google.gson.Gson;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MuckUtils {

    public static ExtractedEntities entitiesFromJsonString(String nlpJsonString){
        List<Map> sentences = sentencesFromJsonString(nlpJsonString);
        return entitiesFromSentenceList(sentences);
    }

    public static List<Map> sentencesFromJsonString(String nlpJsonString) {
        Gson gson = new Gson();
        Map content = gson.fromJson(nlpJsonString, Map.class);
        return sentencesFromObject(content);
    }
    
    private static List<Map> sentencesFromObject(Map object){
        return (List<Map>) ((Map) object.get("corenlp")).get("sentences"); 
    }
    
    /**
     * I've overloaded "position" in each of the occurrences to be sentenceIndex 
     */
    private static ExtractedEntities entitiesFromSentenceList(List<Map> sentences){
        ExtractedEntities entities = new ExtractedEntities();
        int sentenceIdx = 0;
        for (Map sentence : sentences) {
            String queuedEntityText = null;
            String lastEntityType = null;
            List<Map> tokens = (List<Map>) sentence.get("tokens");
            for (Map token : tokens){
                String entityType = (String) token.get("ne"); 
                String tokenText = (String) token.get("word");
                if(entityType.equals(lastEntityType)){
                    queuedEntityText+= " "+tokenText;
                } else {
                    if(queuedEntityText!=null && lastEntityType!=null){
                        //TODO: figure out if we need the character index here or not
                        switch(lastEntityType){
                        case "PERSON":
                            entities.addPerson(new PersonOccurrence(queuedEntityText, sentenceIdx));
                            break;
                        case "LOCATION":
                            entities.addLocation(new LocationOccurrence(queuedEntityText, sentenceIdx));
                            break;
                        case "ORGANIZATION":
                            entities.addOrganization(new OrganizationOccurrence(queuedEntityText, sentenceIdx));
                            break;
                        }
                    }
                    queuedEntityText = tokenText;
                }
                lastEntityType = entityType;
            }
            sentenceIdx++;
        }
        return entities;
    }
    
}
