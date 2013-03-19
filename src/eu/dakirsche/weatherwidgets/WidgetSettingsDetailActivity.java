package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Diese Activity ermöglicht es dem Nutzer für einzelne Widgets spezielle Einstellungen zu treffen (z.B. verwendeter CityCode)
 * */
public class WidgetSettingsDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings_detail);
		
		/*Bei Klick auf den Suche Starten Button soll nach der Eingabe gesucht werden*/
		((Button) findViewById(R.id.wsd_start_search_button)) . setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText searchInput = ((EditText) findViewById(R.id.wsd_search_input));
				String input = searchInput.getText().toString();
				if (input == null || input.equals("")){
					//Keine Sucheingabe
					CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_warning, R.string.error_no_search_input, Toast.LENGTH_LONG).show();
					searchInput.requestFocus();
				}
				else {
					//Suchparameter eingegeben, Suche kann gestartet werden
					FunctionCollection fn = new FunctionCollection(getApplicationContext());
					
					String searchUri = fn.getApiCompatibleSearchUri(searchInput.getText().toString());
					fn.fetchDataFromApi(searchUri);
				}
			}
		});
	}
	


}
