package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * Diese Activity erm�glicht es dem Nutzer f�r einzelne Widgets spezielle Einstellungen zu treffen (z.B. verwendeter CityCode)
 * */
public class WidgetSettingsDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings_detail);

        final FunctionCollection fn = new FunctionCollection(getApplicationContext());

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
					
					String searchUri = fn.getApiCompatibleSearchUri(searchInput.getText().toString());
					String searchResultXml = fn.fetchDataFromApi(searchUri);

                    /*Nur für Debugzwecke, solange der LogCat verbuggt ist*/
                    ((TextView) findViewById(R.id.textView_output_console)).setText(searchResultXml);
				}
			}
		});      //Button.setOnClickListener

        /*Link zu wetter.com auf das Logo setzen*/
        ((ImageView) findViewById(R.id.imageView_wsd_powered_by)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                //Öffnen der Webseite wetter.com via Browser
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wetter.com"));
                startActivity(browser);
            }
        }) ; //Image.setOnClickListener
	}
	


}
