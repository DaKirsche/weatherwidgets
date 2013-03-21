package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
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
    private int mAppWidgetId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings_detail);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        this.mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        //Widget ID aus den IntentExtras einlesen
        if (extras != null) {

              //WidgetId über Konfigurationsaufruf des Widgets starten
            this.mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
             //Kein Aufruf via Konfiguration, Aufruf via BaseApp?
             if (this.mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
                 this.mAppWidgetId = extras.getInt("selectedWidgetId", AppWidgetManager.INVALID_APPWIDGET_ID);
             }

            //Kein Widget gefunden, Fehler ausgeben und beenden
            if (this.mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
                  CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_failure, R.string.error_no_widget_selected, Toast.LENGTH_LONG);
                  finish();
            }
         }

        /*HOWTO Update des Widgets*/
        /*
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(),
            R.layout.example_appwidget);
            appWidgetManager.updateAppWidget(mAppWidgetId, views);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        */
        ((TextView) findViewById(R.id.textView_output_console)).setText(mAppWidgetId + "");
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

        //Test button ... TO REMOVE LATER
        ((Button) findViewById(R.id.button_test_save)) . setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                  save();
            }
        });
        /*REMOVE TILL HERE*/

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

    private void save(){
        Intent resultValue = new Intent();
        CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_success, "saved", Toast.LENGTH_SHORT);
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,this.mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
	


}
