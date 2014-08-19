package org.mediameter.cliff.places;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.gazetteer.GeoName;

/**
 * If you have a country code (iso3166-alpha2) and ADM1 code this returns the GeoName object for it.
 */
public class Adm1GeoNameLookup extends AbstractGeoNameLookup {

    public final static Logger logger = LoggerFactory.getLogger(Adm1GeoNameLookup.class);

    public Adm1GeoNameLookup() throws IOException {
        super();
    }

    public GeoName get(String countryCode, String ADM1){
        return this.get(countryCode+"."+ADM1);
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
	    
}