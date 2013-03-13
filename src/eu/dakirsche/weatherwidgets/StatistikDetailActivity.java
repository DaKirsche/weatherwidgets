package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;
/**
 * Diese Activity stellt ein Object vom Typ GraphView dar, welches �ber die DB Wetterdaten f�r einen Zeitraum erh�lt.
 * Diese stellt es dann in einem Graphen dar
 */
public class StatistikDetailActivity extends Activity {
	private String selectedCityCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistik_detail);
		
		//Erzwinge Landscape Modus f�r diese Activity
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		/*�bergebene Werte einlesen*/
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			//Es wurden Parameter �bergeben
			this.selectedCityCode = extras.getString("selectedCityCode");
		}
		else {
			CustomImageToast.makeImageToast(StatistikDetailActivity.this, R.drawable.icon_failure, R.string.error_missing_citycode, Toast.LENGTH_LONG);
			//Activity beenden
			finish();
		}
	}

}
