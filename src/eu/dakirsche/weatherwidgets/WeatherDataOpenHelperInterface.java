package eu.dakirsche.weatherwidgets;

public interface WeatherDataOpenHelperInterface
{

		/*Klassenkonstanten*/
		public static final String DATABASE_NAME = "WeatherWidgets";
		public static final int DATABASE_VERSION = 1;

		public WeatherData getWeatherData(String cityCode);
		public WeatherData getWeatherData(String cityCode, int time);
		public WeatherData getWeatherData(String cityCode, int time, int date);
		
		public WeatherDataCollection getWeatherSequence(int startDay, int endDay);
		public WeatherDataCollection getWeatherSequence(int startDay, int endDay, int startTime, int endTime);
		
		public CityInformation loadCityInformation(String cityCode);
		public boolean saveWeatherData(WeatherData importableWeatherData);
		
		public void setOptionKey(String keyname, String keyvalue);
		public String getOptionKey(String keyname);
		
		public CityInformationCollection getActiveCityCodesForSync();
		
		public void close();
}
