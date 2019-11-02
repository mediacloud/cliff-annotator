package org.mediameter.cliff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton wrapper around configuration loaded from env-vars
 * @author rahulb
 */
public class CliffConfig {

    private static final Logger logger = LoggerFactory.getLogger(CliffConfig.class);

    private static CliffConfig instance = null;

    private static String INDEX_PATH_ENV_VAR = "INDEX_PATH";
    
    private static String DEFAULT_INDEX_PATH = "/etc/cliff2/IndexDirectory";
            
    private String indexPath = null;
    
    protected CliffConfig() {
    	indexPath = System.getenv(INDEX_PATH_ENV_VAR);
    	if (indexPath == null) {
    		indexPath = DEFAULT_INDEX_PATH;
    		logger.warn("No INDEX_PATH env-var configured, using "+DEFAULT_INDEX_PATH);
    	}
    }
    
    public String getPathToGeonamesIndex(){
    	return indexPath;
    }

    
    public static CliffConfig getInstance() {
       if(instance==null){
           instance = new CliffConfig();
       }
       return instance;
    }
    
}
