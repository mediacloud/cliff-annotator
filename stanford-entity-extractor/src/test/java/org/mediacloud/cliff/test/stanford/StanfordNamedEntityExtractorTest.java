package org.mediacloud.cliff.test.stanford;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.mediacloud.cliff.CliffConfig;
import org.mediacloud.cliff.extractor.EntityExtractor;
import org.mediacloud.cliff.extractor.ExtractedEntities;
import org.mediacloud.cliff.extractor.OrganizationOccurrence;
import org.mediacloud.cliff.extractor.PersonOccurrence;
import org.mediacloud.cliff.stanford.StanfordNamedEntityExtractor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.bericotech.clavin.extractor.LocationOccurrence;

public class StanfordNamedEntityExtractorTest {

    public final static Logger logger = LoggerFactory.getLogger(StanfordNamedEntityExtractorTest.class);
    
    public void testOrgSpanish() {
    	CliffConfig config = CliffConfig.getInstance();
        EntityExtractor extractor = new StanfordNamedEntityExtractor();
        try {
			extractor.initialize(config);
			ExtractedEntities entities = extractor.extractEntities("Instituciones clave: Instituto del Niño y Adolescente del Uruguay (INAU), Administración Nacional de Educación Pública (ANEP), Educación Privada, Universidades, Ministerio de Educación y Cultura (MEC), Centros de formación militar.", false, EntityExtractor.SPANISH);
			List<PersonOccurrence> people = entities.getPeople();
			List<LocationOccurrence> places = entities.getLocations();
			List<OrganizationOccurrence> orgs = entities.getOrganizations();
			assertEquals( 8, orgs.size() );
			assertEquals( 0, places.size() );
			assertEquals( 0, people.size() );
			assertEquals( "Ministerio de Educación y Cultura", orgs.get(5).text);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testPersonSpanish() {
        CliffConfig config = CliffConfig.getInstance();
        EntityExtractor extractor = new StanfordNamedEntityExtractor();
        try {
			extractor.initialize(config);
			ExtractedEntities entities = extractor.extractEntities("David Bowie toma las calles del mundo.", false, EntityExtractor.SPANISH);
			List<PersonOccurrence> people = entities.getPeople();
			List<LocationOccurrence> places = entities.getLocations();
			List<OrganizationOccurrence> orgs = entities.getOrganizations();
			assertEquals( 0, orgs.size() );
			assertEquals( 0, places.size() );
			assertEquals( 1, people.size() );
			assertEquals( "David Bowie", people.get(0).text);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    @Test
    public void testCountrySpanish() {
        CliffConfig config = CliffConfig.getInstance();
        EntityExtractor extractor = new StanfordNamedEntityExtractor();
        try {
			extractor.initialize(config);
			ExtractedEntities entities = extractor.extractEntities("Esto es sobre la ciudad de Berlin.", false, EntityExtractor.SPANISH);
			List<PersonOccurrence> people = entities.getPeople();
			List<LocationOccurrence> places = entities.getLocations();
			List<OrganizationOccurrence> orgs = entities.getOrganizations();
			assertEquals( 0, orgs.size() );
			assertEquals( 0, people.size() );
			assertEquals( 1, places.size() );
			assertEquals( "Berlin", places.get(0).getText());
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
