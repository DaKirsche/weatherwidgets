package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;


/**
 * Diese Activity ermoeglicht es dem Nutzer fuer einzelne Widgets spezielle Einstellungen zu treffen (z.B. verwendeter CityCode)
 * */
public class WidgetSettingsDetailActivity extends Activity {
    private int mAppWidgetId = 0;
    private static final int INVALID_WIDGET_TYPE = 999;
    private CityInformation currentSelectedCity = null;
    private CityInformationCollection currentDatasets = null;

    private static final String TAG = "WidgetSettingDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings_detail);
        //Erzwinge Portrait Modus für diese Activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
       // ((TextView) findViewById(R.id.textView_output_console)).setText(mAppWidgetId + "");
        final FunctionCollection fn = new FunctionCollection(getApplicationContext());

		/*Bei Klick auf den Suche Starten Button soll nach der Eingabe gesucht werden*/
		((Button) findViewById(R.id.wsd_start_search_button)) . setOnClickListener(new View.OnClickListener() {
			
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
					if (fn.isInternetAvaiable()){
                        String searchUri = fn.getApiCompatibleSearchUri(searchInput.getText().toString());
                        String searchResultXml = fn.fetchDataFromApi(searchUri);

                        /*XML String an den PArser übergeben und die CityCollection auswerten*/
                        handleXmlResult(searchResultXml);
                    }
                    else {
                        CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_warning, R.string.error_please_connect_to_internet, Toast.LENGTH_LONG);
                    }

                    /*Nur für Debugzwecke, solange der LogCat verbuggt ist*/
                //    ((TextView) findViewById(R.id.textView_output_console)).setText(searchResultXml);


				}
			}
		});      //Button.setOnClickListener

        //Test button ... TO REMOVE LATER
        ((Button) findViewById(R.id.button_test_save)) . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  save();
            }
        });
        /*REMOVE TILL HERE*/

        /*Link zu wetter.com auf das Logo setzen*/
        ((ImageView) findViewById(R.id.imageView_wsd_powered_by)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Öffnen der Webseite wetter.com via Browser
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wetter.com"));
                startActivity(browser);
            }
        }) ; //Image.setOnClickListener

         /*Vorhandene Widgetdaten laden*/
        this.loadCurrentConfig();
	}

    /**
     * Speichert die aktuelle Zusammenstellung zum Widget und erzeugt ggf. ggf einen CityInformation Datensatz
     */
    private void save(){
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Daten werden gespeichert...");
        /*Nur speichern, wenn auch eine City ausgewählt wurde*/
        if (this.currentSelectedCity != null && this.currentSelectedCity.hasCityCode() && this.mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
            //Datensatz CityInformation speichern, falls nciht vorhanden
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Speichere CityInformation: " + this.currentSelectedCity.toString());
            wdoh.saveCityInformation(this.currentSelectedCity);
            //Widget mit City verknüpfen
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Speichere Widget: " + this.mAppWidgetId + " - " + this.currentSelectedCity.getCityCode());
            int widgetType = this.getWidgetType();
            if (widgetType == INVALID_WIDGET_TYPE)
                widgetType = this.currentSelectedCity.getWidgetType();
            EditText name_input = (EditText) findViewById(R.id.wsd_optionalname_input);
            String widgetName = name_input.getText().toString();
            if (widgetName.trim().equals(""))
                widgetName = this.getWidgetName();
            wdoh.saveWidget(this.mAppWidgetId, widgetType, this.currentSelectedCity.getCityCode(), widgetName);
            wdoh.close();


        /*Alle vorhandenen Widgets aktualisieren*/
            Intent intent = new Intent(this, SmallWidgetProvider.class);
            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            int s_ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SmallWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,s_ids);
            sendBroadcast(intent);

            intent = new Intent(this, LargeWidgetProvider.class);
            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            int l_ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LargeWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,l_ids);
            sendBroadcast(intent);

            intent = new Intent(this, ForecastWidgetProvider.class);
            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            int f_ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ForecastWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,f_ids);
            sendBroadcast(intent);

        /*Antwort für das Widget*/
            Intent resultValue = new Intent();
            CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_success, R.string.success_widget_saved, Toast.LENGTH_SHORT);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,this.mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
        else CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_failure, R.string.error_city_unknown, Toast.LENGTH_LONG).show();
    }
    private void loadCurrentConfig(){
        String cityName = "- Keine Stadt zugeordnet -";
        String cityCode = "";
        String cityZip = "";
        String cityLand = "";
        String cityLandCode = "";

        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Konfiguration wird geladen für WidgetId #" + this.mAppWidgetId);
        /*Nur Laden, wenn eine WidgetId vorhanden ist*/
        if (this.mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
            CityInformation city = wdoh.getWidgetCityInformation(this.mAppWidgetId);
            /*Nur laden, wenn Daten empfangen*/
            if (city != null){
                this.loadTemporaryConfig(city);
                this.currentSelectedCity = city;
            }
            else if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Keine Einstellungen zum Widget gefunden!");

            wdoh.close();
        }
    }


    private void loadTemporaryConfig(CityInformation city){
        String cityName = "";
        String cityCode = "";
        String cityZip = "";
        String cityLand = "";
        String cityLandCode = "";
        String widgetName = "";

        /*Nur laden, wenn Daten empfangen*/
        if (city != null){
            cityName = city.getCityName();
            cityCode = city.getCityCode();
            cityZip = city.getZipCode();
            cityLand = city.getAdditionalLandInformations();
            cityLandCode = city.getLandCode();
            widgetName = city.getWidgetName();
        }

        /*Ausgabe im Template*/
        ((TextView) findViewById(R.id.textView_wsd_city_currentconfig)).setText(cityName + "(" + cityCode + ")");
        ((TextView) findViewById(R.id.textView_wsd_plz_currentconfig)).setText(cityZip);
        ((TextView) findViewById(R.id.textView_wsd_land_currentconfig)).setText(cityLand);
        ((TextView) findViewById(R.id.textView_wsd_landcode_currentconfig)).setText(cityLandCode);
        ((EditText) findViewById(R.id.wsd_optionalname_input)).setText(widgetName);
    }
    /**
     * Methode zum Auswerten der CityCollection und Generierung des PopupSelektors
     * @param xmlResult String - XML-Struktur von der Wetter-API
     */
    private void handleXmlResult(String xmlResult){
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Starte XML Handler");
        XmlParser xmlParser = new XmlParser();

        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Übergebe DatenString");
        CityInformationCollection cICollection = xmlParser.getCities(xmlResult);

        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Ausgewertetes Objekt: " + cICollection.getSize());

        if (cICollection == null || cICollection.getSize() <= 0){
            //Keine Cities in der XML vorhanden
            this.currentDatasets = null;
            CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_failure, R.string.error_no_city_found, Toast.LENGTH_LONG).show();
            ((EditText) findViewById(R.id.wsd_search_input)).requestFocus();
        }
        else {
            this.currentDatasets = cICollection;

            //Wenn nur eine CityInformation in der Collection vorhanden ist brauchen wir kein Popup
            if (this.currentDatasets.getSize() == 1){
                //Item Nr. 1 mit ID 0 automatisch wählen
                selectItemFromPopup(0);
            }
            else {
            /*Popupmenü erzeugen zur Auswahl der gewünschten City*/
                DialogInterface.OnClickListener listener;
                CharSequence[] items;
                CityInformation city;
                String cityCaption;

                items = new String[cICollection.getSize()];
                for (int i = 0; i < cICollection.getSize(); i++) {
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, "Setze Werte für: " + i);
                    city = cICollection.getItem(i);
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, "Aktueller Datensatz: " + i);
                    cityCaption = city.toString();
                    if (FunctionCollection.s_getDebugState())
                        Log.d(TAG, "Aktueller Datensatz enthält: " + cityCaption);
                    items[i] = cityCaption;
                }
                listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          selectItemFromPopup(which);
                    }
                };
                CitySelectPopupMenu popup = new CitySelectPopupMenu(items, listener);
                popup.show(getFragmentManager(), "PopupDialog");
            }
        }
    }

    /**
     * Setzt in die Klassenvariable currentSelectedCity die aktuell ausgewählte CityInformation
     * @param itemId int gewählter Datensatz im Popupmenü
     */
    private void selectItemFromPopup(int itemId){
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Ausgewählte CityInformation hat ID #" + itemId);
        if (this.currentDatasets != null && itemId >= 0 && itemId < this.currentDatasets.getSize()) {
            /* WENN DIE DERZEITIGE CITYINFORMATION WIDGETINFORMATIONEN ENTHÄLT DIESE BEIBEHALTEN */
            int tmpWidgetType = 0;
            String tmpWidgetName = "";
            if (this.currentSelectedCity != null && this.currentSelectedCity.getWidgetType() > 0 && this.currentSelectedCity.getWidgetType() < 999){
                tmpWidgetType = this.currentSelectedCity.getWidgetType();
                tmpWidgetName = this.currentSelectedCity.getWidgetName();
            }

            this.currentSelectedCity = this.currentDatasets.getItem(itemId);
            /* Daten zum neuen Ojekt übertragen */
            if (tmpWidgetType != 0)
                this.currentSelectedCity.setWidget(tmpWidgetType, this.mAppWidgetId, tmpWidgetName);

            this.loadTemporaryConfig(this.currentSelectedCity);
        }

        //Toast ausgeben
        CustomImageToast.makeImageToast(WidgetSettingsDetailActivity.this, R.drawable.icon_success, R.string.success_city_selected, Toast.LENGTH_SHORT);

        /*Widgetname Input Focus setzen*/
        ((EditText) findViewById(R.id.wsd_optionalname_input)).requestFocus();
    }
    /**
     * Dummymethode, die von den Widgetspezifischen, abgeleiteten Klassen sinnvoll gefüllt werden.
     */
    protected int getWidgetType(){
        /* Wenn das aktuell geladene Widget einen Typen hat returne diesen */
        if (this.currentSelectedCity != null && this.currentSelectedCity.getWidgetType() != INVALID_WIDGET_TYPE)
            return this.currentSelectedCity.getWidgetType();

        return INVALID_WIDGET_TYPE;
    }
    /**
     * Dummymethode, die von den Widgetspezifischen, abgeleiteten Klassen sinnvoll gefüllt werden.
     */
    protected String getWidgetName(){
        return getString(R.string.typename_unknown);
    }

}
