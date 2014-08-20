package org.mediameter.cliff.places;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.GeoName;

/**
 * If you have a country code (iso3166-alpha2) and ADM1 code this returns the GeoName object for it.
 */
public class Adm1GeoNameLookup extends AbstractGeoNameLookup {

    public final static Logger logger = LoggerFactory.getLogger(Adm1GeoNameLookup.class);

    private static Adm1GeoNameLookup instance;
    
    public Adm1GeoNameLookup() throws IOException {
        super();
    }

    public static String getKey(String countryCode, String ADM1){
        return countryCode+"."+ADM1;
    }

    public static String getKey(CountryCode countryCode, String ADM1){
        return getKey(countryCode.name(),ADM1);
    }

    public GeoName get(String countryCode, String ADM1){
        return this.get(getKey(countryCode,ADM1));
    }
    
    @Override
    public void parse() {
        try {
            CustomLuceneLocationResolver resolver = (CustomLuceneLocationResolver) ParseManager.getResolver();
            File file = new File("src/main/resources/geonames/admin1CodesASCII.txt"); 
            LineIterator it = FileUtils.lineIterator(file, "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine().trim();
                if(line.length()==0){
                    continue;
                }
                if (line.charAt(0)=='#') {
                    continue;
                }
                String[] columns = line.split("\t");
                String key = columns[0];
                String name = columns[1];
                int geonameId = Integer.parseInt(columns[3]);
                try {
                    this.put(key, resolver.getByGeoNameId(geonameId));
                } catch (UnknownGeoNameIdException e) {
                    logger.error("Uknown geoNameId "+geonameId+" for "+name);
                }
            }
            logger.info("Loaded "+this.size()+" countries");
        } catch(Exception e){
            logger.error("Unable to load location resolver");
            logger.error(e.toString());
        }
    }
	    
    private static Adm1GeoNameLookup getInstance() throws IOException{
        if(instance==null){
            instance = new Adm1GeoNameLookup();
        }
        return instance;
    }
    
    public static GeoName lookup(String countryCodeDotAdm1Code){
        try{
            Adm1GeoNameLookup lookup = getInstance();
            GeoName geoName = lookup.get(countryCodeDotAdm1Code);
            logger.debug("Found '"+countryCodeDotAdm1Code+"': "+geoName);
            return geoName;
        } catch (IOException ioe){
            logger.error("Couldn't lookup state ADM1 geoname!");
            logger.error(ioe.toString());
        }
        return null;
    }

    public static boolean isValid(String countryCodeDotAdm1Code){
        boolean valid = false;
        try{
            Adm1GeoNameLookup lookup = getInstance();
            valid = lookup.contains(countryCodeDotAdm1Code);
        } catch (IOException ioe){
            logger.error("Couldn't lookup state ADM1 geoname!");
            logger.error(ioe.toString());
        }
        return valid;
    }
    
    public static GeoName lookup(String countryCode, String adm1Code){
        return lookup( getKey(countryCode, adm1Code) );
    }
    
}