package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

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
	  int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	  
	  for (int widgetId : allWidgetIds) {
		  //Für alle gesetzten Widgets diesen Typs
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
		          R.layout.widget_layout_large);
		  
		  
		  //  remoteViews.setTextViewText(R.id.widget_title, "DesktopNotes");
		  remoteViews.setImageViewResource(R.id.imageView_widget_large_weather_icon, R.drawable.regen);
		  remoteViews.setImageViewResource(R.id.imageView_widget_large_api, R.drawable.wettercom_logo_small);

          CityInformation city = wdoh.getWidgetCityInformation(widgetId);
          if (city != null){
              //CityInformation gefunden

            /*Auf dem Widget die Textfelder beschriften*/
              remoteViews.setTextViewText(R.id.textView_widget_large_cityname, city.getCityName());

              WeatherData weather = this.getWeatherXmlForThisWidgetPlacedCityCode(city);
              if (weather != null){
                  remoteViews.setTextViewText(R.id.textView_widget_large_temperature, weather.getTemperatureMax().toString());
                  remoteViews.setTextViewText(R.id.textView_widget_large_weather, this.getWeatherName(weather.getWeatherCode()));
              }
              else {
                  //Keine Rückgabe erhalten
                  //Derzeit wird dann nix geändert
              }

          }
	
	   // intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	   // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	    Intent intent = new Intent(context, MainActivity.class);
	   // intent.putExtra("startPoint", "WidgetSettingsDetailActivity");
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
	    remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
	    
	    appWidgetManager.updateAppWidget(widgetId, remoteViews);
	  } //for allWidgetIds
	  
	  wdoh.close();
	}
	
}