package eu.dakirsche.weatherwidgets;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 
 * Datenbankhandler fuer die Wetterdaten. Arbeitet mit Objekten vom Typ DataBase, Cursor und WeatherData
 *
 */
public class WeatherDataOpenHelper extends SQLiteOpenHelper {

    /**
     * TODO
     * Methoden bereistellen fuer:
     * --> CityInformation speichern (als Collection oder Single)
     * --> WeatherData speichern (als Collection oder Single)
     * Widget speichern:
     * --> Params: WidgetId int und String cityCode
     * Datensaetze aelter als 3 Monate loeschen
     * */

	/*Klassenvariablen*/

	
	/*Klassenkonstanten*/
	private static final String TAG = "DB-Interface";
	public static final String DATABASE_NAME = "WeatherWidgets.db";
	public static final int DATABASE_VERSION = 1;
	
	/*Tabellenbezeichner*/
	public static final String TABLE_CITIES = "Cities";
	public static final String TABLE_WEATHER = "Weather";
	public static final String TABLE_WIDGETS = "Widgets";
	
	/*Tabellenfelder*/
	public static final String CITIES_ID = "_ID";
	public static final String CITIES_CODE = "CityCode";
	public static final String CITIES_NAME = "Name";
	public static final String CITIES_ZIP = "Zip";
	public static final String CITIES_LAND_SHORT = "LandShort";
	public static final String CITIES_LAND_LONG = "LandLong";
	
	public static final String WEATHER_ID = "_ID";
	public static final String WEATHER_City_ID = "City_ID";
	public static final String WEATHER_DateTime = "Weather_DateTime";
	public static final String WEATHER_Temp_Min = "Temp_Min";
	public static final String WEATHER_Temp_Max = "Temp_Max";
	public static final String WEATHER_Code = "Code";
	
	public static final String WIDGET_ID = "_ID";
	public static final String WIDGET_IDs = "Widget_ID";
	public static final String WIDGET_City_ID = "City_ID";
	
	
	/*Tabellen Create Methoden*/
//	private static final String TABLE_CITIES_CREATE = "CREATE TABLE " + TABLE_CITIES + " (" + CITIES_ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
//			+ CITIES_CODE + " VARCHAR(10), " + CITIES_NAME + " VARCHAR(80), " + CITIES_ZIP + " VARCHAR(10), " + CITIES_LAND_SHORT + " VARCHAR(5),"  + CITIES_LAND_LONG + " VARCHAR(80))";
	
	private static final String TABLE_CITIES_CREATE = "CREATE TABLE '"+TABLE_CITIES+"' ("+	CITIES_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							CITIES_CODE+" VARCHAR(10),"+ 
																							CITIES_NAME+" VARCHAR(80),"+ 
																							CITIES_ZIP+" VARCHAR(10),"+ 
																							CITIES_LAND_SHORT+" VARCHAR(5),"+ 	// noch benoetigt ??
																							CITIES_LAND_LONG+" VARCHAR(80)	);";// noch benoetigt ??
																							// logDate hinzufuegen fuer die 30 Tage!
	
	private static final String TABLE_WEATHER_CREATE = "CREATE TABLE '"+TABLE_WEATHER+"' ("+WEATHER_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							WEATHER_City_ID+" INTEGER NOT NULL CONSTRAINT "+
																							WEATHER_City_ID+" REFERENCES "+TABLE_CITIES+"("+CITIES_ID+") ON DELETE CASCADE,"+ 
																							WEATHER_DateTime+" DATETIME,"+ 
																							WEATHER_Temp_Min+" DOUBLE,"+ 
																							WEATHER_Temp_Max+" DOUBLE,"+ 
																							WEATHER_Code+" INTEGER	);"; // noch benoetigt ??
	
	private static final String TABLE_WIDGETS_CREATE = "CREATE TABLE '"+TABLE_WIDGETS+"' ("+WIDGET_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							WIDGET_IDs+" INTEGER NOT NULL,"+ 
																							WIDGET_City_ID+" INTEGER NOT NULL CONSTRAINT "+
																							WIDGET_City_ID+" REFERENCES "+TABLE_WEATHER+"("+WIDGET_City_ID+") ON DELETE CASCADE);";
	
	
	/*Konstruktoren*/
	
	public WeatherDataOpenHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/*Override Methoden*/
	
	// Die Datenbanktabellen werden angelegt
	@Override
	public void onCreate(SQLiteDatabase db) {		
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabellenstruktur wird angelegt");		
		db.execSQL(TABLE_CITIES_CREATE);
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabelle " + TABLE_CITIES + " angelegt");
		db.execSQL(TABLE_WEATHER_CREATE);
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabelle " + TABLE_WEATHER + " angelegt");
		db.execSQL(TABLE_WIDGETS_CREATE);		
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "Tabelle " + TABLE_WIDGETS + " angelegt");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		// Wird spaeter implementiert!
		// Datenbankinhalte muessen ausgelesen werden, in den Speicher geschrieben werden.
		// Anschliessend Tabellen Droppen, Tabellen neu anlegen und dann Datensaetze zurueckschreiben		
	}
	
	/*Public Deklarationen*/
	
	/* Methods for Widgets */
	
	public boolean saveWidget(Integer widgetID, String cityCode){
		boolean result;
		ContentValues values = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();	
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT * FROM " + TABLE_CITIES + 
								" WHERE "+CITIES_CODE+"='"+cityCode+"'", new String[] {});
		result = (cursor.getCount() == 1);
		if (result){
			values.put(WIDGET_IDs, widgetID);
			values.put(WIDGET_City_ID, cursor.getString(cursor.getColumnIndex(CITIES_ID)));
			result = (db.insert(TABLE_WIDGETS, null, values) >= 0); 	
			cursor.close();
		}
		if (!result)
			Log.d(TAG, "Widget " + widgetID + " nicht gespeichert!");
		db.close();
		return result;		
	}
	
	
	
	public CityInformation getWidgetCityInformation(Integer widgetID){
		// was ist, wenn neues widget alte (in der db vorhandene) id erhaelt !!??
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Anfrage: getWidgetCityInformation für Widget #" + widgetID);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery(	"SELECT * FROM " +TABLE_WIDGETS+","+TABLE_CITIES+" WHERE "+
								WIDGET_IDs+"="+widgetID+" AND "+TABLE_CITIES+"."+CITIES_ID+"="+WIDGET_City_ID, new String[] {});
		if (cursor.getCount() == 1){
			city = new CityInformation();
			city.setCityCode(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
			city.setCityName(cursor.getString(cursor.getColumnIndex(CITIES_NAME)));
			city.setZipCode(cursor.getString(cursor.getColumnIndex(CITIES_ZIP)));
			// LandCode !?
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "CityInformation gefunden: " + city.toString());
			cursor.close();			
		}else if (cursor.getCount() > 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Widget : "+widgetID+" hat > 1 Cities!");
		}else if (cursor.getCount() == 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Widget: "+widgetID+" nicht gefunden!");
		}		
		db.close();
		return city;		
	}
	
	// Methode: Wenn Widget geschlossen wird, muss es aus der DB geloescht werden!!!
	
	/* Methods for WeahterData */
	
	public WeatherData getWeatherData(String cityCode){
		Date nowDateTime = new Date(System.currentTimeMillis());
		return getWeatherData(cityCode,nowDateTime);
	}
	public WeatherData getWeatherData(String cityCode, long time){
		Date nowDateTime = new Date(System.currentTimeMillis());
		nowDateTime.setTime(time);
		return getWeatherData(cityCode,nowDateTime);
	}
	
	// Gibt den Datensatz zurueck, der dateTimeValue am naechsten liegt und cityCode zugeordnet ist
	public WeatherData getWeatherData(String cityCode, Date dateTimeValue){		
		String queryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateTimeValue);
		SQLiteDatabase db = getReadableDatabase();
		WeatherData weatherData = null;
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT * FROM "+TABLE_WEATHER+","+TABLE_CITIES+" WHERE "+
								CITIES_CODE+"='"+cityCode+"' AND "+WEATHER_City_ID+"="+TABLE_CITIES+"."+CITIES_ID+" AND "+
								"DATE("+WEATHER_DateTime+") = Date('"+queryDate+"') AND "+
								"Time("+WEATHER_DateTime+") >= Time('"+queryDate+"') "+
								"ORDER BY "+WEATHER_DateTime+" ASC" , new String[] {}
							);
		cursor.moveToFirst();
		if (cursor.getCount() > 0){
			weatherData = new WeatherData();
			weatherData.setCityInformation(getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))));
			weatherData.setDateTimeStr(cursor.getString(cursor.getColumnIndex(WEATHER_DateTime)));
			weatherData.setTemperatures(cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Min)), 
										cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Max)));
			cursor.close();
		}
		db.close();
		return weatherData;
	}
	   
	public WeatherDataCollection getWeatherSequence(Date startDate, Date endDate){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		long startTime = 0;	
		long endTime = 0;
	    try {
	    	startTime = sdf.parse("00:00:00").getTime();
	    	endTime = sdf.parse("24:59:59").getTime();
	    } catch(ParseException e){
	    	
	    }
		return getWeatherSequence(startDate,endDate,startTime,endTime);
	}
	public WeatherDataCollection getWeatherSequence(Date startDate, Date endDate, long startTime, long endTime){
		startDate.setTime(startTime);
		endDate.setTime(endTime);
		String strStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(startDate);
		String strEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(endDate);
		WeatherDataCollection collection = new WeatherDataCollection();		
		SQLiteDatabase db = getReadableDatabase();
		WeatherData weatherData = null;
		Cursor cursor;		
		
		cursor = db.rawQuery(	"SELECT * FROM "+TABLE_WEATHER+","+TABLE_CITIES+" WHERE "+
								WEATHER_City_ID+"="+TABLE_CITIES+"."+CITIES_ID+" AND "+
								"DATETIME("+WEATHER_DateTime+") >= DATETIME('"+strStartDate+"') AND "+
								"DATETIME("+WEATHER_DateTime+") <= DATETIME('"+strEndDate+"') AND "+
								"Time("+WEATHER_DateTime+") >= Time('"+strStartDate+"') AND "+
								"Time("+WEATHER_DateTime+") <= Time('"+strEndDate+"') "+
								"ORDER BY "+WEATHER_DateTime+" ASC" , new String[] {}
							);	
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			while (!cursor.isLast()){
				weatherData = new WeatherData();
				weatherData.setCityInformation(getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))));
				weatherData.setDateTimeStr(cursor.getString(cursor.getColumnIndex(WEATHER_DateTime)));
				weatherData.setTemperatures(cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Min)), 
											cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Max)));
				collection.addItem(weatherData);
				cursor.moveToNext();
			}
			cursor.close();
		}
		else {
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Keine Wetterdaten vom "+strStartDate+" bis "+strEndDate+" verf�gbar!");
		}	
		db.close();
		return collection;
	}
	
	public boolean saveWeatherData(WeatherData importableWeatherData){
		boolean result;
		ContentValues values = new ContentValues();
		String weatherDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(importableWeatherData.getDate());
		SQLiteDatabase db = getWritableDatabase();
		CityInformation city = getCityInformation(importableWeatherData.getCityCode());
		result = (city != null);
		if (result){
			values.put(WEATHER_City_ID, city.getCityCode());
			values.put(WEATHER_DateTime,weatherDate);
			values.put(WEATHER_Temp_Min,importableWeatherData.getTemperaturMin().toString() );
			values.put(WEATHER_Temp_Max,importableWeatherData.getTemperatureMax().toString());
//			Was ist mit WEATHER_Code ?? 
			result =  (db.insert(TABLE_WEATHER, null, values) >= 0); 
			if (!result)
				Log.d(TAG, "WeatherData wurde nicht gespeichert!");
		}else
			Log.d(TAG, "City mit Citycode: " + importableWeatherData.getCityCode() + " nicht gefunden!");
		db.close();
		return result;
	}
	
	/* Methods for CityInformation */
	
	public boolean saveCityInformation(CityInformation importableCityInformation) {
		boolean result;
		ContentValues values = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT * FROM " + TABLE_CITIES + 
								" WHERE "+CITIES_CODE+"='"+importableCityInformation.getCityCode()+"'", new String[] {});
		result = (cursor.getCount() > 0); 
		if (!result) {		
			values.put(CITIES_CODE, importableCityInformation.getCityCode());
			values.put(CITIES_NAME, importableCityInformation.getCityName());
			values.put(CITIES_ZIP, importableCityInformation.getZipCode());
			// Was ist mit dem LandShort/ Long ?? Habe nur noch LandCode zur Verfuegung !?	
			result = (db.insert(TABLE_CITIES, null, values) >= 0); 
			cursor.close();
		}		
		db.close();
		if (!result)
			Log.d(TAG, "City mit Citycode: " + importableCityInformation.getCityCode() + " nicht gespeichert!");
		return result;
	}
	
	public boolean saveCityInformationCollection(CityInformationCollection importableCityInformationCollection) {
		boolean result = true;
		for (int i = 0; i < importableCityInformationCollection.getSize(); i++) {
			result = (saveCityInformation(importableCityInformationCollection.getItem(i))&& result);	
		}		
		return result;
	}
	//Lade Informationen zum CityCode aus DB und fuelle CityInformation-Objekt
	public CityInformation getCityInformation(String cityCode){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES+" WHERE "+CITIES_CODE+"='"+cityCode+"'", new String[] {});
		if (cursor.getCount() == 1){
			city = new CityInformation();
			city.setCityCode(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
			city.setCityName(cursor.getString(cursor.getColumnIndex(CITIES_NAME)));
			city.setZipCode(cursor.getString(cursor.getColumnIndex(CITIES_ZIP)));
			cursor.close();
			// LandCode !?
		}else if (cursor.getCount() > 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+cityCode+" kommt > 1 vor!");
		}else if (cursor.getCount() == 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+cityCode+" nicht gefunden!");
		}		
		db.close();
		return city;
	}
	/*
		Fuege alle CityCodes zur CityInformationCollection hinzu, zu denen Wetterstaen vorhanden sind und demzufolge fuer einen
		Graphen zur Verfuegung stehen
	*/
	public CityInformationCollection getAvaiableCityInformations(){
		CityInformationCollection collection = new CityInformationCollection();		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES, new String[] {});		
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
			while (!cursor.isLast()){
				city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))); 
				if (city != null)
					collection.addItem(city);
				cursor.moveToNext();
			}
			cursor.close();
		}
		else {
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "FOUND ROWS: 0");
		}
		db.close();
		return collection;
	}
	public CityInformationCollection getWidgetPlacedCityInformations(){
		// Wird spaeter implementiert!
		SQLiteDatabase db = getReadableDatabase();
		CityInformationCollection collection = new CityInformationCollection();
		db.close();
		/*
			Fuege alle CityCodes zur CityInformationCollection hinzu, die aktuell in einem Widget platziert sind --> Wie wird AKTIV definiert?
			Zusaetzlich werden die CityInformation Objekte mit Informationen zu dem verwendeten Widget und der WidgetId versehen
			
			Die WidgetArten sind verfuegbar via statischem Aufruf von
			CustomWidgetProvider.WIDGET_TYPE_SMALL
			CustomWidgetProvider.WIDGET_TYPE_LARGE
			CustomWidgetProvider.WIDGET_TYPE_FORECAST
		*/
		return collection;
	}
	public CityInformationCollection getActiveCityCodesForSync(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			Fuege alle CityCodes zur CityInformationCollection hinzu, die in einem Widget verlinkt sind
			oder ggf auf einer WatchList stehen, falls dieses Feature implementiert wird
		*/
		return collection;
	}
	
	public void setOptionKey(String keyname, String kevalue){
		// Options beachten ? Stefan fragen, wo welche Options gesetzt werden!
	}
	public String getOptionKey(String keyname){
		// Options spaeter implementieren!
		return "";
	}
	
	/*Private Deklarationen*/

}
