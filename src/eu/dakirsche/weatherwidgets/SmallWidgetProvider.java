package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

public class SmallWidgetProvider extends CustomWidgetProvider{
	protected void setWidgetType(){
		this.widgetType = WIDGET_TYPE_LARGE;
	}
	public SmallWidgetProvider(){
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
	      SmallWidgetProvider.class);
	  
	  // Get all ids
	 // int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
            R.layout.widget_layout_small);

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
            remoteViews.setTextViewText(R.id.textView_widget_small_city, city.getCityName());

            WeatherData weather = this.getWeatherXmlForThisWidgetPlacedCityCode(city);
            if (weather != null){
                int wCode = weather.getWeatherCode();
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "WeatherData: " + weather.toString());

                remoteViews.setTextViewText(R.id.textView_widget_small_temperature, weather.getTemperatureMaxInt() + " °C");
                remoteViews.setTextViewText(R.id.textView_widget_small_weather, this.getWeatherName(wCode));
                remoteViews.setImageViewResource(R.id.imageView_widget_small_weather_icon, this.getWeatherIconResId(wCode));
            }
            else {
                //Keine Rückgabe erhalten
                //Derzeit wird dann nix geändert
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Es wurde kein Wetterdatensatz gefunden für " + city.toString());
            }

        }
        else {
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "CityInformation nicht gefunden!");

            remoteViews.setTextViewText(R.id.textView_widget_small_city, this.context.getString(R.string.widget_error_city));
            remoteViews.setTextViewText(R.id.textView_widget_small_weather, this.context.getString(R.string.widget_error_blank));
            remoteViews.setTextViewText(R.id.textView_widget_small_temperature, this.context.getString(R.string.widget_error_blank));

            remoteViews.setImageViewResource(R.id.imageView_widget_small_weather_icon, this.getWeatherIconResId(30));

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