package eu.dakirsche.weatherwidgets;

public interface XmlParserInterface
{
		public void setXmlDocument(String xmlDocument);
		
		public WeatherData getSingleWeatherData();
		public WeatherData getSingleWeatherData(int timeValue);
		public WeatherData getSingleWeatherData(int dateValue, int timeValue);
		
		public void parseXmlDocument();
}
