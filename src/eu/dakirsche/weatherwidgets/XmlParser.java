package eu.dakirsche.weatherwidgets;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XmlParser implements XmlParserInterface {
	/*Klassenvariablen*/
	private String xmlDocument;
	private WeatherDataCollection weatherInformations;
	
	/*Konstruktoren*/
	public XmlParser(){
		
	}
	
	// Annehmen des XML-Strings, Verarbeitung zum document object model
	public Document getDom(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try { 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
            return doc;
    }
	
	
	public String getValue(Element item, String str) {
	    NodeList n = item.getElementsByTagName(str);
	    return this.getElementValue(n.item(0));
	}
	 
	public final String getElementValue( Node elem ) {
	         Node child;
	         if( elem != null){
	             if (elem.hasChildNodes()){
	                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  } 

public CityInformationCollection getCities(String XML){
	if (XML != null){
	Document doc =	this.getDom(XML);
	NodeList nl = doc.getElementsByTagName("item");
	
	CityInformationCollection CiCollection = new CityInformationCollection();

	
	for (int i = 0; i < nl.getLength(); i++) {
        CityInformation ci = new CityInformation();
		Element e = (Element) nl.item(i);
	    ci.setCityCode(this.getValue(e, "city_code"));
	    ci.setZipCode(this.getValue(e, "plz")); 
	    ci.setCityName(this.getValue(e, "name"));
        /*Zusatzinformationen, da US Städte keinen ZIP liefern und demnach z.B. alle Hamburg, US haben */
        String additionalLandString = this.getValue(e, "plz") + (this.getValue(e, "plz") != "" ? ", " : "") + this.getValue(e, "adm_2_name") + ", " + this.getValue(e, "adm_1_code");
	    ci.setLand(this.getValue(e, "adm_1_code"), additionalLandString);
	    CiCollection.addItem(ci);
	}
	
	return CiCollection;
	}
	else
		return null;	
	}
	
	/*Mit dieser Methode wird das XML-Konstrukt an die Klasse �bergeben*/
	@Override
	public void setXmlDocument(String xmlDocument){
		this.xmlDocument = xmlDocument;
	}
	
	/*mit dieser Methode wird die Ausgabe eines einzelnen WeatherData Objekte angefordert*/
	@Override
	public WeatherData getSingleWeatherData(){
		
		
		return new WeatherData();
	}
	@Override
	public WeatherData getSingleWeatherData(int timeValue){
		
		return new WeatherData();
	}
	@Override
	public WeatherData getSingleWeatherData(int dateValue, int timeValue){
		
		return new WeatherData();  
	}
	/*Private Deklarationen*/
	@Override
	public void parseXmlDocument(){
		/*Hier wird das XML-Dokument in ein Objekt vom Typ Weatherdata geparst*/
	}
}
