package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
	  int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	  
	  for (int widgetId : allWidgetIds) {
		  //Fuer alle gesetzten Widgets diesen Typs
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
		          R.layout.widget_layout_small);
		  
		  
		  //  remoteViews.setTextViewText(R.id.widget_title, "DesktopNotes");
		  remoteViews.setImageViewResource(R.id.imageView_widget_small_weather_icon, R.drawable.regen);
		  remoteViews.setImageViewResource(R.id.imageView_widget_small_api, R.drawable.wettercom_logo_small);

        //Informationen zum Widget aus der DB laden
        CityInformation city = wdoh.getWidgetCityInformation(widgetId);
        if (city != null){
            //CityInformation gefunden

            /*Auf dem Widget die Textfelder beschriften*/
            remoteViews.setTextViewText(R.id.textView_widget_small_city, city.getCityName());

            WeatherData weather = this.getWeatherXmlForThisWidgetPlacedCityCode(city);
            if (weather != null){
                remoteViews.setTextViewText(R.id.textView_widget_small_temperature, weather.getTemperatureMax().toString() + "°C");
                remoteViews.setTextViewText(R.id.textView_widget_small_weather, this.getWeatherName(weather.getWeatherCode()));
            }
            else {
                //Keine Rückgabe erhalten
                //Derzeit wird dann nix geändert
            }

        }

	    // Register an onClickListener
	    Intent intent = new Intent(context, MainActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
	    remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
	    
	    appWidgetManager.updateAppWidget(widgetId, remoteViews);
	  } //for allWidgetIds
	  
	  wdoh.close();
	}
	
}