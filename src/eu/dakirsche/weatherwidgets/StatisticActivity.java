package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Diese Activity stellt alle registrierten CityCodes in einer Liste dar. 
 * Die Liste besteht aus einem ListView Element, in dem die einzelnen CityCodes mit Stadtnamen, PLZ 
 * und Land geladen werden und durch Auswahl eines Eintrages in die StatistikDetailActivity wechselt, in der
 * der Graph für den CityCode erzeugt wird
 * 
 * */
public class StatisticActivity extends Activity {
    private static final String TAG = "StatisticActivity";
    protected StatisticCityAdapter mAdapter = null;

    protected CityInformationCollection datasets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());

        //@Stefan
        //Max: 	Hier sollte wdoh.removeOldWidgets(widgetManager benoetigt) aufgerufen werden,
        //		damit bereits entfernte Widgets nicht gelistet werden!

        this.datasets = wdoh.getActiveCityCodesForSync();
        if (this.datasets == null || this.datasets.getSize() == 0){
            //Keine CityInformationen verfuegbar
            CustomImageToast.makeImageToast(StatisticActivity.this, R.drawable.icon_failure, R.string.error_no_city_found, Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            //Es wurden Datensätze gefunden
            if (FunctionCollection.s_getDebugState()) {
                Log.d(TAG, "Datensätze zu Cities: " + this.datasets.getSize());
                //for (int i = 0; i < this.datasets.getSize(); i++)
                //    Log.d(TAG, "Datensatz " + (i+1) + ": "+ this.datasets.getItem(i).toString());
            }


            /*Die ListView Komponente vorbereiten*/
            ListView listView = (ListView) findViewById(R.id.listView_statistic_registeredCities);
            this.mAdapter = new StatisticCityAdapter();

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
            this.datasets = wdoh.getActiveCityCodesForSync();
            wdoh.close();
            /*Den ListViewAdapter informieren, dass es neu gezeichnet werden muss*/
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * INNER CLASS WidgetCityAdapter
     * Klassenkontrukt als Adapter fuer die ListView Komponente
     */
    class StatisticCityAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private final LayoutInflater mInflater;
        private int selectedItem = 0;

        public CityInformation getItem(int position){
            return datasets.getItem(position);
        }
        public int getCount(){
            return datasets.getSize();
        }
        public StatisticCityAdapter() {
            mInflater = (LayoutInflater) StatisticActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public long getItemId(int position) {
            return (long) datasets.getItem(position).getCityId();
        }
        public String getCityCode(int position) {
            return  datasets.getItem(position).getCityCode();
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
            TextView textline = (TextView) view.findViewById(R.id.wcsi_row_text);
            ImageView icon = (ImageView) view.findViewById(R.id.wcsi_row_icon);


            String shortenText = datensatz.getAdditionalLandInformationsByRemovingZip();
            String cityName = datensatz.getZipCode() + " " + datensatz.getCityName();

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
                shortenText = shortenText + " (#" + datensatz.getCityId() + ")";



            headline.setText(cityName);
            textline.setText(shortenText);

            icon.setImageResource(R.drawable.icon_home);

        }

        /**
         * Klickeventhandler für die einzelnen Elemente der ListView
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
             /*Für selektiertes Widget die WidgetSettingsDetailActivity starten*/
            CityInformation selectedDataset = datasets.getItem(position);
            if (selectedDataset.hasCityCode()){
                Intent intent = new Intent(StatisticActivity.this, StatistikDetailActivity.class);
                intent.putExtra("selectedCityCode", selectedDataset.getCityCode());
                intent.putExtra("selectedViewDepth", StatistikDetailActivity.VIEW_DEPTH_THREEDAYS);
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Starte StatistikDetails für City: "+selectedDataset.toString());
                startActivity(intent);
            }
            else {
                // WidgetId passt nicht zur WidgetId, die vom EventListener übergeben wurde
                CustomImageToast.makeImageToast(StatisticActivity.this, R.drawable.icon_warning, R.string.error_city_unknown, Toast.LENGTH_LONG).show();
            }
        }
    }
}
