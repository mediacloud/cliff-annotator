package org.mediameter.cliff.test.reuters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mediameter.cliff.test.places.aboutness.NYTAboutnessCheck;
import org.mediameter.cliff.util.ISO3166Utils;
import org.mediameter.cliff.util.UnknownCountryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bericotech.clavin.gazetteer.CountryCode;

/**
 * RCV1 doc wrapper
 * @author rahulb
 *
 */
public class ReutersCorpusDocument {

    private static final Logger logger = LoggerFactory.getLogger(ReutersCorpusDocument.class);

    private static String CODES_TAG = "codes";
    
    private String id;
    private String title;
    private String headline;
    private String text;
    private String dateline;
    private ArrayList<String> countryCodes;
    
    public ReutersCorpusDocument(){
        countryCodes = new ArrayList<String>();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getCountryCodes() {
        return countryCodes;
    }

    public ArrayList<CountryCode> getCountryCodeObjects() {
        ArrayList<CountryCode> countryCodeObjects = new ArrayList<CountryCode>(); 
        for(String code: countryCodes){
            countryCodeObjects.add(CountryCode.valueOf(code));
        }
        return countryCodeObjects;
    }

    public void addCountryCode(String countryCode){
        countryCodes.add(countryCode);
    }
    
    public boolean hasCodedCountries() {
        return countryCodes.size()>0;
    }
    
    public String getCompiledText(){
        return getHeadline() + ". " + getDateline()+"." +getText();
    }

    public static ReutersCorpusDocument fromFile(String path) throws IOException, ParserConfigurationException, SAXException {
        ReutersCorpusDocument reutersDoc = new ReutersCorpusDocument();
        // load the xml file
        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        // grab the basics
        NodeList newsItemList = doc.getElementsByTagName("newsitem");
        reutersDoc.setId( ((Element) newsItemList.item(0)).getAttribute("itemid") );
        NodeList titleList = doc.getElementsByTagName("title");
        reutersDoc.setTitle( titleList.item(0).getTextContent() );
        NodeList headlineList = doc.getElementsByTagName("headline");
        reutersDoc.setHeadline( headlineList.item(0).getTextContent() );
        NodeList textList = doc.getElementsByTagName("text");
        reutersDoc.setText( textList.item(0).getTextContent() );
        NodeList datelineText = doc.getElementsByTagName("dateline");
        reutersDoc.setDateline( datelineText.item(0).getTextContent() );
        // fill in any countries
        NodeList codesNodeList = doc.getElementsByTagName(CODES_TAG);
        for (int codesIdx = 0; codesIdx < codesNodeList.getLength(); codesIdx++) {
            Node codesNode = codesNodeList.item(codesIdx);
            if (codesNode.getNodeType() == Node.ELEMENT_NODE) {
                Element codesElement = (Element) codesNode;
                if("bip:countries:1.0".equals(codesElement.getAttribute("class"))){
                    NodeList codeNodeList = codesElement.getElementsByTagName("code");
                    for (int codeIdx = 0; codeIdx < codeNodeList.getLength(); codeIdx++) {
                        Node codeNode = codeNodeList.item(codeIdx);
                        if (codeNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element codeElement = (Element) codeNode;
                            String reutersCountryCode = codeElement.getAttribute("code");
                            try {
                                switch(reutersCountryCode){
                                case "RUSS": reutersCountryCode="RUS";break;                                    
                                }
                                reutersDoc.addCountryCode( ISO3166Utils.alpha3toalpha2(reutersCountryCode) );
                            } catch (UnknownCountryException e) {
                                logger.error("Unkown country in reuters data - "+reutersCountryCode);
                            }
                        }
                    }
                }
            }
     
        }
        return reutersDoc;
    }    
    
}
