package org.mediameter.cliff.places;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.GeoName;

/**
 * A HashMap from name to geoname object.
 */
public abstract class AbstractGeoNameLookup {
	
	public final static Logger logger = LoggerFactory.getLogger(AbstractGeoNameLookup.class);
	
	private HashMap<String,GeoName> lookup;
	
	/**
	 * 
	 */
	public AbstractGeoNameLookup() throws IOException {
	    lookup = new HashMap<String,GeoName>();
	    this.parse();
	}

	public abstract void parse() throws IOException;
	
	public void put(String key, GeoName geoName){
	    lookup.put(key, geoName);
	}
	
	public GeoName get(String key){
	    return lookup.get(key);
	}

	public boolean contains(String key){
	    return lookup.containsKey(key);
	}
	
	public int size(){
	    return lookup.size();
	}
	
}