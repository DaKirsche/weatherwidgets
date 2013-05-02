package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class CustomWidgetProvider extends AppWidgetProvider{
/*Konstantendeklaration*/
	//Die drei verf�gbaren Widgets
	public static final int WIDGET_TYPE_SMALL = 1;
	public static final int WIDGET_TYPE_LARGE = 2;
	public static final int WIDGET_TYPE_FORECAST = 3;

    public static final String TAG = "CustomWidgetProvider";
	
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
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Rufe aktuelle Wetter-XML über API ab...");
        if (fn.isInternetAvaiable()){
            //Internetverbindung verfügbar
            String uri = fn.getApiCompatibleUri(city);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Generierte URI: " + uri);

            String xmlResult = fn.fetchDataFromApi(uri);

            XmlParser xmlParser = new XmlParser();
            WeatherDataCollection wcol = xmlParser.getWeather(xmlResult);

            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "WeatherDataCollection enthält : " + wcol.getSize() + " Datensätze");

            weather = wcol.getFirst();
            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(this.context);

            while (wcol.hasNext()){
                if (weather != null) {
                    weather.setCityInformation(city);
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, weather.toString());
                    wdoh.saveWeatherData(weather);
                }
               weather = wcol.getNext();
            }
            //Den letzten Datensatz auch noch speichern
            if (weather != null) {
                weather.setCityInformation(city);
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, weather.toString());
                wdoh.saveWeatherData(weather);
            }
            else if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Wetter wurde nicht erzeugt!");


            //Das aktuelle Wetter wieder laden
            weather = wdoh.getWeatherData(city.getCityCode());

        }
        else
            CustomImageToast.makeImageToast((Activity)this.context, R.drawable.icon_warning, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
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
        String weatherString = "";
        switch (weatherCode){
            case 10: weatherString = this.context.getString(R.string.weather_code_lightcloudy);break;
            case 20: weatherString = this.context.getString(R.string.weather_code_cloudy);break;
            default: weatherString = this.context.getString(R.string.weather_code_unknown);break;
        }

        return weatherString;
    }
}