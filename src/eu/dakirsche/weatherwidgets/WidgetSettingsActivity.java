package eu.dakirsche.weatherwidgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Diese Activity stellt alle derzeit in einem Widget verankerten CityCodes dar und die liefert Informationen zur
 * Widgetart, Stadtname, PLZ, Land und oeffnet durch Interaktion mit der Listview die Activity WidgetSettingsActivity
 */
public class WidgetSettingsActivity extends Activity {
    private static final String TAG = "WidgetDetailActivity";
    protected WidgetCityAdapter mAdapter = null;

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
		
		//@Stefan
		//Max: 	Hier sollte wdoh.removeOldWidgets(widgetManager benoetigt) aufgerufen werden,
		//		damit bereits entfernte Widgets nicht gelistet werden!
		
		this.datasets = wdoh.getWidgetPlacedCityInformations();
		if (this.datasets == null || this.datasets.getSize() == 0){
			//Keine CityInformationen verfuegbar
			CustomImageToast.makeImageToast(WidgetSettingsActivity.this, R.drawable.icon_failure, R.string.error_no_widgets_avaiable, Toast.LENGTH_LONG).show();
			finish();
		}
        else {
            //Es wurden Datensätze gefunden
            if (FunctionCollection.s_getDebugState()) {
                Log.d(TAG, "Datensätze zu Widgets: " + this.datasets.getSize());
                for (int i = 0; i < this.datasets.getSize(); i++)
                    Log.d(TAG, "Datensatz " + (i+1) + ": "+ this.datasets.getItem(i).toString());
            }


            /*Die ListView Komponente vorbereiten*/
            ListView listView = (ListView) findViewById(R.id.listView_ws_listbox);
            this.mAdapter = new WidgetCityAdapter();

            listView.setAdapter(this.mAdapter);
            listView.setOnItemClickListener(this.mAdapter);

        }
	}
    @Override
    protected void onResume(){
        super.onResume();
        if (this.mAdapter != null){
            /*Aktualisierte Daten aus DB laden*/
            WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
            this.datasets = wdoh.getWidgetPlacedCityInformations();
            wdoh.close();
            /*Den ListViewAdapter informieren, dass es neu gezeichnet werden muss*/
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * INNER CLASS WidgetCityAdapter
     * Klassenkontrukt als Adapter fuer die ListView Komponente
     */
    class WidgetCityAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private final LayoutInflater mInflater;
        private int selectedItem = 0;

        public CityInformation getItem(int position){
            return datasets.getItem(position);
        }
        public int getCount(){
            return datasets.getSize();
        }
        public WidgetCityAdapter() {
            mInflater = (LayoutInflater) WidgetSettingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public long getItemId(int position) {
            return (long) datasets.getItem(position).getWidgetId();
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.widget_city_select_item, parent, false);
            bindView(itemView, position);
            return itemView;
        }
        private void bindView(LinearLayout view, int position) {
            CityInformation datensatz = getItem(position);
            view.setId((int) getItemId(position));
            TextView headline = (TextView) view.findViewById(R.id.wcsi_row_title);


            String shortenText = datensatz.toString();

			/*Bei zu langen Texten ab dem letzten Wort abschneiden*/
            int maxCharacters = 100;

            if (shortenText.length() > maxCharacters) {
                int i = maxCharacters;
                while (shortenText.charAt(i) != ' '){
                    i--;
                }

                shortenText = shortenText.substring(0, i) + " [...] ";
            }

            //WidgetId zur Beschriftung hinzufügen im DebugModus
            if (FunctionCollection.s_getDebugState())
                shortenText = shortenText + " (#" + datensatz.getWidgetId() + ")";

            headline.setText(shortenText);
        }

        /**
         * Klickeventhandler für die einzelnen Elemente der ListView
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
             /*Für selektiertes Widget die WidgetSettingsDetailActivity starten*/
            CityInformation selectedDataset = datasets.getItem(position);
            if (selectedDataset.getWidgetId() == (int)id){
              Intent intent = new Intent(WidgetSettingsActivity.this, WidgetSettingsDetailActivity.class);
              intent.putExtra("selectedWidgetId", selectedDataset.getWidgetId());
              if (FunctionCollection.s_getDebugState())
                  Log.d(TAG, "Starte WidgetDetails für Widget #"+id);
              startActivity(intent);
            }
            else {
               // WidgetId passt nicht zur WidgetId, die vom EventListener übergeben wurde
               CustomImageToast.makeImageToast(WidgetSettingsActivity.this, R.drawable.icon_warning, R.string.error_not_matching_widgetids, Toast.LENGTH_LONG).show();
            }
        }
    }

}
