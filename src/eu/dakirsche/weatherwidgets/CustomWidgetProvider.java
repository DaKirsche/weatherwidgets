package eu.dakirsche.weatherwidgets;

import android.appwidget.AppWidgetProvider;
import android.content.Context;

public abstract class CustomWidgetProvider extends AppWidgetProvider{
/*Konstantendeklaration*/
	//Die drei verf�gbaren Widgets
	public static final int WIDGET_TYPE_SMALL = 1;
	public static final int WIDGET_TYPE_LARGE = 2;
	public static final int WIDGET_TYPE_FORECAST = 3;
	
/*Klassenvariablen*/
	//Information �ber den aktuellen WidgetType
	protected int widgetType;
    protected Context context;
	/**
     * Abrufen der Wetterinformationen
     */
    protected WeatherData getWeatherXmlForThisWidgetPlacedCityCode(CityInformation city){
        /*Aktualisiere Wetterdaten*/
        FunctionCollection fn = new FunctionCollection(this.context);
        WeatherData weather = null;
        if (fn.isInternetAvaiable()){
            //Internetverbindung verfügbar
            String uri = fn.getApiCompatibleUri(city);
            String xmlResult = fn.fetchDataFromApi(uri);

            XmlParser xmlParser = new XmlParser();
            WeatherDataCollection wcol = xmlParser.getWeather(xmlResult);
            weather = wcol.getFirst();
            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(this.context);

            while (wcol.hasNext()){
               weather.setCityInformation(city);
               wdoh.saveWeatherData(weather);
               weather = wcol.getNext();
            }
            //Den letzten Datensatz auch noch speichern
            weather.setCityInformation(city);
            wdoh.saveWeatherData(weather);

            //Das aktuelle Wetter wieder laden
            weather = wdoh.getWeatherData(city.getCityCode());

        }
        return weather;
    }
	/**
	 * Legt den eigenen WidgetType anhand der Konstanten WIDGET_TYPE fest
	 */
	protected abstract void setWidgetType();
	
	public CustomWidgetProvider(){
		super();
		this.setWidgetType();
	}

    /**
     * Methode für die Umwandlung von WeatherCodes zu entsprechdndem Text
     * @param weatherCode  Wettercode lt. Wetter.com API
     * @return lesbare bezeichnung des Wetters als String (z.B. Sonne)
     */
    protected String getWeatherName(int weatherCode){
        return "Sonne";
    }
}