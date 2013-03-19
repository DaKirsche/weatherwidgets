package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Diese Activity stellt alle registrierten CityCodes in einer Liste dar. 
 * Die Liste besteht aus einem ListView Element, in dem die einzelnen CityCodes mit Stadtnamen, PLZ 
 * und Land geladen werden und durch Auswahl eines Eintrages in die StatistikDetailActivity wechselt, in der
 * der Graph für den CityCode erzeugt wird 
 * 
 * */
public class StatisticActivity extends Activity {
	private CityInformationCollection datasets;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		
		/*Laden der Datensätze in in CityInformationCollection datasets*/
		WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(getApplicationContext());
		this.datasets = wdoh.getActiveCityCodesForSync(); //Liefert die aktuell vorhandenen CityInformations zurück
		
		if (this.datasets == null || this.datasets.getSize() == 0){
			//Keine Daten vorhanden -> Activity beenden, Meldung ausgeben
			CustomImageToast.makeImageToast(StatisticActivity.this, R.drawable.icon_failure, R.string.error_no_cities_avaiable, Toast.LENGTH_LONG).show();
			finish();
		}
		wdoh.close();
	}
	
	//Subclass für die ListView
	class cityListViewAdapter extends BaseAdapter implements OnItemClickListener, OnItemLongClickListener {
		private int selectedItem = 0;
		private final LayoutInflater mInflater;
		
		public cityListViewAdapter() {
			mInflater = (LayoutInflater) StatisticActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return datasets.getSize();
		}
		@Override
		public CityInformation getItem(int position) {
			return datasets.getItem(position);
		}
		@Override
		public long getItemId(int position) {
			//wird bisher nur implementiert, wenn die ID der CityInformation von der Pos abweicht
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.statistic_list_view_layout, parent, false);
			bindView(itemView, position);
			return itemView;
		}
		private void bindView(LinearLayout view, int position) {
			CityInformation datensatz = getItem(position);
			view.setId((int) getItemId(position));
			TextView titleView = (TextView) view.findViewById(R.id.statistic_listview_item_headline);
			TextView tView = (TextView) view.findViewById(R.id.statistic_listview_item_subline);
			
			
			String shortenText = datensatz.getZipCode() + ", " + datensatz.getLandCode();
			
			titleView.setText(datensatz.getCityName());
			
			/*Icon in das TextView einfügen*/
			/* Es wird nur ein Icon verwendet, dass bereits über das LAyout eingebettet ist
			Drawable img = getResources().getDrawable(datensatz.getIconResId());
			img.setBounds( 0, 0, 30, 30 );
			titleView.setCompoundDrawables( img, null, null, null );
			*/
			tView.setText(shortenText);
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			// Meldung ausgeben oder Intent bauen und Activity starten
			CityInformation gewaehlterDatensatz = datasets.getItem(position);
			selectedItem = position;
			if (gewaehlterDatensatz.hasCityCode()){
				//Wir benötigen einen CityCode, um die Statistiken auszuwerten
				Intent newWnd = new Intent (StatisticActivity.this, StatistikDetailActivity.class);
				newWnd.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
				startActivity(newWnd);
			}
			
			//CustomImageToast.makeImageToast(ViewActivity.this,gewaehlterDatensatz.getIconResId(), "Datensatz: " + gewaehlterDatensatz.getId() + " - " + gewaehlterDatensatz.getNoteTitle(), Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			//Bei OnLongClick wird ein Auswahlmenü angezeigt, über den man Interaktionen ausführen kann
			final CityInformation gewaehlterDatensatz = datasets.getItem(position);
			DialogInterface.OnClickListener listener;
			CharSequence[] items;
			selectedItem = position;
			/*Popup Menü initialisieren*/
			 items = new CharSequence[]{
						getString(R.string.popup_btn_statistics),
						getString(R.string.popup_btn_delete)
				};
				 

			 listener = new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
							case 0: {
								/*Öffnet den Statistik-Graphen*/
								Intent newWnd = new Intent (StatisticActivity.this, StatistikDetailActivity.class);
								newWnd.putExtra("selectedCityCode", gewaehlterDatensatz.getCityCode());
								startActivity(newWnd);
							}
							break;
							case 1: {
								/*Hier soll die Möglichkeit geschaffen werden ein ausgewählten Datensatz zu löschen (History)*/
							}
							break;
						}
					}
				 };
			CustomPopupMenu popup = new CustomPopupMenu(items, listener, gewaehlterDatensatz);
			popup.show(getFragmentManager(), "PopupDialog");
			
			return true;
		}
	}
}
