package eu.dakirsche.weatherwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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


        this.context = context;

        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(context);

        Cursor cur = null;

        WeatherData weatherData;

        ComponentName thisWidget = new ComponentName(context,
                ForecastWidgetProvider.class);

        // Get all ids
        // int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_forecast);

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
                remoteViews.setTextViewText(R.id.textView_forecastwidget_city, city.getCityName() + ", " +city.getLandCode() + " " + city.getZipCode());

                WeatherData weather = this.getWeatherXmlForThisWidgetPlacedCityCode(city, true);
                if (weather != null){
                    int wCode = weather.getWeatherCode();
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, "WeatherData: " + weather.toString());

                    Date nowDt = new Date(System.currentTimeMillis());

                    Calendar c = Calendar.getInstance();
                    c.setTime(nowDt);
                    c.set(Calendar.HOUR_OF_DAY, 11);
                    c.set(Calendar.MINUTE, 00);
                    c.add(Calendar.DATE, 1);  // number of days to add
                    WeatherData tomorrow = wdoh.getWeatherData(city.getCityCode(), c.getTime());
                    c.add(Calendar.DATE, 1);  // number of days to add
                    WeatherData afterTomorrow = wdoh.getWeatherData(city.getCityCode(), c.getTime());


                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_0, weather.getTemperatureMaxInt() + " °C");
                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_txt, this.getWeatherName(wCode));
                    remoteViews.setImageViewResource(R.id.widget_forecast_icon_today, this.getWeatherIconResId(wCode));

                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_1, tomorrow.getTemperatureMaxInt() + " °C");
                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_txt, this.getWeatherName(tomorrow.getWeatherCode()));
                    remoteViews.setImageViewResource(R.id.widget_forecast_icon_1, this.getWeatherIconResId(tomorrow.getWeatherCode()));

                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_2, afterTomorrow.getTemperatureMaxInt() + " °C");
                    remoteViews.setTextViewText(R.id.textView_forecastwidget_today_txt, this.getWeatherName(afterTomorrow.getWeatherCode()));
                    remoteViews.setImageViewResource(R.id.widget_forecast_icon_2, this.getWeatherIconResId(afterTomorrow.getWeatherCode()));
                   // remoteViews.setTextViewText(R.id.textView_forecastwidget_today_3, weather.getTemperatureMaxInt() + " °C");
                   // remoteViews.setImageViewResource(R.id.widget_forecast_icon_3, this.getWeatherIconResId(wCode));
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

                remoteViews.setTextViewText(R.id.textView_widget_small_city, "ERROR");
            }
            /*Tagesbezeichnungen laden*/
            remoteViews.setTextViewText(R.id.textView_forecastwidget_nametoday, this.context.getString(R.string.weekday_tod));
            remoteViews.setTextViewText(R.id.textView_forecastwidget_name_1, this.context.getString(R.string.weekday_tom));

            remoteViews.setTextViewText(R.id.textView_forecastwidget_name_2, getDayNameOfTodayAddingDays(2));
          //  remoteViews.setTextViewText(R.id.textView_forecastwidget_name_3, getDayNameOfTodayAddingDays(3));


            // Register an onClickListener
            Intent intent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.widgetLayoutForecast, pendingIntent);


            if (FunctionCollection.s_getDebugState())
                Log.i(TAG, "Display des Widgets #" + widgetId);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        } //for allWidgetIds

        wdoh.close();
	}
    private String getDayNameOfTodayAddingDays(int addDays){
        GregorianCalendar oCalendar = new GregorianCalendar();
        int iWDay =oCalendar.get(GregorianCalendar.DAY_OF_WEEK);
        iWDay = ((iWDay + addDays) % 7) + 1;
        String dayName = "";
        switch (iWDay) {
            case 1:
                dayName = this.context.getString(R.string.weekday_sun);
                break;
            case 2:
                dayName = this.context.getString(R.string.weekday_mon);
                break;
            case 3:
                dayName = this.context.getString(R.string.weekday_tue);
                break;
            case 4:
                dayName = this.context.getString(R.string.weekday_wed);
                break;
            case 5:
                dayName = this.context.getString(R.string.weekday_thu);
                break;
            case 6:
                dayName = this.context.getString(R.string.weekday_fri);
                break;
            case 7:
                dayName = this.context.getString(R.string.weekday_sat);
                break;
            default:
                dayName = this.context.getString(R.string.weekday_som);
                break;
        }

        return dayName;
    }
	
}