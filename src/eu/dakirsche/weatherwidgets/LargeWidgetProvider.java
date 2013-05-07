package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Date;
import java.text.SimpleDateFormat;

public class LargeWidgetProvider extends CustomWidgetProvider{
	@Override
	protected void setWidgetType(){
		this.widgetType = WIDGET_TYPE_LARGE;
	}
	public LargeWidgetProvider(){
		super();
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds) {
        this.context = context;

        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(context);

        Cursor cur = null;

        WeatherData weatherData;

        ComponentName thisWidget = new ComponentName(context,
                LargeWidgetProvider.class);

        // Get all ids
        // int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_large);

        for (int widgetId : appWidgetIds) {
            //Fuer alle gesetzten Widgets diesen Typs
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Aktualisiere Widget #" + widgetId);
            //remoteViews.setImageViewResource(R.id.imageView_widget_small_api, R.drawable.wettercom_logo_small);

            //Informationen zum Widget aus der DB laden
            CityInformation city = wdoh.getWidgetCityInformation(widgetId);
            if (city != null){
                //CityInformation gefunden

                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "CityInformation: " + city.toString());
            /*Auf dem Widget die Textfelder beschriften*/


                remoteViews.setTextViewText(R.id.textView_widget_large_cityname, city.getCityName());
                remoteViews.setTextViewText(R.id.textView_widget_large_zip, city.getZipCode());
                remoteViews.setTextViewText(R.id.textView_widget_large_land, city.getAdditionalLandInformationsByRemovingZip());

                WeatherData weather = this.getWeatherXmlForThisWidgetPlacedCityCode(city);

                Date date = new Date(System.currentTimeMillis());     //Aktuele Zeit (System)

                if (weather != null){
                    int wCode = weather.getWeatherCode();
                    if (FunctionCollection.s_getDebugState()){
                        Log.d(TAG, "WeatherData: " + weather.toString());
                        date = weather.getDate();            // Wetterdaten Zeitstempel
                    }
                    remoteViews.setTextViewText(R.id.textView_widget_large_temperature, weather.getTemperatureMaxInt() + " °C");
                    remoteViews.setTextViewText(R.id.textView_widget_large_weather, this.getWeatherName(wCode));
                    remoteViews.setImageViewResource(R.id.imageView_widget_large_weather_icon, this.getWeatherIconResId(wCode));
                }
                else {
                    //Keine Rückgabe erhalten
                    //Derzeit wird dann nix geändert
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, "Es wurde kein Wetterdatensatz gefunden für " + city.toString());
                }
                //DateTime ausgeben
                String nowDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
                remoteViews.setTextViewText(R.id.textView_widget_large_datetime, nowDateTime);

            }
            else {    //Keine CityInformation
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "CityInformation nicht gefunden!");

                Date date = new Date(System.currentTimeMillis());

                String nowDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);

                remoteViews.setTextViewText(R.id.textView_widget_large_cityname, this.context.getString(R.string.widget_error_city));

                remoteViews.setTextViewText(R.id.textView_widget_large_zip, this.context.getString(R.string.widget_error_blank));
                remoteViews.setTextViewText(R.id.textView_widget_large_land, this.context.getString(R.string.widget_error_blank));
                remoteViews.setTextViewText(R.id.textView_widget_large_weather, this.context.getString(R.string.widget_error_blank));
                remoteViews.setTextViewText(R.id.textView_widget_large_temperature, this.context.getString(R.string.widget_error_blank));
                remoteViews.setTextViewText(R.id.textView_widget_large_datetime, nowDateTime);

                remoteViews.setImageViewResource(R.id.imageView_widget_large_weather_icon, this.getWeatherIconResId(80));
            }

            // Register an onClickListener
            Intent intent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);


            if (FunctionCollection.s_getDebugState())
                Log.i(TAG, "Display des Widgets #" + widgetId);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        } //for allWidgetIds

        wdoh.close();
	}
	
}