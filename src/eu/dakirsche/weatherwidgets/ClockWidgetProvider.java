package eu.dakirsche.weatherwidgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockWidgetProvider extends CustomWidgetProvider{
    /* INTENT BEZEICHNUNG FÜR UHRZEITAKTUALISIERUNG */
    public static String CLOCK_WIDGET_UPDATE = "eu.dakirsche.weatherwidgets.CLOCK_WIDGET_UPDATE";

	@Override
	protected void setWidgetType(){
		this.widgetType = WIDGET_TYPE_WEATHERCLOCK;
	}
	public ClockWidgetProvider(){
		super();
	}

    /**
     * Aktualisierung des Widgets per Widget-Update Interval
     * @param context Anwendungscontext
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds  WidgetIds des WidgetsTyps
     */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds) {
        this.context = context;

        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(context);

        ComponentName thisWidget = new ComponentName(context,
                ClockWidgetProvider.class);


        // Get all ids
         int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        /*Widgets diesen Typs bereinigen*/
        wdoh.removeOldWidgets(allWidgetIds, WIDGET_TYPE_WEATHERCLOCK);

        for (int widgetId : allWidgetIds) {
            //Fuer alle gesetzten Widgets diesen Typs
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Aktualisiere Widget #" + widgetId);

            CityInformation city = wdoh.getWidgetCityInformation(widgetId);

            if (city != null){
               this.getWeatherXmlForThisWidgetPlacedCityCode(city);
            }
            //Widget aktualisieren
            this.updateAppWidget(context, appWidgetManager, widgetId);
        } //for allWidgetIds

        wdoh.close();
	}

    /**
     * Erzeugt einen AlertTimer, damit die Uhrzeit zeitnah aktualisiert wird
     * @param context Anwendungscontext
     * @return Intent des AlertTimers
     */
    private PendingIntent createClockTickIntent(Context context) {
            Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

    /**
     * Wird aufgerufen, wenn das erste Widgets diesen Typs platziert wurde
     * @param context  Anwendungscontext
     */
    @Override
    public void onEnabled(Context context) {
                super.onEnabled(context);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);
                alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 10000
                , createClockTickIntent(context));
        }

    /**
     * Wird aufgerufen, wenn das letzte Widget diesen Typs entfernt wurde
     * @param context Anwendungscontext
     */
    @Override
    public void onDisabled(Context context) {
                super.onDisabled(context);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(createClockTickIntent(context));
    }

    /**
     * Wird aufgerufen, wenn der Widgetprovider aktiviert wird (per Timer oder WidgetUpdate)
     * @param context Anwendungscontext
     * @param intent Aufrufender Befehl
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            this.context = context;
            for (int appWidgetID: ids) {
                    updateAppWidget(context, appWidgetManager, appWidgetID);
                }
        }
        else onUpdate(context, appWidgetManager, ids);
    }

    /**
     * Aktuaisiert das Widget
     * @param context Anwendungscontext
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetID ID des zu aktualisierenden Widgets
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetID) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout_weatherclock);

        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(context);

        CityInformation city = wdoh.getWidgetCityInformation(appWidgetID);
        if (city != null){
            //CityInformation gefunden
            /*Auf dem Widget die Textfelder beschriften*/


            remoteViews.setTextViewText(R.id.widget_wc_cityname, city.getCityName());
            remoteViews.setTextViewText(R.id.widget_wc_plz, city.getZipCode());
            remoteViews.setTextViewText(R.id.widget_wc_addit, city.getAdditionalLandInformationsByRemovingZip());

            WeatherData weather = wdoh.getWeatherData(city.getCityCode());

            if (weather != null){
                int wCode = weather.getWeatherCode();
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "WeatherData: " + weather.toString());
                remoteViews.setTextViewText(R.id.widget_wc_degree, weather.getTemperatureMaxInt() + context.getString(R.string.caption_degree));
                remoteViews.setTextViewText(R.id.widget_wc_weathername, this.getWeatherName(wCode));
                remoteViews.setImageViewResource(R.id.widget_wc_weather, this.getWeatherIconResId(wCode));
            }
            else {
                //Keine Rückgabe erhalten
                //Derzeit wird dann nix geändert
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Es wurde kein Wetterdatensatz gefunden für " + city.toString());
            }
            //DateTime ausgeben
            //String nowDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
            //remoteViews.setTextViewText(R.id.textView_widget_large_datetime, nowDateTime);

        }
        else {    //Keine CityInformation
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "CityInformation nicht gefunden!");

            remoteViews.setTextViewText(R.id.widget_wc_cityname, context.getString(R.string.widget_error_city));

            remoteViews.setTextViewText(R.id.widget_wc_plz, context.getString(R.string.widget_error_blank));
            remoteViews.setTextViewText(R.id.widget_wc_degree, context.getString(R.string.widget_error_blank));
            remoteViews.setTextViewText(R.id.widget_wc_weathername, context.getString(R.string.widget_error_blank));
            remoteViews.setTextViewText(R.id.widget_wc_addit, context.getString(R.string.widget_error_blank));

            remoteViews.setImageViewResource(R.id.widget_wc_weather, this.getWeatherIconResId(80));
        }

         /* Uhrzeit können wir definitiv darstellen */
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String nowTime = sdf.format(new Date());
        int currentLetter = 0;
        currentLetter = Integer.parseInt(nowTime.substring(0,1));
        remoteViews.setImageViewResource(R.id.widget_wc_h1, this.getLetterResourceId(currentLetter));

        currentLetter = Integer.parseInt(nowTime.substring(1,2));
        remoteViews.setImageViewResource(R.id.widget_wc_h2, this.getLetterResourceId(currentLetter));

        currentLetter = Integer.parseInt(nowTime.substring(2,3));
        remoteViews.setImageViewResource(R.id.widget_wc_m1, this.getLetterResourceId(currentLetter));

        currentLetter = Integer.parseInt(nowTime.substring(3,4));
        remoteViews.setImageViewResource(R.id.widget_wc_m2, this.getLetterResourceId(currentLetter));

        // Register an onClickListener
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.widgetLayoutClock, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
        wdoh.close();
    }

    private int getLetterResourceId(int letter){
        int resId = 0;
        switch (letter){
            case 1: resId = R.drawable.time_1;break;
            case 2: resId = R.drawable.time_2;break;
            case 3: resId = R.drawable.time_3;break;
            case 4: resId = R.drawable.time_4;break;
            case 5: resId = R.drawable.time_5;break;
            case 6: resId = R.drawable.time_6;break;
            case 7: resId = R.drawable.time_7;break;
            case 8: resId = R.drawable.time_8;break;
            case 9: resId = R.drawable.time_9;break;
            case 0: resId = R.drawable.time_0;break;
            default: resId = R.drawable.time_b;break;
        }

        return resId;
    }

}