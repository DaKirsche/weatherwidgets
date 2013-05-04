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
            WeatherDataCollection wcol = null;
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Generierte URI: " + uri);

            try {
                String xmlResult = fn.fetchDataFromApi(uri);

                XmlParser xmlParser = new XmlParser();
                wcol = xmlParser.getWeather(xmlResult);
            } catch (Exception e){
                if (FunctionCollection.s_getDebugState())
                    Log.e(TAG, "Fehler bei der Abfrage der Wetterdaten: ", e);
            }



            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "WeatherDataCollection enthält : " + wcol.getSize() + " Datensätze");

            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(this.context);

            if (wcol != null){
                weather = wcol.getFirst();

                while (wcol.hasNext()){
                    try {
                        if (weather != null) {
                            weather.setCityInformation(city);
                            if (FunctionCollection.s_getDebugState())
                                Log.d(TAG, weather.toString());
                            wdoh.saveWeatherData(weather);
                        }
                    }
                    catch (Exception e){
                        if (FunctionCollection.s_getDebugState())
                            Log.e(TAG, "Fehler beim Speichern des Wetterdatensatzes " + weather.toString(), e);
                    }
                   weather = wcol.getNext();
                }
                //Den letzten Datensatz auch noch speichern
                if (weather != null) {
                    try {
                        weather.setCityInformation(city);
                        if (FunctionCollection.s_getDebugState())
                            Log.d(TAG, weather.toString());
                        wdoh.saveWeatherData(weather);
                    }
                    catch (Exception e){
                        if (FunctionCollection.s_getDebugState())
                            Log.e(TAG, "Fehler beim Speichern des Wetterdatensatzes " + weather.toString(), e);
                    }
                }
                else if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Wetter wurde nicht erzeugt!");
            }
            else {
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Keine Wetterdaten gespeichert!");
            }

            //Das aktuelle Wetter wieder laden
            weather = wdoh.getWeatherData(city.getCityCode());

            /*WENN weather NULL, dann einen Wert auf der WeatherDataCollection selbst auslesen*/
            /* Eine Xml Struktur beinhaltet 4 WeatherData von 4, 11, 17 und 0 Uhr und wir wählen, wenn keiner aus DB geladen wird, den Mittagswert (11Uhr)*/
            if (weather == null)  {
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Keine Wetterdaten aus DB erhalten. Wähle eigenständig aus WeatherCollection");
                if (wcol != null)
                    weather = wcol.getItemAtPos(wcol.getSize() - 2);
                else if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Konnte keine Wetterdaten laden!");
            }

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
            case 30: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 40: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 50: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 60: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 70: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 80: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            case 90: weatherString = this.context.getString(R.string.weather_code_unknown);break;
            default: weatherString = this.context.getString(R.string.weather_code_unknown);break;
        }
        if (FunctionCollection.s_getDebugState())
            Log.i(TAG, "Gefundener Wetterbezeichner für WetterCode " + weatherCode + " => " + weatherString);
        return weatherString;
    }
    /**
     * Methode für die Auswetung von WeatherCodes zu entsprechendem Icon
     * @param weatherCode  Wettercode lt. Wetter.com API
     * @return ResId des Icons
     */
    protected int getWeatherIconResId(int weatherCode){
        int weatherIconResId = 0;

        switch (weatherCode){
            case 10: weatherIconResId = R.drawable.d_1_b;break;
            case 20: weatherIconResId = R.drawable.d_2_b;break;
            case 30: weatherIconResId = R.drawable.d_3_b;break;
            case 40: weatherIconResId = R.drawable.d_4_b;break;
            case 50: weatherIconResId = R.drawable.d_5_b;break;
            case 60: weatherIconResId = R.drawable.d_6_b;break;
            case 70: weatherIconResId = R.drawable.d_7_b;break;
            case 80: weatherIconResId = R.drawable.d_8_b;break;
            case 90: weatherIconResId = R.drawable.d_9_b;break;
            default: weatherIconResId = R.drawable.d_0_b;break;
        }
        if (FunctionCollection.s_getDebugState())
            Log.i(TAG, "Gefundener ImageResource für WetterCode " + weatherCode + " => " + weatherIconResId);
        return weatherIconResId;
    }
}