package edu.mit.civic.mediacloud.muck;

import java.util.List;
import java.util.Map;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.google.gson.Gson;

import edu.mit.civic.mediacloud.extractor.ExtractedEntities;
import edu.mit.civic.mediacloud.extractor.PersonOccurrence;

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
    
    private static ExtractedEntities entitiesFromSentenceList(List<Map> sentences){
        ExtractedEntities entities = new ExtractedEntities();
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
                            entities.addPerson(new PersonOccurrence(queuedEntityText, -1));
                            break;
                        case "LOCATION":
                            entities.addLocation(new LocationOccurrence(queuedEntityText, -1));
                            break;
                        }
                    }
                    queuedEntityText = tokenText;
                }
                lastEntityType = entityType;
            }
        }
        return entities;
    }
    
}
