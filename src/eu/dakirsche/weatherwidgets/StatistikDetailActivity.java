package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Diese Activity stellt ein Object vom Typ GraphView dar, welches �ber die DB Wetterdaten f�r einen Zeitraum erh�lt.
 * Diese stellt es dann in einem Graphen dar
 */
public class StatistikDetailActivity extends Activity {
    public static final int VIEW_DEPH_ONEDAY = 1;
    public static final int VIEW_DEPTH_THREEDAYS = 3;
    public static final int VIEW_DEPH_ONEWEEK = 7;
    public static final int VIEW_DEPTH_MONTH = 31;

    private static final String TAG = "StatisticDetailActivity";

	private String selectedCityCode = "";
    private CityInformation selectedCity = null;
    private int selectedViewDepth = VIEW_DEPTH_THREEDAYS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistik_detail);

        //Erzwinge Landscape Modus f�r diese Activity
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		/*Übergebene Werte einlesen*/
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			//Es wurden Parameter �bergeben
            this.selectedCityCode = extras.getString("selectedCityCode");
            this.selectedViewDepth = extras.getInt("selectedViewDepth", VIEW_DEPTH_THREEDAYS);
		}
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Übergebene Parameter: \nCityCode: " + this.selectedCityCode + "\nViewDepth: " + this.selectedViewDepth);

        if (extras == null || this.selectedCityCode.equals("")) {    //Keine Daten vorhanden
			CustomImageToast.makeImageToast(StatistikDetailActivity.this, R.drawable.icon_failure, R.string.error_missing_citycode, Toast.LENGTH_LONG);
			//Activity beenden
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "CityCode leer!");

            finish();
		}

        /*Viewdepth sind nur Werte der Konstanten erlaubt*/
        if (this.selectedViewDepth != VIEW_DEPH_ONEDAY && this.selectedViewDepth != VIEW_DEPTH_THREEDAYS
                && this.selectedViewDepth != VIEW_DEPH_ONEWEEK && this.selectedViewDepth != VIEW_DEPTH_MONTH )
            this.selectedViewDepth = VIEW_DEPTH_THREEDAYS; //Default 3 Tage

        /*Prüfen, ob der CityCode einer Stadt in der DB zugeordnet werden kann*/
        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
        this.selectedCity = wdoh.getCityInformation(this.selectedCityCode);
        wdoh.close();
        if (this.selectedCity == null){
            CustomImageToast.makeImageToast(this, R.drawable.icon_failure, R.string.error_city_unknown, Toast.LENGTH_LONG);

            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Stadt unbekannt");
            finish();
        }
        this.initializeGraphView();
	}

    /**
     * Methode zum initialisieren der GraphView Komponente
     */
    private void initializeGraphView(){
        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);       //Heute 23:39 Uhr

        //Wenn für Morgen noch alle Daten vorhanden sind wähle diesen Tag als Endzeit
        cal.add(Calendar.DATE, 1); //Morgen 23:59 Uhr
        if (wdoh.getWeatherData(this.selectedCity.getCityCode(), cal.getTime()) == null)
            cal.add(Calendar.DATE, -1); //Heute 23:59 Uhr

        Date endDate = cal.getTime();

        if (this.selectedViewDepth != VIEW_DEPTH_MONTH)
            cal.add(Calendar.DATE, (this.selectedViewDepth * -1));
        else
            cal.add(Calendar.MONTH, (this.selectedViewDepth * -1));

        Date startDate = cal.getTime();
        WeatherDataCollection wCol = wdoh.getWeatherSequence(startDate, endDate);

        if (wCol == null || wCol.getSize() == 0){
            CustomImageToast.makeImageToast(this, R.drawable.icon_failure, R.string.error_no_weatherdata_avaiable, Toast.LENGTH_LONG);

            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Keine Datensätze gefunden für City: " + this.selectedCity.toString());
            finish();
        }
        else {
            GraphView graphView = (GraphView) findViewById(R.id.graphView_statistic_graph);
            if (graphView != null){
                 graphView.useDataCollection(wCol);
            }
            else {
                CustomImageToast.makeImageToast(this, R.drawable.icon_warning, R.string.error_please_restart, Toast.LENGTH_LONG);
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "GraphView nicht gefunden!");
                finish();
            }
        }

    }
}
