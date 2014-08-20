package org.mediameter.cliff.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.mediameter.cliff.ParseManager;

public class ParseManagerTest {

    @Test
    public void testCountry() {
        HashMap response = ParseManager.parseFromText("This is about India the country.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("states")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("cities")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }

    @Test
    public void testCity() {
        HashMap response = ParseManager.parseFromText("This is about Brooklyn the city.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("states")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("cities")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }

    @Test
    public void testState() {
        HashMap response = ParseManager.parseFromText("This is about New York the state.", false);
        HashMap results = (HashMap) response.get("results");
        assertEquals( ((ArrayList)((HashMap) results.get("places")).get("mentions")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("states")).size(),1 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("cities")).size(),0 );
        assertEquals( ((ArrayList)((HashMap)((HashMap) results.get("places")).get("about")).get("countries")).size(),1 );
        assertEquals( ((ArrayList) results.get("people")).size(),0 );
        assertEquals( ((ArrayList) results.get("organizations")).size(),0 );
    }

}
