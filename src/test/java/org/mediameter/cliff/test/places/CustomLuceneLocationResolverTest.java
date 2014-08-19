package org.mediameter.cliff.test.places;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.places.CustomLuceneLocationResolver;

import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.gazetteer.GeoName;

public class CustomLuceneLocationResolverTest {

    @Test
    public void testGetByGeoNameId() throws Exception {
        CustomLuceneLocationResolver resolver = new CustomLuceneLocationResolver(
                new File(ParseManager.PATH_TO_GEONAMES_INDEX), 10);
        GeoName geoName = resolver.getByGeoNameId(6252001);
        assertEquals(geoName.geonameID,6252001);
        assertEquals(geoName.name,"United States");
        assertEquals(geoName.featureCode,FeatureCode.PCLI);
    }
    
}
