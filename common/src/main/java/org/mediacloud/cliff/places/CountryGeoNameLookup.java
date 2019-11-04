package org.mediacloud.cliff.places;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mediacloud.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.GeoName;

/**
 * If you have a country code (iso3166-alpha2) this returns the GeoName object for it
 */
public class CountryGeoNameLookup extends AbstractGeoNameLookup {

    public final static Logger logger = LoggerFactory.getLogger(CountryGeoNameLookup.class);

    public static final String RESOURCE_NAME = "countryInfo.txt";

    private static CountryGeoNameLookup instance;
    
    public CountryGeoNameLookup() throws IOException {
        super();
    }

    @Override
    public void parse() {
        try {
            CliffLocationResolver resolver = (CliffLocationResolver) ParseManager.getResolver();
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)));
            String line = null;
            while ((line = br.readLine()) != null) {  
                if(line.trim().length()==0){
                    continue;
                }
                if (line.charAt(0)=='#') {
                    continue;
                }
                String[] columns = line.trim().split("\t");
                try {
                    String iso3166Alpha2 = columns[0];
                    //String name = columns[4];
                    int geonameId = Integer.parseInt(columns[16]);                
                    this.put(iso3166Alpha2, resolver.getByGeoNameId(geonameId));
                } catch (NumberFormatException nfe){
                    logger.error("Couldn't parse geoname id from line: "+line);
                } catch (UnknownGeoNameIdException ugie) {
                    logger.error("Uknown geoNameId "+ugie.getGeoNameId()+" for: "+columns[4]);
                }
            }
            logger.info("Loaded "+this.size()+" countries");
        } catch(Exception e){
            logger.error("Unable to load location resolver");
            logger.error(e.toString());
        }
    }
    
    private static CountryGeoNameLookup getInstance() throws IOException{
        if(instance==null){
            instance = new CountryGeoNameLookup();
        }
        return instance;
    }
    
    public static GeoName lookup(String countryCodeAlpha2) {
        try{
            CountryGeoNameLookup lookup = getInstance();
            GeoName countryGeoName = lookup.get(countryCodeAlpha2);
            logger.debug("Found '"+countryCodeAlpha2+"': "+countryGeoName);
            return countryGeoName;
        } catch (IOException ioe){
            logger.error("Couldn't lookup country geoname!");
            logger.error(ioe.toString());
        }
        return null;
    }
	    
}