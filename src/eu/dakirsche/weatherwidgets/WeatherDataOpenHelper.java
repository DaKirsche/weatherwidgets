package eu.dakirsche.weatherwidgets;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 
 * Datenbankhandler für die Wetterdaten. Arbeitet mit Objekten vom Typ DataBase, Cursor und WeatherData
 *
 */
public class WeatherDataOpenHelper extends SQLiteOpenHelper implements WeatherDataOpenHelperInterface {
	
	/*Klassenvariablen*/
	
	/*Klassenkonstanten*/
	private static final String TAG = "DB-Interface";
	public static final String DATABASE_NAME = "WeatherWidgets";
	public static final int DATABASE_VERSION = 1;
	
	/*Tabellenbezeichner*/
	public static final String TABLE_CITIES = "cities";
	
	/*Tabellenfelder*/
	public static final String CITIES_ID = "_ID";
	public static final String CITIES_CODE = "CityCode";
	public static final String CITIES_NAME = "CityName";
	public static final String CITIES_ZIP = "CitiesZip";
	public static final String CITIES_LAND_SHORT = "LandShort";
	public static final String CITIES_LAND_LONG = "LandLong";
	
	/*Tabellen Cretae Methoden*/
	private static final String TABLE_CITIES_CREATE = "CREATE TABLE " + TABLE_CITIES + " (" + CITIES_ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
			+ CITIES_CODE + " VARCHAR(10), " + CITIES_NAME + " VARCHAR(80), " + CITIES_ZIP + " VARCHAR(10), " + CITIES_LAND_SHORT + " VARCHAR(5),"  + CITIES_LAND_LONG + " VARCHAR(80))";

	/*Konstruktoren*/
	public WeatherDataOpenHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/*Override Methoden*/
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Hier müssen die Datenbanktabellen angelegt werden
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabellenstruktur wird angelegt");
		db.execSQL(TABLE_CITIES_CREATE);
		
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabelle " + TABLE_CITIES + " angelegt");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		// Datenbankinhalte müssen ausgelesen werden, in den Speicher geschrieben werden.
		// Anschließend Tabellen Droppen, Tabellen neu anlegen und dann Datensätze zurückschreiben
		
	}
	
	/*Public Deklarationen*/
	public WeatherData getWeatherData(String cityCode){
		//Ruft getWeatherData(String cityCode, int time, int date) auf mit aktuellem Datum und aktueller Zeit
		return new WeatherData();
	}
	public WeatherData getWeatherData(String cityCode, int time){
		//Ruft getWeatherData(String cityCode, int time, int date) auf mit aktuellem Datum
		return new WeatherData();
	}
	public WeatherData getWeatherData(String cityCode, int time, int date){
		//Gibt den Datensatz zurï¿½ck, der date und time am nï¿½chsten liegt und cityCode zugeordnet ist
		return new WeatherData();
	}
	   
	public WeatherDataCollection getWeatherSequence(int startDay, int endDay){
		//Gibt getWeatherSequence(int startDay, int endDay, int startTime, int endTime) mit minimaler Zeit fï¿½r Start und maximaler Zeit fï¿½r End wieder
		return new WeatherDataCollection();
	}
	public WeatherDataCollection getWeatherSequence(int startDay, int endDay, int startTime, int endTime){
		return new WeatherDataCollection();
	}
	public CityInformation loadCityInformation(String cityCode){
		//lade Informationen zum CityCode aus DB und fülle CityInformation-Objekt
		return new CityInformation();
	}
	public boolean saveWeatherData(WeatherData importableWeatherData){
		/*Speichert die WeatherData in der Datenbank. Gibt false zurück, wenn die Daten nicht gespeichert wurden, sonst true*/
		return true;
	}
	public void setOptionKey(String keyname, String kevalue){
		
	}
	public String getOptionKey(String keyname){
		return "";
	}
	public CityInformationCollection getAvaiableCityInformations(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			Füge alle CityCodes zur CityInformationCollection hinzu, zu denen Wetterstaen vorhanden sind und demzufolge für einen
			Graphen zur Verfügung stehen
		*/
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;

		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES, new String[] {});
		CityInformation city = null;
		
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
			while (!cursor.isLast()){
				city = new CityInformation();
				city.setCityCode(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
				city.setCityName(cursor.getString(cursor.getColumnIndex(CITIES_NAME)));
				city.setZipCode(cursor.getString(cursor.getColumnIndex(CITIES_ZIP)));
				city.setLand(cursor.getString(cursor.getColumnIndex(CITIES_LAND_SHORT)), cursor.getString(cursor.getColumnIndex(CITIES_LAND_LONG)));
				collection.addItem(city);
				cursor.moveToNext();
			}
			cursor.close();
		}
		else {
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "FOUND ROWS: 0");
		}
		
		return collection;
	}
	public CityInformationCollection getWidgetPlacedCityInformations(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			Füge alle CityCodes zur CityInformationCollection hinzu, die aktuell in einem Widget platziert sind
			Zusätzlich werden die CityInformation Objekte mit Informationen zu dem verwendeten Widget und der WidgetId versehen
			
			Die WidgetArten sind verfügbar via statischem Aufruf von
			CustomWidgetProvider.WIDGET_TYPE_SMALL
			CustomWidgetProvider.WIDGET_TYPE_LARGE
			CustomWidgetProvider.WIDGET_TYPE_FORECAST
		*/
		return collection;
	}
	public CityInformationCollection getActiveCityCodesForSync(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			Füge alle CityCodes zur CityInformationCollection hinzu, die in einem Widget verlinkt sind
			oder ggf auf einer WatchList stehen, falls dieses Feature implementiert wird
		*/
		return collection;
	}
	/*Private Deklarationen*/
	
	/*Protected Deklarationen*/

}
