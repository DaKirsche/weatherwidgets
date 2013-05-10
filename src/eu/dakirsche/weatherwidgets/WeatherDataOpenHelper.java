package eu.dakirsche.weatherwidgets;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Datenbankhandler fuer die Wetterdaten. Arbeitet mit Objekten vom Typ DataBase, Cursor und WeatherData.
 * @author JMaKro
 */

public class WeatherDataOpenHelper extends SQLiteOpenHelper {
	
	/*constant variables*/
	private static final String TAG = "DB-Interface";
	public static final String DATABASE_NAME = "WeatherWidgets.db";
	public static final int DATABASE_VERSION = 1;
	
	/*Table names*/
	public static final String TABLE_CITIES = "cities";
	public static final String TABLE_WEATHER = "weather";
	public static final String TABLE_WIDGETS = "widgets";
	
	/*Tables*/
	// City
	public static final String CITIES_ID = "_id";
	public static final String CITIES_CODE = "cityCode";
	public static final String CITIES_NAME = "name";
	public static final String CITIES_ZIP = "zip";
	public static final String CITIES_LAND_SHORT = "landShort";
	public static final String CITIES_LAND_LONG = "landLong";
	public static final String CITIES_LASTUSAGE = "lastUsage";
	// Weather
	public static final String WEATHER_ID = "_id";
	public static final String WEATHER_City_ID = "city_ID";
	public static final String WEATHER_DateTime = "weather_DateTime";
	public static final String WEATHER_Temp_Min = "temp_Min";
	public static final String WEATHER_Temp_Max = "temp_Max";
	public static final String WEATHER_Code = "code";
	// Widget
	public static final String WIDGET_ID = "_id";
	public static final String WIDGET_IDs = "widget_ID";
	public static final String WIDGET_Name = "widget_Name";
	public static final String WIDGET_Type = "widget_Type";
	public static final String WIDGET_City_ID = "city_ID";
	
	
	private static final String TABLE_CITIES_CREATE = "CREATE TABLE '"+TABLE_CITIES+"' ("+	CITIES_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							CITIES_CODE+" VARCHAR(10),"+ 
																							CITIES_NAME+" VARCHAR(80),"+ 
																							CITIES_ZIP+" VARCHAR(10),"+ 
																							CITIES_LAND_SHORT+" VARCHAR(5),"+ 
																							CITIES_LAND_LONG+" VARCHAR(80),"+ 			
																							CITIES_LASTUSAGE+" DATE DEFAULT (Date()));";																						
	
	private static final String TABLE_WEATHER_CREATE = "CREATE TABLE '"+TABLE_WEATHER+"' ("+WEATHER_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							WEATHER_City_ID+" INTEGER NOT NULL CONSTRAINT "+
																							WEATHER_City_ID+" REFERENCES "+TABLE_CITIES+"("+CITIES_ID+") ON DELETE CASCADE,"+ 
																							WEATHER_DateTime+" DATETIME,"+ 
																							WEATHER_Temp_Min+" DOUBLE,"+ 
																							WEATHER_Temp_Max+" DOUBLE,"+ 
																							WEATHER_Code+" INTEGER	);"; 
	
	private static final String TABLE_WIDGETS_CREATE = "CREATE TABLE '"+TABLE_WIDGETS+"' ("+WIDGET_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+ 
																							WIDGET_IDs+" INTEGER NOT NULL,"+ 
																							WIDGET_Name+" VARCHAR(20) ,"+
																							WIDGET_Type+" INTEGER, "+
																							WIDGET_City_ID+" INTEGER NOT NULL CONSTRAINT "+
																							WIDGET_City_ID+" REFERENCES "+TABLE_CITIES+"("+CITIES_ID+") ON DELETE CASCADE);";	

	private static final String DELETE_CITIES_AFTER_3_MONTH = "DELETE FROM "+TABLE_CITIES+" WHERE ((strftime('%m',Date())-strftime('%m',"+CITIES_LASTUSAGE+"))>=3);";
	
	private static final String DELETE_WEAHTER_OLDER_THAN_3_MONTH = "DELETE FROM "+TABLE_WEATHER+" WHERE ((strftime('%m',Date())-strftime('%m',"+WEATHER_DateTime+"))>=3);";
	
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
		// Entferne Tabellen
		if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Upgrade der Datenbank von " + oldV + " zu " + newV);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_WEATHER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_WIDGETS);
		// Lege DB neu an
		onCreate(db);
		// Daten muessen ggf. noch gesichert und wieder hergestellt werden!
	}
	
	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++( Public Deklarationen )+++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	
	/* +++++++++++++++++++++++++++++ Methods for Widgets +++++++++++++++++++++++++++++ */

    /**
     * Verknuepft ein Widget mit einem CityCode
     * @param aWidgetID Systeminterne laufende Nummer des Widgets
     * @param aWidgetType Integerkennung des Widgets. Definiert in der Klasse CustomWidgetProvider.WIDGET_TYPE_[SMALL/LARGE/FORECAST]
     * @param aCityCode Der CityCode der WetterAPI
     * @param aWidgetName Die Bezeichnung des Widgets, um Widgets mit gleicher City zu unterscheiden.
     * @return true bei Erfolg, ansonsten false
     */
    public boolean saveWidget(int aWidgetID, int aWidgetType, String aCityCode, String aWidgetName){
        boolean result;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+TABLE_CITIES+" WHERE "+CITIES_CODE+"='"+aCityCode+"'", null);
        result = (cursor.getCount() == 1);
        if (result){
            cursor.moveToFirst();
            /*Loesche Eintraege aus Datenbank von dieser WidgetId, falls vorhanden*/
            int  anz = db.delete(TABLE_WIDGETS, WIDGET_IDs + " = ?", new String[]{aWidgetID+""});
            if (FunctionCollection.s_getDebugState()){
                Log.d(TAG, "Anzahl geloeschter Altdatensaetze zu WidgetId #"+aWidgetID+": "+anz);
            }
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Widget " + aWidgetID + " wird gespeichert mit CityId " + cursor.getString(cursor.getColumnIndex(CITIES_ID)) + " fuer " + aCityCode);
            // Speicher Widget ab
            values.put(WIDGET_IDs, aWidgetID);
            values.put(WIDGET_Type, aWidgetType);
            values.put(WIDGET_Name, aWidgetName);
            values.put(WIDGET_City_ID, cursor.getString(cursor.getColumnIndex(CITIES_ID)));
            result = (db.insert(TABLE_WIDGETS, null, values) >= 0);
            cursor.close();
        }
        if (!result && FunctionCollection.s_getDebugState())
            Log.d(TAG, "Widget " + aWidgetID + " nicht gespeichert!");
        db.close();
        return result;
    }

	/**Verknuepft ein Widget mit einem CityCode
	 * @param aWidgetID Systeminterne laufende Nummer des Widgets
	 * @param aCityCode Der CityCode der WetterAPI
	 * @return true bei Erfolg, ansonsten false
	 */
	public boolean saveWidget(int aWidgetID, String aCityCode){
        return this.saveWidget(aWidgetID, 0, aCityCode, "");
	}	
	
	/**Gibt die CityInformation zurueck, die mit der widgetID verknuepft ist.
	 * @param aWidgetID ID des Widgets mit der entsprechenden CityInformation
	 * @return Die CityInformation, andernfalls null 
	 */
	public CityInformation getWidgetCityInformation(Integer aWidgetID){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Anfrage: getWidgetCityInformation fuer Widget #" + aWidgetID);
		cursor = db.rawQuery(	"SELECT * FROM " +TABLE_WIDGETS+","+TABLE_CITIES+" WHERE "+WIDGET_IDs+"="+aWidgetID+" AND "+TABLE_CITIES+"."+CITIES_ID+"="+WIDGET_City_ID, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 1){
			city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
			// Setzt die WidgetInformationen von city
        	setWidgetInformation(city,aWidgetID);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "CityInformation gefunden: " + city.toString());
			cursor.close();			
		}else if (cursor.getCount() > 0 && FunctionCollection.s_getDebugState()){
			Log.d(TAG, "Widget : "+aWidgetID+" hat > 1 Cities!!!");
		}else if (cursor.getCount() == 0 && FunctionCollection.s_getDebugState()){
			Log.d(TAG, "Widget: "+aWidgetID+" nicht gefunden!");
		}		
		db.close();
		return city;		
	}
	
	/** Entfernt alle ungueltigen Widgets aus der Datenbank.
	 * @param currentWidgets Beinhaltet die aktuellen WidgetIDs des widgetTypes.
	 * @param aWidgetType Ist der zu pruefende WidgetType in der Datenbank.
	 * @return Gibt die Anzahl der geloeschten Widget-Datensaetze zurueck.
	 */
	public int removeOldWidgets(int[] currentWidgets, int aWidgetType){
		int delCount;
		String aQuery;
		SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        aQuery = "(";
        // Baue Query
        for (int i = 0; i < currentWidgets.length; i++){  
        	aQuery = aQuery + currentWidgets[i];
        	if (i < currentWidgets.length-1)
        		aQuery = aQuery+",";
        }
        aQuery = aQuery + ")";
        // Pruefe DB
        cursor = db.rawQuery("SELECT * FROM "+TABLE_WIDGETS+" WHERE "+WIDGET_IDs+" NOT IN "+aQuery+" AND "+WIDGET_Type+"="+aWidgetType, null);
        cursor.moveToFirst();
        delCount = cursor.getCount();
        // Loesche Datensaetze
        if (delCount > 0){
        	db.execSQL("DELETE FROM "+TABLE_WIDGETS+" WHERE "+WIDGET_IDs+" NOT IN "+aQuery+" AND "+WIDGET_Type+"="+aWidgetType);
        }
        db.close();
        if (FunctionCollection.s_getDebugState())
        	Log.d(TAG, "Es wurden "+delCount+" Datensatze geloescht.");        
		return delCount;
	}
	
	/* +++++++++++++++++++++++++++++ Methods for WeahterData +++++++++++++++++++++++++++++ */
	
	/**Gibt den Datensatz mit dem aktuellen Datum und Uhrzeit zurueck, welche cityCode zugeordnet sind.
	 * @param aCityCode City fuer die die Wetterdaten bestimmt sind.
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String aCityCode){
		Date nowDateTime = new Date(System.currentTimeMillis());
		return getWeatherData(aCityCode,nowDateTime);
	}
	
	/**Gibt  den Datensatz mit dem aktuellen Datum und der in time angegebenen Uhrzeit zurueck, welche cityCode zugeordnet sind.
	 * @param aCityCode City fuer die die Wetterdaten bestimmt sind.
	 * @param aTime Die zu bestimmende Uhrzeit
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String aCityCode, long aTime){
		Date nowDateTime = new Date(System.currentTimeMillis());
		nowDateTime.setTime(aTime);
		return getWeatherData(aCityCode,nowDateTime);
	}
	
	/**Gibt den Datensatz zurueck, der dateTimeValue am naechsten liegt und cityCode zugeordnet ist.
	 * @param aCityCode City fuer die die Wetterdaten bestimmt sind.
	 * @param dateTimeValue Datum und Uhrzeit fuer die Wetterdaten
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String aCityCode, java.util.Date dateTimeValue){
		String queryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateTimeValue);
		int cityID = getCityIDByCityCode(aCityCode);
		SQLiteDatabase db = getReadableDatabase();
		WeatherData weatherData = null;		
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT "+TABLE_WEATHER+".*, CASE WHEN "+
								"((strftime('%s','"+queryDate+"') - strftime('%s',DateBefore."+WEATHER_DateTime+")) < "+
								"(strftime('%s',NextDate."+WEATHER_DateTime+") - strftime('%s','"+queryDate+"'))) then "+
								"DateBefore."+WEATHER_ID+" ELSE NextDate."+WEATHER_ID+" END AS FinalDateID " +
								"FROM "+TABLE_WEATHER+",( SELECT * FROM "+TABLE_WEATHER+" "+
								"WHERE "+WEATHER_City_ID+"="+cityID+" AND "+
								"DATE("+WEATHER_DateTime+") = Date('"+queryDate+"') AND "+
								"Time("+WEATHER_DateTime+") <= Time('"+queryDate+"') "+
								"ORDER BY "+WEATHER_DateTime+" DESC LIMIT 1) AS DateBefore, "+
								"( SELECT * FROM "+TABLE_WEATHER+" "+
								"WHERE "+WEATHER_City_ID+"="+cityID+" AND "+
								"DATE("+WEATHER_DateTime+") = Date('"+queryDate+"') AND "+
								"Time("+WEATHER_DateTime+") >= Time('"+queryDate+"') "+
								"ORDER BY "+WEATHER_DateTime+" ASC LIMIT 1) AS NextDate "+
								"WHERE FinalDateID = "+TABLE_WEATHER+"."+WEATHER_ID, null							
							);
		cursor.moveToFirst();
		if (cursor.getCount() > 0){
            weatherData = new WeatherData();
			weatherData.setCityInformation(getCityInformation(aCityCode));
			weatherData.setDateTimeStr(cursor.getString(cursor.getColumnIndex(WEATHER_DateTime)));
			weatherData.setTemperatures(cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Min)), 
										cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Max)));
			weatherData.setWeatherCode(cursor.getInt(cursor.getColumnIndex(WEATHER_Code)));
			cursor.close();
		}
		db.close();
		return weatherData;
	}
	   
	/**Gibt eine Sequenz von Wetterdaten zurueck, die in einer Range von startDate bis endDate gebildet wird. 
	 * @param aCityID Die Datenbank-ID der City fuer die die Sequenz bestimmt wird.
	 * @param aStartDate Startdatum fuer die Wettersequenz.
	 * @param aEndDate Enddatum fuer die Wettersequenz.
	 * @return WeatherDataCollection, wenn keine Wetterdaten verfuegbar sind mit 0 Elementen.
	 */
	public WeatherDataCollection getWeatherSequence(int aCityID, Date aStartDate, Date aEndDate){
		long startTime = 0;
		long endTime = 0;
		Calendar cal = Calendar.getInstance(); // locale-specific
		// Set Start Time
		cal.setTime(aStartDate);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startTime = cal.getTimeInMillis();
		// Set End Time
		cal.setTime(aEndDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 99);
		endTime = cal.getTimeInMillis();
		return getWeatherSequence(aCityID,aStartDate,aEndDate,startTime,endTime);
	}
	
	/**Gibt eine Sequenz von Wetterdaten zurueck, die in einer Range von startDate bis endDate gebildet wird. 
	 * Zusaetzlich kann auch die startTime bzw. endTime angegeben werden. 
	 * @param aCityID Die Datenbank-ID der City fuer die die Sequenz bestimmt wird. 
	 * @param aStartDate Startdatum fuer die Wettersequenz
	 * @param aEndDate Enddatum fuer die Wettersequenz
	 * @param aStartTime Startzeit fuer die Wettersequenz 
	 * @param aEndTime Endzeit fuer die Wettersequenz
	 * @return WeatherDataCollection, wenn keine Wetterdaten verfuegbar sind mit 0 Elementen.
	 */
	public WeatherDataCollection getWeatherSequence(int aCityID, Date aStartDate, Date aEndDate, long aStartTime, long aEndTime){
		aStartDate.setTime(aStartTime);
		aEndDate.setTime(aEndTime);
		String strStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(aStartDate);
		String strEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(aEndDate);
		WeatherDataCollection collection = new WeatherDataCollection();		
		SQLiteDatabase db = getReadableDatabase();
		WeatherData weatherData = null;
		Cursor cursor;		
		
		cursor = db.rawQuery(	"SELECT * FROM "+TABLE_WEATHER+","+TABLE_CITIES+" WHERE "+
								WEATHER_City_ID+"="+TABLE_CITIES+"."+CITIES_ID+" AND "+
								TABLE_CITIES+"."+CITIES_ID+"="+aCityID+" AND "+
								"DATETIME("+WEATHER_DateTime+") >= DATETIME('"+strStartDate+"') AND "+
								"DATETIME("+WEATHER_DateTime+") <= DATETIME('"+strEndDate+"') AND "+
								"Time("+WEATHER_DateTime+") >= Time('"+strStartDate+"') AND "+
								"Time("+WEATHER_DateTime+") <= Time('"+strEndDate+"') "+
								"ORDER BY "+WEATHER_DateTime+" ASC" , null
							);	
		cursor.moveToFirst();
		if (cursor.getCount() > 0){			
			while (!cursor.isAfterLast()){
				weatherData = new WeatherData();
				weatherData.setCityInformation(getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))));
				weatherData.setDateTimeStr(cursor.getString(cursor.getColumnIndex(WEATHER_DateTime)));
				weatherData.setTemperatures(cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Min)), 
											cursor.getDouble(cursor.getColumnIndex(WEATHER_Temp_Max)));
				weatherData.setWeatherCode(cursor.getInt(cursor.getColumnIndex(WEATHER_Code)));
				collection.addItem(weatherData);
				cursor.moveToNext();
			}
			cursor.close();
		}
		else {
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Keine Wetterdaten vom "+strStartDate+" bis "+strEndDate+" verfuegbar!");
		}	
		db.close();
		return collection;
	}
    
	/**Speichert die importableWeatherData in der Datenbank. 
	 * @param aWeatherData WeatherData-Object, welches gespeichert werden soll.
	 * @return true, wenn erfolgreich gespeichert wurde, anderfalls false
	 */
	public boolean saveWeatherData(WeatherData aWeatherData){
		boolean result;
		ContentValues values = new ContentValues();
		String weatherDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(aWeatherData.getDate());
		removeOldWeatherData(aWeatherData);
		SQLiteDatabase db = getWritableDatabase();
		// Speichere WeatherData
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Speichere WeatherData fuer " +weatherDate );
        // Uebergebe Daten
        values.put(WEATHER_City_ID, getCityID(aWeatherData.getCityInformation()));
        values.put(WEATHER_DateTime,weatherDate);
        values.put(WEATHER_Temp_Min,aWeatherData.getTemperaturMin());
        values.put(WEATHER_Temp_Max,aWeatherData.getTemperatureMax());
        values.put(WEATHER_Code,aWeatherData.getWeatherCode());
        // Speichern
        result =  (db.insert(TABLE_WEATHER, null, values) >= 0);
        if (!result && FunctionCollection.s_getDebugState())
            Log.d(TAG, "WeatherData wurde nicht gespeichert!");
		db.close();
		return result;
	}
	
	/*+++++++++++++++++++++++++++++ Methods for CityInformation +++++++++++++++++++++++++++++*/
	
	/**Speichert die importableCityInformation in der Datenbank. 
	 * @param aCityInformation CityInformation-Object, welches gespeichert werden soll.
	 * @return true, wenn erfolgreich gespeichert wurde oder bereits vorhanden, anderfalls false
	 */
	public boolean saveCityInformation(CityInformation aCityInformation) {
		boolean result;
		ContentValues values = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT * FROM " + TABLE_CITIES +" WHERE "+CITIES_CODE+"='"+aCityInformation.getCityCode()+"'", null);
		cursor.moveToFirst();
		result = (cursor.getCount() > 0); 
		if (!result) {		
			values.put(CITIES_CODE, aCityInformation.getCityCode());
			values.put(CITIES_NAME, aCityInformation.getCityName());
			values.put(CITIES_ZIP, aCityInformation.getZipCode());
            values.put(CITIES_LAND_SHORT, aCityInformation.getLandCode());
            values.put(CITIES_LAND_LONG, aCityInformation.getAdditionalLandInformations());           
            // Speichern
			result = (db.insert(TABLE_CITIES, null, values) >= 0); 
			cursor.close();
		}		
		db.close();
		if (!result && FunctionCollection.s_getDebugState())
			Log.d(TAG, "City mit Citycode: " + aCityInformation.getCityCode() + " nicht gespeichert!");
		return result;
	}
	
	/**Speichert alle CityInformation-Objecte der Collection in der Datenbank.
	 * @param aCityInformationCollection Collection mit den CityInformation-Objecten
	 * @return true, wenn alle Objeckte erfolgreich gespeichert wurden, anderfalls false
	 */
	public boolean saveCityInformationCollection(CityInformationCollection aCityInformationCollection) {
		boolean result = true;
		for (int i = 0; i < aCityInformationCollection.getSize(); i++) {
			result = (saveCityInformation(aCityInformationCollection.getItem(i))&& result);	
		}		
		return result;
	}
	
	/**Lade Informationen zum CityCode aus der Datenbank und fuelle CityInformation-Objekt
	 * @param aCityCode Der CityCode der WetterAPI  
	 * @return CityInformation-Objekt, welches cityCode zugeordnet ist, ansonsten null 
	 */
	public CityInformation getCityInformation(String aCityCode){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES + " WHERE " + CITIES_CODE + "='"+aCityCode+"'", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 1){
            city = new CityInformation();
            // Lade City aus DB
            city.setCityId(cursor.getInt(cursor.getColumnIndex(CITIES_ID)));
			city.setCityCode(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
			city.setCityName(cursor.getString(cursor.getColumnIndex(CITIES_NAME)));
			city.setZipCode(cursor.getString(cursor.getColumnIndex(CITIES_ZIP)));
			city.setLand(cursor.getString(cursor.getColumnIndex(CITIES_LAND_SHORT)),
       			 		 cursor.getString(cursor.getColumnIndex(CITIES_LAND_LONG)));
			// WidgetInformationen werden in setWidgetInformation gesetzt!
			cursor.close();			
		}else if (cursor.getCount() > 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+aCityCode+" kommt > 1 vor!");
		}else if (cursor.getCount() == 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+aCityCode+" nicht gefunden!");
		}		
		db.close();
		refreshCityUsage(city);		
		return city;
	}

	/**Gibt alle CityCodes in einer CityInformationCollection zurueck, zu denen Wetterdaten vorhanden sind und demzufolge fuer einen
	 * Graphen zur Verfuegung stehen.
	 * @return CityInformationCollection, ggf. mit 0 Elementen, wenn keine Cities verfuegbar sind.
	 */
	public CityInformationCollection getAvaiableCityInformations(){
		CityInformationCollection collection = new CityInformationCollection();		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES, null);		
		cursor.moveToFirst();
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
		if (cursor.getCount() > 0){			
			while (!cursor.isAfterLast()){
				city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))); 
				if (city != null)
					collection.addItem(city);
				cursor.moveToNext();
			}
			cursor.close();
		}
		db.close();
		return collection;
	}
	
	/**Gibt alle CityCodes in einer CityInformationCollection zurueck, die aktuell in einem Widget platziert sind.		
	 * Zusaetzlich werden die CityInformation Objekte mit Informationen zu dem verwendeten Widget und der WidgetId versehen
	 * @return CityInformationCollection, ggf. mit 0 Elementen, wenn keine Cities verfuegbar sind.
	 */
	public CityInformationCollection getWidgetPlacedCityInformations(){
		SQLiteDatabase db = getReadableDatabase();
		CityInformationCollection collection = new CityInformationCollection();
		CityInformation city = null;
        Cursor cursor = db.rawQuery("SELECT * FROM (" + TABLE_WIDGETS + " LEFT OUTER JOIN " + TABLE_CITIES + " ON " + TABLE_WIDGETS+"."+WIDGET_City_ID+" = " + TABLE_CITIES + "." + CITIES_ID + ") ORDER BY " + TABLE_WIDGETS + "." + WIDGET_Name, null);
        if (cursor.getCount() > 0){
            // Es wurden Widget-CityInformation gefunden
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
            	city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
            	// Setzt die WidgetInformationen von city
            	setWidgetInformation(city, cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)));
            	if (city != null)
					collection.addItem(city);
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
		return collection;
	}
	
	/**Gibt alle CityCodes in einer CityInformationCollection zurueck, die in einem Widget verlinkt sind 
	 * oder ggf. auf einer WatchList stehen. Es wird <u>empfohlen</u> removeOldWidgets vorher auszufuehren!
	 * @return CityInformationCollection, ggf. mit 0 Elementen, wenn keine Cities verfuegbar sind.
	 */
	public CityInformationCollection getActiveCityCodesForSync(){
		CityInformationCollection collection = new CityInformationCollection();
		CityInformation city = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_WIDGETS + "," + TABLE_CITIES + 
							 " WHERE " + WIDGET_City_ID + "=" + TABLE_CITIES + "." + CITIES_ID +
							 " GROUP BY "+WIDGET_City_ID+
							 " ORDER BY "+TABLE_CITIES+"."+CITIES_NAME, null);		
		cursor.moveToFirst();
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
		if (cursor.getCount() > 0){			
			while (!cursor.isAfterLast()){
				city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE))); 
				// Setzt die WidgetInformationen von city
            	setWidgetInformation(city, cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)));
				if (city != null)
					collection.addItem(city);
				cursor.moveToNext();
			}
			cursor.close();
		}
		db.close();
		return collection;
	}
	
	/*----------------------------------------------------------( Private Deklarationen )----------------------------------------------------------*/
	
	/** Gibt die ID von aCity aus der Datenbank zurueck.
	 * @param aCity Zu bestimmende ID
	 * @return -1, wenn die city in der Datenbank keine ID hat bzw. nicht vorhanden ist.
	 */
	private int getCityID(CityInformation aCity){
		int cityID = -1;
		if (aCity != null){
			cityID = getCityIDByCityCode(aCity.getCityCode());
		}
		return cityID;		
	}
	
	/**Gibt die ID von der City mit dem cityCode aus der Datenbank zurueck.
	 * @param cityCode Zu bestimmende ID 
	 * @return -1, wenn die city in der Datenbank keine ID hat bzw. nicht vorhanden ist.
	 */
	private int getCityIDByCityCode(String cityCode){
		int cityID = -1;
		SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+TABLE_CITIES+" WHERE "+CITIES_CODE+"='"+cityCode+"'", null);
        if (cursor.getCount() == 1){
        	cursor.moveToFirst();
        	cityID = cursor.getInt(cursor.getColumnIndex(CITIES_ID));
        }
        return cityID;
	}
	
	/**Das Verwendungs-Datum von aCity wird auf das aktuelle Datum gesetzt.
	 * Cities, die aelter sind als 3 Monate, werden aus der Datenbank entfernt.
	 * @param aCity Die City von der das Verwendungs-Datum gesetzt wird.
	 */
	private void refreshCityUsage(CityInformation aCity){
		SQLiteDatabase db = getWritableDatabase();
		if (aCity != null){
			// Verwendungs-Datum von aCity auf aktuelles Datum setzen
			db.execSQL("UPDATE "+TABLE_CITIES+" SET "+CITIES_LASTUSAGE+"=Date() WHERE "+CITIES_CODE+"='"+aCity.getCityCode()+"'");
		}
		// Cities entfernen die laenger als 3 Monate nicht verwendet wurden
		db.execSQL(DELETE_CITIES_AFTER_3_MONTH);
	}
	
	/**
	 * Entfernt Wetterdaten aus der Datenbank, die aelter als 3 Monate sind und 
	 * von der City mit gleichen Datum, damit Daten nur einmal vorkommen und immer aktuell sind.
	 */
	private void removeOldWeatherData(WeatherData aWeatherData){
		SQLiteDatabase db = getWritableDatabase();
		// Entferne Wetterdaten von der City mit gleichen Datum, damit Daten nur einmal vorkommen und immer aktuell sind. 
		if (aWeatherData != null && aWeatherData.getCityInformation() != null){
			String weatherDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(aWeatherData.getDate());
			db.execSQL("DELETE FROM "+TABLE_WEATHER+" WHERE DateTime('"+weatherDate+"') = DateTime("+WEATHER_DateTime+") AND "+WEATHER_City_ID+"="+getCityID(aWeatherData.getCityInformation()));
		}
        // Wetterdaten entfernen die aelter als 3 Monate sind
		db.execSQL(DELETE_WEAHTER_OLDER_THAN_3_MONTH);
        // Ggf. noch mehr Code
	}
	
	/**Methode setzt die Widget-Informationen (Type, ID, Name) von aCity.
	 * @param aCity Die City zu der die Widget-Informationen gesetzt werden sollen.
	 * @param aWidgetID Die ID des Widgets.
	 */
	private void setWidgetInformation(CityInformation aCity, int aWidgetID){
		if (aCity != null){
			SQLiteDatabase db = getReadableDatabase();
	        Cursor cursor;
	        cursor = db.rawQuery("SELECT * FROM "+TABLE_WIDGETS+" WHERE "+WIDGET_City_ID+"='"+getCityID(aCity)+"' AND "+WIDGET_IDs+"="+aWidgetID, null);
	        if (cursor.getCount() == 1){
	        	cursor.moveToFirst();
	        	aCity.setWidget(cursor.getInt(cursor.getColumnIndex(WIDGET_Type)),		// Widget Type 
								cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)), 	  	// Widget ID, hier kann ggf. auch der Parameter uebergeben werden!
								cursor.getString(cursor.getColumnIndex(WIDGET_Name)));	// Widget Name
	        }
		}// END IF
	}// END setWidetInformation

}
