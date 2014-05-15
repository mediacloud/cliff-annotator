package org.mediameter.cliff.test.gdelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdeltCsv {

    private static final Logger logger = LoggerFactory.getLogger(GdeltCsv.class);
    
    public enum COLUMNS { GLOBALEVENTID,SQLDATE,MonthYear,Year,FractionDate,Actor1Code,Actor1Name,
        Actor1CountryCode_CAMEO3,Actor1KnownGroupCode,Actor1EthnicCode,Actor1Religion1Code,Actor1Religion2Code,
        Actor1Type1Code,Actor1Type2Code,Actor1Type3Code,Actor2Code,Actor2Name,Actor2CountryCode_CAMEO3,
        Actor2KnownGroupCode,Actor2EthnicCode,Actor2Religion1Code,Actor2Religion2Code,Actor2Type1Code,
        Actor2Type2Code,Actor2Type3Code,IsRootEvent,EventCode,EventBaseCode,EventRootCode,QuadClass,
        GoldsteinScale,NumMentions,NumSources,NumArticles,AvgTone,Actor1Geo_Type,Actor1Geo_FullName,
        Actor1Geo_CountryCode_FIPS2,Actor1Geo_ADM1Code_FIPS,Actor1Geo_Lat,Actor1Geo_Long,Actor1Geo_FeatureID,
        Actor2Geo_Type,Actor2Geo_FullName,Actor2Geo_CountryCode_FIPS2,Actor2Geo_ADM1Code_FIPS,Actor2Geo_Lat,
        Actor2Geo_Long,Actor2Geo_FeatureID,ActionGeo_Type,ActionGeo_FullName,ActionGeo_CountryCode,
        ActionGeo_ADM1Code,ActionGeo_Lat,ActionGeo_Long,ActionGeo_FeatureID,DATEADDED,SOURCEURL};
    
    private static File[] allDailyDownloadFiles(String baseDir){
        File[] files = new File(baseDir).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });
        logger.info("  Found "+files.length+" GDELT csv files in "+baseDir);
        return files;
    }
    
    public static ArrayList<GdeltEvent> allEvents(String baseDir) throws Exception {
        ArrayList<GdeltEvent> events = new ArrayList<GdeltEvent>();
        for(File csvFile : allDailyDownloadFiles(baseDir) ){
            logger.info("Loading GDELT events from "+csvFile.getName());
            BufferedReader in = new BufferedReader( new FileReader(csvFile) );
            String str = null;
            int fileEventCount = 0;
            while ((str = in.readLine()) != null) {
                if (str.trim().length() == 0) {
                        continue;
                }
                String[] values = str.split("\\t");
                String globalEventId = values[COLUMNS.GLOBALEVENTID.ordinal()];
                try{
                    URL sourceUrl = new URL(values[COLUMNS.SOURCEURL.ordinal()]);
                    /**
                     * Note: which geography tag to use?
                     * "The georeferenced location for an actor may not always match the 
                     * Actor1_CountryCode or Actor2_CountryCode field, such as in a case where the President of Russia is 
                     * visiting Washington, DC in the United States, in which case the Actor1_CountryCode would contain the 
                     * code for Russia, while the georeferencing fields below would contain a match for Washington, DC. It 
                     * may not always be possible for the system to locate a match for each actor or location, in which case 
                     * one or more of the fields may be blank"
                     * @see page 5 of http://data.gdeltproject.org/documentation/GDELT-Data_Format_Codebook.pdf
                     */
                    String actor1CountryCode = values[COLUMNS.Actor1CountryCode_CAMEO3.ordinal()].trim();
                    String actor2CountryCode = values[COLUMNS.Actor2CountryCode_CAMEO3.ordinal()].trim();
                    if(actor1CountryCode.length()==0 || actor2CountryCode.length()==0){
                        logger.debug("Skipping event "+globalEventId+" because it doesn't have two countries");
                        continue;   // skip entries with no country codes
                    }
                    GdeltEvent event = new GdeltEvent(globalEventId, new GdeltActor(actor1CountryCode), new GdeltActor(actor2CountryCode), sourceUrl);
                    events.add(event);
                    fileEventCount++;
                } catch(org.mediameter.cliff.util.UnknownCountryException uce){
                    logger.error("Uknown country "+uce.getCountryCode()+" SKIPPING this event");
                } catch(MalformedURLException mue){
                    logger.info("Skipping "+globalEventId+" because no url "+values[COLUMNS.SOURCEURL.ordinal()]);
                }
            }
            in.close();
            logger.info("  loaded "+fileEventCount+" events");
        }
        return events;
    }
    
}
