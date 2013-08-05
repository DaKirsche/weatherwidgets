package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
        //Erzwinge Portrait Modus für diese Activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            listView.setOnItemLongClickListener(this.mAdapter);

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
    class StatisticCityAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, OnItemLongClickListener {
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
            LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.statistic_list_view_layout, parent, false);
            bindView(itemView, position);
            return itemView;
        }
        private void bindView(LinearLayout view, int position) {
            CityInformation datensatz = getItem(position);
            view.setId((int) getItemId(position));
            TextView headline = (TextView) view.findViewById(R.id.statistic_listview_item_headline);
            TextView textline = (TextView) view.findViewById(R.id.statistic_listview_item_subline);
            ImageView icon = (ImageView) view.findViewById(R.id.statistic_listview_item_icon);


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
        /**
         * LongClickEventHandler für die einzelnen Elemente der ListView
         */
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            CityInformation gewaehlterDatensatz = datasets.getItem(position);
            DialogInterface.OnClickListener listener;
            CharSequence[] items;
            selectedItem = position;
                items = new CharSequence[]{
                        getString(R.string.viewdepth_name_oneday),
                        getString(R.string.viewdepth_name_threeday),
                        getString(R.string.viewdepth_name_week),
                        getString(R.string.viewdepth_name_month)
                };


                listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: {
                                CityInformation gewaehlterDatensatz = datasets.getItem(selectedItem);

                                Intent intent = new Intent(StatisticActivity.this, StatistikDetailActivity.class);
                                intent.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
                                intent.putExtra("selectedViewDepth", StatistikDetailActivity.VIEW_DEPH_ONEDAY);

                                startActivity(intent);
                            }
                            break;
                            case 1: {
                                CityInformation gewaehlterDatensatz = datasets.getItem(selectedItem);

                                Intent intent = new Intent(StatisticActivity.this, StatistikDetailActivity.class);
                                intent.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
                                intent.putExtra("selectedViewDepth", StatistikDetailActivity.VIEW_DEPTH_THREEDAYS);

                                startActivity(intent);
                            }
                            break;
                            case 2: {
                                CityInformation gewaehlterDatensatz = datasets.getItem(selectedItem);

                                Intent intent = new Intent(StatisticActivity.this, StatistikDetailActivity.class);
                                intent.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
                                intent.putExtra("selectedViewDepth", StatistikDetailActivity.VIEW_DEPH_ONEWEEK);

                                startActivity(intent);
                            }
                            break;
                            case 3: {
                                CityInformation gewaehlterDatensatz = datasets.getItem(selectedItem);

                                Intent intent = new Intent(StatisticActivity.this, StatistikDetailActivity.class);
                                intent.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
                                intent.putExtra("selectedViewDepth", StatistikDetailActivity.VIEW_DEPTH_MONTH);

                                startActivity(intent);
                            }
                            break;
                        }
                    }
                };
            StatisticListItemPopupMenu popup = new StatisticListItemPopupMenu(items, listener, gewaehlterDatensatz.getCityId());
            popup.show(getFragmentManager(), "PopupDialog");

            return true;
        }
    }
}
