package eu.dakirsche.weatherwidgets;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * Datenbankhandler f�r die Wetterdaten. Arbeitet mit Objekten vom Typ DataBase, Cursor und WeatherData
 *
 */
public class WeatherDataOpenHelper extends SQLiteOpenHelper implements WeatherDataOpenHelperInterface {
	
	/*Klassenvariablen*/
	
	/*Klassenkonstanten*/
	public static final String DATABASE_NAME = "WeatherWidgets";
	public static final int DATABASE_VERSION = 1;
	
	/*Konstruktoren*/
	public WeatherDataOpenHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/*Override Methoden*/
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// Hier m�ssen die Datenbanktabellen angelegt werden

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// Datenbankinhalte m�ssen ausgelesen werden, in den Speicher geschrieben werden.
		// Anschlie�end Tabellen Droppen, Tabellen neu anlegen und dann Datens�tze zur�ckschreiben

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
		//Gibt den Datensatz zur�ck, der date und time am n�chsten liegt und cityCode zugeordnet ist
		return new WeatherData();
	}
	   
	public WeatherDataCollection getWeatherSequence(int startDay, int endDay){
		//Gibt getWeatherSequence(int startDay, int endDay, int startTime, int endTime) mit minimaler Zeit f�r Start und maximaler Zeit f�r End wieder
		return new WeatherDataCollection();
	}
	public WeatherDataCollection getWeatherSequence(int startDay, int endDay, int startTime, int endTime){
		return new WeatherDataCollection();
	}
	public CityInformation loadCityInformation(String cityCode){
		//lade Informationen zum CityCode aus DB und f�lle CityInformation-Objekt
		return new CityInformation();
	}
	public boolean saveWeatherData(WeatherData importableWeatherData){
		/*Speichert die WeatherData in der Datenbank. Gibt false zur�ck, wenn die Daten nicht gespeichert wurden, sonst true*/
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
			F�ge alle CityCodes zur CityInformationCollection hinzu, zu denen Wetterstaen vorhanden sind und demzufolge f�r einen
			Graphen zur Verf�gung stehen
		*/
		return collection;
	}
	public CityInformationCollection getWidgetPlacedCityInformations(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			F�ge alle CityCodes zur CityInformationCollection hinzu, die aktuell in einem Widget platziert sind
			Zus�tzlich werden die CityInformation Objekte mit Informationen zu dem verwendeten Widget und der WidgetId versehen
			
			Die WidgetArten sind verf�gbar via statischem Aufruf von
			CustomWidgetProvider.WIDGET_TYPE_SMALL
			CustomWidgetProvider.WIDGET_TYPE_LARGE
			CustomWidgetProvider.WIDGET_TYPE_FORECAST
		*/
		return collection;
	}
	public CityInformationCollection getActiveCityCodesForSync(){
		CityInformationCollection collection = new CityInformationCollection();
		/*
			F�ge alle CityCodes zur CityInformationCollection hinzu, die in einem Widget verlinkt sind
			oder ggf auf einer WatchList stehen, falls dieses Feature implementiert wird
		*/
		return collection;
	}
	/*Private Deklarationen*/
	
	/*Protected Deklarationen*/

}
