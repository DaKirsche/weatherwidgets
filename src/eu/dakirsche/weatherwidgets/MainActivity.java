package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * Einstiegs-Activity der Basis-App
 * Hier wird eine Menüstruktur dargestellt, dúrch die auf die Activities "StatisticActivity", "WidgetSettingsActivity" und "InfoActivity"
 * zugegriffen werden kann
 * */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Den Buttons einen OnClickListener zuweisen, in dem die neue Activity gestartet wird
		((Button) findViewById(R.id.button_main_about)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Öffnet die Activity InfoActivity
				Intent intent = new Intent(MainActivity.this, InfoActivity.class);
				startActivity(intent);
			}
		});
		((Button) findViewById(R.id.button_main_statistics)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Öffnet die Activity StatisticActivity
				Intent intent = new Intent(MainActivity.this, StatisticActivity.class);
				startActivity(intent);
			}
		});
		((Button) findViewById(R.id.button_main_widgets)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Öffnet die Activity WidgetSettingsActivity
				Intent intent = new Intent(MainActivity.this, WidgetSettingsActivity.class);
				startActivity(intent);
			}
		});
		
	}
}
