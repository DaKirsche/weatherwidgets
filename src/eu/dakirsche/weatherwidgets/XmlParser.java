package eu.dakirsche.weatherwidgets;

public class XmlParser implements XmlParserInterface {
	/*Klassenvariablen*/
	private String xmlDocument;
	private WeatherDataCollection weatherInformations;
	
	/*Konstruktoren*/
	public XmlParser(){
		
	}
	
	/*Mit dieser Methode wird das XML-Konstrukt an die Klasse übergeben*/
	public void setXmlDocument(String xmlDocument){
		this.xmlDocument = xmlDocument;
	}
	
	/*mit dieser Methode wird die Ausgabe eines einzelnen WeatherData Objekte angefordert*/
	public WeatherData getSingleWeatherData(){
		
		
		return new WeatherData();
	}
	public WeatherData getSingleWeatherData(int timeValue){
		
		return new WeatherData();
	}
	public WeatherData getSingleWeatherData(int dateValue, int timeValue){
		
		return new WeatherData();  
	}
	/*Private Deklarationen*/
	public void parseXmlDocument(){
		/*Hier wird das XML-Dokument in ein Objekt vom Typ Weatherdata geparst*/
	}
}
