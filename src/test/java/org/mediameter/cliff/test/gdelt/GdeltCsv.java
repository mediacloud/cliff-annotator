package org.mediameter.cliff.test.gdelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdeltCsv {

    private static final Logger logger = LoggerFactory.getLogger(GdeltCsv.class);
    
    public enum COLUMNS { GLOBALEVENTID,SQLDATE,MonthYear,Year,FractionDate,Actor1Code,Actor1Name,
        Actor1CountryCode,Actor1KnownGroupCode,Actor1EthnicCode,Actor1Religion1Code,Actor1Religion2Code,
        Actor1Type1Code,Actor1Type2Code,Actor1Type3Code,Actor2Code,Actor2Name,Actor2CountryCode,
        Actor2KnownGroupCode,Actor2EthnicCode,Actor2Religion1Code,Actor2Religion2Code,Actor2Type1Code,
        Actor2Type2Code,Actor2Type3Code,IsRootEvent,EventCode,EventBaseCode,EventRootCode,QuadClass,
        GoldsteinScale,NumMentions,NumSources,NumArticles,AvgTone,Actor1Geo_Type,Actor1Geo_FullName,
        Actor1Geo_CountryCode,Actor1Geo_ADM1Code,Actor1Geo_Lat,Actor1Geo_Long,Actor1Geo_FeatureID,
        Actor2Geo_Type,Actor2Geo_FullName,Actor2Geo_CountryCode,Actor2Geo_ADM1Code,Actor2Geo_Lat,
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
                GdeltActor actor1 = new GdeltActor(
                        values[COLUMNS.Actor1Geo_CountryCode.ordinal()], 
                        values[COLUMNS.Actor1Geo_ADM1Code.ordinal()], 
                        values[COLUMNS.Actor1Geo_Lat.ordinal()], 
                        values[COLUMNS.Actor1Geo_Long.ordinal()], 
                        values[COLUMNS.Actor1Geo_FeatureID.ordinal()], 
                        values[COLUMNS.Actor1Geo_Type.ordinal()], 
                        values[COLUMNS.Actor1Geo_FullName.ordinal()]);
                GdeltActor actor2 = new GdeltActor(
                        values[COLUMNS.Actor2Geo_CountryCode.ordinal()], 
                        values[COLUMNS.Actor2Geo_ADM1Code.ordinal()], 
                        values[COLUMNS.Actor2Geo_Lat.ordinal()], 
                        values[COLUMNS.Actor2Geo_Long.ordinal()], 
                        values[COLUMNS.Actor2Geo_FeatureID.ordinal()], 
                        values[COLUMNS.Actor2Geo_Type.ordinal()], 
                        values[COLUMNS.Actor2Geo_FullName.ordinal()]);
                GdeltEvent event = new GdeltEvent(actor1, actor2, values[COLUMNS.SOURCEURL.ordinal()]);
                events.add(event);
                fileEventCount++;
            }
            in.close();
            logger.info("  loaded "+fileEventCount+" events");
        }
        return events;
    }
    
}
