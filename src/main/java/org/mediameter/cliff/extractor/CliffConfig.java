package org.mediameter.cliff.extractor;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton wrapper around configuration properties file
 * @author rahulb
 */
public class CliffConfig {

    private static final Logger logger = LoggerFactory.getLogger(CliffConfig.class);

    private static CliffConfig instance = null;

    private static String PROPS_FILE_NAME = "cliff.properties";
    
    private Properties props;
        
    protected CliffConfig() throws Exception{
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(PROPS_FILE_NAME);
        if(is==null){
            logger.error("Couldn't find resource "+PROPS_FILE_NAME);
            throw new RuntimeException("Couldn't find resource "+PROPS_FILE_NAME);
        }
        props = new Properties();
        props.load(is);
        logger.debug("Loaded "+PROPS_FILE_NAME+":");
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
          String key = keys.nextElement().toString();
          String value = props.get(key).toString();
          logger.debug("  "+key+ "=" + value);
        }
    }
    
    public String getNerModelName(){
        return (String) props.get("ner.modelToUse");
    }
    
    public static CliffConfig getInstance() {
       if(instance==null){
           try {
               instance = new CliffConfig();
           } catch (Exception e) {
               logger.error("Couldn't load "+PROPS_FILE_NAME+ " ("+e.toString()+")");
           }
       }
       return instance;
    }
    
}
