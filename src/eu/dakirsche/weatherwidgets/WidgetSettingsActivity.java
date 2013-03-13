package eu.dakirsche.weatherwidgets;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;
/**
 * Diese Activity stellt alle derzeit in einem Widget verankerten CityCodes dar und die liefert Informationen zur
 * Widgetart, Stadtname, PLZ, Land und öffnet durch Interaktion mit der Listview die Activity WidgetSettingsActivity
 */
public class WidgetSettingsActivity extends Activity {
	protected CityInformationCollection datasets;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		
		WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
		
		this.datasets = wdoh.getWidgetPlacedCityInformations();
		if (this.datasets == null || this.datasets.getSize() == 0){
			//Keine CityInformationen verfügbar
			CustomImageToast.makeImageToast(WidgetSettingsActivity.this, R.drawable.icon_failure, R.string.error_no_widgets_avaiable, Toast.LENGTH_LONG).show();
			finish();
		}
		
		wdoh.close();
	}



}
