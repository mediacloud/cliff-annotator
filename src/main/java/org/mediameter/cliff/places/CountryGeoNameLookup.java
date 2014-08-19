package org.mediameter.cliff.places;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If you have a country code (iso3166-alpha2) this returns the GeoName object for it
 */
public class CountryGeoNameLookup extends AbstractGeoNameLookup {

    public final static Logger logger = LoggerFactory.getLogger(CountryGeoNameLookup.class);

    public CountryGeoNameLookup() throws IOException {
        super();
    }

    @Override
    public void parse() {
        try {
            CustomLuceneLocationResolver resolver = (CustomLuceneLocationResolver) ParseManager.getResolver();
            File file = new File("src/main/resources/geonames/countryInfo.txt"); 
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
                String iso3166Alpha2 = columns[0];
                String name = columns[4];
                int geonameId = Integer.parseInt(columns[16]);
                try {
                    this.put(iso3166Alpha2, resolver.getByGeoNameId(geonameId));
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