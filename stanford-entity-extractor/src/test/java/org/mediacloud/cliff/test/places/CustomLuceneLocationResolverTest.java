package org.mediacloud.cliff.test.places;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.mediacloud.cliff.CliffConfig;
import org.mediacloud.cliff.places.CliffLocationResolver;

import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;

public class CustomLuceneLocationResolverTest {

    @Test
    public void testGetByGeoNameId() throws Exception {
        Gazetteer gazetteer = new LuceneGazetteer(new File(CliffConfig.getInstance().getPathToGeonamesIndex()));
        CliffLocationResolver resolver = new CliffLocationResolver(gazetteer);
        GeoName geoName = resolver.getByGeoNameId(6252001);
        assertEquals(geoName.getGeonameID(),6252001);
        assertEquals(geoName.getName(),"United States");
        assertEquals(geoName.getFeatureCode(),FeatureCode.PCLI);
    }
    
}
