package org.mediameter.cliff.test.gdelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdeltCsv {

    private static final Logger logger = LoggerFactory.getLogger(GdeltCsv.class);

    private static String BASE_DIR = "data/gdelt/";
    
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
    
    private static File[] allDailyDownloadFiles(){
        File[] files = new File(BASE_DIR).listFiles();
        return files;
    }
    
    public static ArrayList<GdeltEvent> allEvents() throws Exception {
        ArrayList<GdeltEvent> events = new ArrayList<GdeltEvent>();
        for(File csvFile : allDailyDownloadFiles() ){
            BufferedReader in = new BufferedReader( new FileReader(csvFile) );
            String str = null;
            while ((str = in.readLine()) != null) {
                if (str.trim().length() == 0) {
                        continue;
                }
                String[] values = str.split("\\t");
                logger.debug(values[COLUMNS.SOURCEURL.ordinal()]);
            }
            in.close();
        }
        return events;
    }
    
}
