package eu.dakirsche.weatherwidgets;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Datenbankhandler fuer die Wetterdaten. Arbeitet mit Objekten vom Typ DataBase, Cursor und WeatherData
 * @author JMaKro
 */

public class WeatherDataOpenHelper extends SQLiteOpenHelper {
	
	/*constant variables*/
	private static final String TAG = "DB-Interface";
	public static final String DATABASE_NAME = "WeatherWidgets.db";
	public static final int DATABASE_VERSION = 3;
	
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
	
	/* Methods for Widgets */

    /**
     * Verknuepft ein Widget mit einem CityCode
     * @param widgetID Systeminterne laufende Nummer des Widgets
     * @param widgetType Integerkennung des Widgets. Definiert in der Klasse CustomWidgetProvider.WIDGET_TYPE_[SMALL/LARGE/FORECAST]
     * @param cityCode Der CityCode der WetterAPI
     * @param widgetName Die Bezeichnung des Widgets, um Widgets mit gleicher City zu unterscheiden.
     * @return true bei Erfolg, ansonsten false
     */
    public boolean saveWidget(int widgetID, int widgetType, String cityCode, String widgetName){
        boolean result;
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+TABLE_CITIES+" WHERE "+CITIES_CODE+"='"+cityCode+"'", new String[] {});
        result = (cursor.getCount() == 1);
        if (result){
            cursor.moveToFirst();
            /*Loesche Eintraege aus Datenbank von dieser WidgetId, falls vorhanden*/
            int  anz = db.delete(TABLE_WIDGETS, WIDGET_IDs + " = ?", new String[]{widgetID+""});
            if (FunctionCollection.s_getDebugState()){
                Log.d(TAG, "Anzahl geloeschter Altdatensaetze zu WidgetId #"+widgetID+": "+anz);
            }
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "Widget " + widgetID + " wird gespeichert mit CityId " + cursor.getString(cursor.getColumnIndex(CITIES_ID)) + " fuer " + cityCode);
            // Speicher Widget ab
            values.put(WIDGET_IDs, widgetID);
            values.put(WIDGET_Type, widgetType);
            values.put(WIDGET_Name, widgetName);
            values.put(WIDGET_City_ID, cursor.getString(cursor.getColumnIndex(CITIES_ID)));
            result = (db.insert(TABLE_WIDGETS, null, values) >= 0);
            cursor.close();
        }
        if (!result && FunctionCollection.s_getDebugState())
            Log.d(TAG, "Widget " + widgetID + " nicht gespeichert!");
        db.close();
        return result;
    }

	/**Verknuepft ein Widget mit einem CityCode
	 * @param widgetID Systeminterne laufende Nummer des Widgets
	 * @param cityCode Der CityCode der WetterAPI
	 * @return true bei Erfolg, ansonsten false
	 */
	public boolean saveWidget(int widgetID, String cityCode){
        return this.saveWidget(widgetID, 0, cityCode, "");
	}	
	
	/**Gibt die CityInformation zurueck, die mit der widgetID verknuepft ist.
	 * @param widgetID ID des Widgets mit der entsprechenden CityInformation
	 * @return Die CityInformation, andernfalls null 
	 */
	public CityInformation getWidgetCityInformation(Integer widgetID){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Anfrage: getWidgetCityInformation fuer Widget #" + widgetID);
		cursor = db.rawQuery(	"SELECT * FROM " +TABLE_WIDGETS+","+TABLE_CITIES+" WHERE "+
								WIDGET_IDs+"=? AND "+TABLE_CITIES+"."+CITIES_ID+"="+WIDGET_City_ID, new String[] {widgetID.toString()});
		cursor.moveToFirst();
		if (cursor.getCount() == 1){
			city = getCityInformation(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
           /* city.setCityCode(cursor.getString(cursor.getColumnIndex(CITIES_CODE)));
            city.setZipCode(cursor.getString(cursor.getColumnIndex(CITIES_ZIP)));
            city.setLand(cursor.getString(cursor.getColumnIndex(CITIES_LAND_SHORT)), cursor.getString(cursor.getColumnIndex(CITIES_LAND_LONG)));
            city.setWidget(cursor.getInt(cursor.getColumnIndex(WIDGET_Type)), cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)), cursor.getString(cursor.getColumnIndex(WIDGET_Name)));
            city.setCityName(cursor.getString(cursor.getColumnIndex(CITIES_NAME)));  */
            /*Schmeisst eine nullpointer Exception*/
            if (city == null && FunctionCollection.s_getDebugState())
                Log.d(TAG, "Keine CityInformation gefunden!");
            if (city != null && FunctionCollection.s_getDebugState())
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
	
	/** Entfernt alle ungueltigen Widgets aus der Datenbank.
	 * @param currentWidgets Beinhaltet die aktuellen WidgetIDs, also die gerade auf dem Homescreen laufen.
	 */
	public void removeOldWidgets(int[] currentWidgets){
		Integer iID;
		SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM "+TABLE_WIDGETS, new String[] {});
        cursor.moveToFirst();
        Arrays.sort(currentWidgets);
        // Gehe alle Widgets aus der DB durch ...
        for (int i = 0; i < cursor.getCount(); i++){  
        	iID = cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)); 
        	// Wenn WidgetID nicht mehr aktuell ist ...
        	if (Arrays.binarySearch(currentWidgets, iID) == -1){
        		db.delete(TABLE_WIDGETS, WIDGET_IDs + " = ?", new String[]{iID+""});
        	}
        	cursor.moveToNext();
        }
        db.close();
	}
	
	/* Methods for WeahterData */
	
	/**Gibt den Datensatz mit dem aktuellen Datum und Uhrzeit zurueck, welche cityCode zugeordnet sind.
	 * @param cityCode City fuer die die Wetterdaten bestimmt sind.
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String cityCode){
		Date nowDateTime = new Date(System.currentTimeMillis());
		return getWeatherData(cityCode,nowDateTime);
	}
	
	/**Gibt den Datensatz mit dem aktuellen Datum und der in time angegebenen Uhrzeit zurueck, welche cityCode zugeordnet sind.
	 * @param cityCode City fuer die die Wetterdaten bestimmt sind.
	 * @param time Die zu bestimmende Uhrzeit
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String cityCode, long time){
		Date nowDateTime = new Date(System.currentTimeMillis());
		nowDateTime.setTime(time);
		return getWeatherData(cityCode,nowDateTime);
	}
	
	/**Gibt den Datensatz zurueck, der dateTimeValue am naechsten liegt und cityCode zugeordnet ist.
	 * @param cityCode City fuer die die Wetterdaten bestimmt sind.
	 * @param dateTimeValue Datum und Uhrzeit fuer die Wetterdaten
	 * @return Die Wetterdaten, andernfalls null
	 */
	public WeatherData getWeatherData(String cityCode, java.util.Date dateTimeValue){
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
			weatherData.setWeatherCode(cursor.getInt(cursor.getColumnIndex(WEATHER_Code)));
			cursor.close();
		}
		db.close();
		removeOldWeatherData();
		return weatherData;
	}
	   
	/**Gibt eine Sequenz von Wetterdaten zurueck, die in einer Range von startDate bis endDate gebildet wird. 
	 * @param startDate Startdatum fuer die Wettersequenz.
	 * @param endDate Enddatum fuer die Wettersequenz.
	 * @return WeatherDataCollection, wenn keine Wetterdaten verfuegbar sind mit 0 Elementen.
	 */
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
	
	/**Gibt eine Sequenz von Wetterdaten zurueck, die in einer Range von startDate bis endDate gebildet wird. 
	 * Zusaetzlich kann auch die startTime bzw. endTime angegeben werden.  
	 * @param startDate Startdatum fuer die Wettersequenz
	 * @param endDate Enddatum fuer die Wettersequenz
	 * @param startTime Startzeit fuer die Wettersequenz 
	 * @param endTime Endzeit fuer die Wettersequenz
	 * @return WeatherDataCollection, wenn keine Wetterdaten verfuegbar sind mit 0 Elementen.
	 */
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
		cursor.moveToFirst();
		if (cursor.getCount() > 0){			
			while (!cursor.isLast()){
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
	 * @param importableWeatherData WeatherData-Object, welches gespeichert werden soll.
	 * @return true, wenn erfolgreich gespeichert wurde, anderfalls false
	 */
	public boolean saveWeatherData(WeatherData importableWeatherData){
		boolean result;
		ContentValues values = new ContentValues();
		String weatherDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(importableWeatherData.getDate());
		CityInformation city = getCityInformation(importableWeatherData.getCityCode());
		SQLiteDatabase db = getWritableDatabase();
		result = (city != null);
		try {
            if (result){
                if (FunctionCollection.s_getDebugState())
                    Log.d(TAG, "Speichere WeatherData fuer " +weatherDate );
                values.put(WEATHER_City_ID, getCityID(importableWeatherData.getCityInformation()));
                values.put(WEATHER_DateTime,weatherDate);
                values.put(WEATHER_Temp_Min,importableWeatherData.getTemperaturMin());
                values.put(WEATHER_Temp_Max,importableWeatherData.getTemperatureMax());
                values.put(WEATHER_Code,importableWeatherData.getWeatherCode());
                result =  (db.insert(TABLE_WEATHER, null, values) >= 0);
                if (!result)
                    Log.d(TAG, "WeatherData wurde nicht gespeichert!");
            }else
                Log.d(TAG, "City mit Citycode: " + importableWeatherData.getCityCode() + " nicht gefunden!");
        }
        catch (Exception e){
               Log.e(TAG, "Exception in SaveWeather", e);
            if (FunctionCollection.s_getDebugState()){
                if (city != null)
                    Log.d(TAG, "City: " + city.toString());
                if (importableWeatherData != null)
                    Log.d(TAG, "WeatherData: " + importableWeatherData.toString());
            }
               result = false;
        }
		db.close();
		return result;
	}
	
	/* Methods for CityInformation */
	
	/**Speichert die importableCityInformation in der Datenbank. 
	 * @param importableCityInformation CityInformation-Object, welches gespeichert werden soll.
	 * @return true, wenn erfolgreich gespeichert wurde, anderfalls false
	 */
	public boolean saveCityInformation(CityInformation importableCityInformation) {
		boolean result;
		ContentValues values = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(	"SELECT * FROM " + TABLE_CITIES + 
								" WHERE "+CITIES_CODE+"='"+importableCityInformation.getCityCode()+"'", new String[] {});
		cursor.moveToFirst();
		result = (cursor.getCount() > 0); 
		if (!result) {		
			values.put(CITIES_CODE, importableCityInformation.getCityCode());
			values.put(CITIES_NAME, importableCityInformation.getCityName());
			values.put(CITIES_ZIP, importableCityInformation.getZipCode());
            values.put(CITIES_LAND_SHORT, importableCityInformation.getLandCode());
            values.put(CITIES_LAND_LONG, importableCityInformation.getAdditionalLandInformations());            
			result = (db.insert(TABLE_CITIES, null, values) >= 0); 
			cursor.close();
		}		
		db.close();
		if (!result)
			Log.d(TAG, "City mit Citycode: " + importableCityInformation.getCityCode() + " nicht gespeichert!");
		return true;
	}
	
	/**Speichert alle CityInformation-Objecte der Collection in der Datenbank.
	 * @param importableCityInformationCollection Collection mit den CityInformation-Objecten
	 * @return true, wenn alle Objeckte erfolgreich gespeichert wurden, anderfalls false
	 */
	public boolean saveCityInformationCollection(CityInformationCollection importableCityInformationCollection) {
		boolean result = true;
		for (int i = 0; i < importableCityInformationCollection.getSize(); i++) {
			result = (saveCityInformation(importableCityInformationCollection.getItem(i))&& result);	
		}		
		return result;
	}
	
	/**Lade Informationen zum CityCode aus der Datenbank und fuelle CityInformation-Objekt
	 * @param cityCode Der CityCode der WetterAPI  
	 * @return CityInformation-Objekt, welches cityCode zugeordnet ist, ansonsten null 
	 */
	public CityInformation getCityInformation(String cityCode){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		CityInformation city = null;
		cursor = db.rawQuery("SELECT * FROM " + TABLE_WIDGETS + "," + TABLE_CITIES + " WHERE " + WIDGET_City_ID + "=" + TABLE_CITIES + "." + CITIES_ID + " AND " + CITIES_CODE + "='"+cityCode+"'", new String[] {});
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
			city.setWidget(	cursor.getInt(cursor.getColumnIndex(WIDGET_Type)),		// Widget Type 
		    				cursor.getInt(cursor.getColumnIndex(WIDGET_IDs)), 	  	// Widget ID
		    				cursor.getString(cursor.getColumnIndex(WIDGET_Name)));	// Widget Name
			cursor.close();			
		}else if (cursor.getCount() > 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+cityCode+" kommt > 1 vor!");
		}else if (cursor.getCount() == 0){
			if (FunctionCollection.s_getDebugState())
				Log.d(TAG, "Citycode: "+cityCode+" nicht gefunden!");
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
		cursor = db.rawQuery("SELECT * FROM " + TABLE_CITIES, new String[] {});		
		cursor.moveToFirst();
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
		if (cursor.getCount() > 0){			
			while (!cursor.isLast()){
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
        Cursor cursor = db.rawQuery("SELECT * FROM (" + TABLE_WIDGETS + " LEFT OUTER JOIN " + TABLE_CITIES + " ON " + TABLE_WIDGETS+"."+WIDGET_City_ID+" = " + TABLE_CITIES + "." + CITIES_ID + ") ORDER BY " + TABLE_CITIES + "." + CITIES_NAME, new String[]{});
        if (cursor.getCount() > 0){
            // Es wurden Widget-CityInformation gefunden
            cursor.moveToFirst();
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
							 " GROUP BY "+WIDGET_City_ID, new String[] {});		
		cursor.moveToFirst();
		if (FunctionCollection.s_getDebugState())
			Log.d(TAG, "FOUND ROWS: " + cursor.getCount());
		if (cursor.getCount() > 0){			
			while (!cursor.isLast()){
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
	
	/*----------------------------------------------------------( Private Deklarationen )----------------------------------------------------------*/
	
	/** Gibt die ID von aCity aus der Datenbank zurueck.
	 * @param aCity Zu bestimmende ID
	 * @return -1, wenn die city in der Datenbank keine ID hat bzw. nicht vorhanden ist.
	 */
	private int getCityID(CityInformation aCity){
		int cityID = -1;
		if (aCity != null){
			SQLiteDatabase db = getReadableDatabase();
	        Cursor cursor;
	        cursor = db.rawQuery("SELECT * FROM "+TABLE_CITIES+" WHERE "+CITIES_CODE+"='"+aCity.getCityCode()+"'", new String[] {});
	        if (cursor.getCount() == 1){
	        	cursor.moveToFirst();
	        	cityID = cursor.getInt(cursor.getColumnIndex(CITIES_ID));
	        }
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
        db.close();
	}
	
	/**
	 * Entfernt Wetterdaten aus der Datenbank, die aelter als 3 Monate sind!
	 */
	private void removeOldWeatherData(){
		SQLiteDatabase db = getWritableDatabase();
        // Wetterdaten entfernen die aelter als 3 Monate sind
		db.execSQL(DELETE_WEAHTER_OLDER_THAN_3_MONTH);
        // Ggf. noch mehr Code
        db.close();
	}

}
