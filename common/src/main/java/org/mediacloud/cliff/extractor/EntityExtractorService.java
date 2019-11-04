package org.mediacloud.cliff.extractor;

import org.mediacloud.cliff.CliffConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: aaslinger
 * Date: 7/22/15
 * Time: 1:42 PM
 * <p/>
 * Developed By OpenWhere, Inc.
 */
public class EntityExtractorService {

    private static final Logger logger = LoggerFactory.getLogger(EntityExtractorService.class);
    private static EntityExtractorService service;
    private ServiceLoader<EntityExtractor> loader;

    private EntityExtractorService(){
        loader = ServiceLoader.load(EntityExtractor.class);
    }

    public static synchronized EntityExtractorService getInstance(){
        if(service == null){
            service = new EntityExtractorService();
        }
        return service;
    }

    public void initialize(CliffConfig config) throws Exception{

      //  Class clazz = Class.forName("edu.stanford.nlp.ie.crf.CRFClassifier");
       // ServiceLoader<EntityExtractor> loader = ServiceLoader.load(EntityExtractor.class);

        Iterator<EntityExtractor> extractors = loader.iterator();
        logger.info("Initializing NER Extractors");
        while (extractors != null && extractors.hasNext()) {
            EntityExtractor currentExtractor = extractors.next();
            logger.info("Initializing Extractor - {}", currentExtractor.getName());
            currentExtractor.initialize(config);
        }

    }
    
    public ExtractedEntities extractEntities(String textToParse, boolean manuallyReplaceDemonyms, String language){
        ExtractedEntities e = new ExtractedEntities();
        try {
            Iterator<EntityExtractor> extractors = loader.iterator();
            while (extractors != null && extractors.hasNext()) {
                EntityExtractor currentExtractor = extractors.next();
                ExtractedEntities e2 = currentExtractor.extractEntities(textToParse, manuallyReplaceDemonyms, language);
                e.merge(e2);
            }
        } catch (ServiceConfigurationError serviceError) {
            e = null;
            serviceError.printStackTrace();
        }
        return e;
    }

    @SuppressWarnings("rawtypes")
    public ExtractedEntities extractEntitiesFromSentences(Map[] sentences, boolean manuallyReplaceDemonyms, String langauge){
        ExtractedEntities e = new ExtractedEntities();
        try {
            Iterator<EntityExtractor> extractors = loader.iterator();
            while (extractors != null && extractors.hasNext()) {
                EntityExtractor currentExtractor = extractors.next();
                ExtractedEntities e2 = currentExtractor.extractEntitiesFromSentences(sentences, manuallyReplaceDemonyms, langauge);
                e.merge(e2);
            }
        } catch (ServiceConfigurationError serviceError) {
            e = null;
            serviceError.printStackTrace();
        }
        return e;
    }

}
