package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class ForecastWidgetProvider extends CustomWidgetProvider{
	@Override
	protected void setWidgetType(){
		this.widgetType = WIDGET_TYPE_FORECAST;
	}
	public ForecastWidgetProvider(){
		super();
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds) {
	
	  
		WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(context);
		
		Cursor cur = null;
		
		WeatherData weatherData;
		
	  ComponentName thisWidget = new ComponentName(context,
	      ForecastWidgetProvider.class);
	  
	  // Get all ids
	  int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	  
	  for (int widgetId : allWidgetIds) {
		  //Für alle gesetzten Widgets diesen Typs
		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
		          R.layout.widget_layout_forecast);
		  
		  
		  //  remoteViews.setTextViewText(R.id.widget_title, "DesktopNotes");
		    
	    // Register an onClickListener
	
	   // intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	   // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	    Intent intent = new Intent(context, MainActivity.class);
	   // intent.putExtra("startPoint", "WidgetSettingsDetailActivity");
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
	    remoteViews.setOnClickPendingIntent(R.id.widgetLayoutForecast, pendingIntent);
	    
	    appWidgetManager.updateAppWidget(widgetId, remoteViews);
	  } //for allWidgetIds
	  
	  wdoh.close();
	}
	
}