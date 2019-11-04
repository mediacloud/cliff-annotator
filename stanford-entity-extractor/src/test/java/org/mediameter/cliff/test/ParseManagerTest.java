package org.mediameter.cliff.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;
import org.mediameter.cliff.extractor.EntityExtractor;

public class ParseManagerTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void testCountry() {
        HashMap response = ParseManager.parseFromText("This is about India the country.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("states")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("cities")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testCity() {
        HashMap response = ParseManager.parseFromText("This is focus Brooklyn the city.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("states")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("cities")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testState() {
        HashMap response = ParseManager.parseFromText("This is focus New York the state.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("states")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("cities")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("focus")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }

}
