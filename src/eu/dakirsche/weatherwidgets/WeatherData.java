package eu.dakirsche.weatherwidgets;

import android.util.Log;

/**
 * 
 * Diese Klasse beinhaltet die Daten von Wetterinformationen zu einem bestimmten Zeitpunkt.
 * Die Wetterdaten werden �ber den DB-Handler geladen oder �ber die FunctionCollection zum Speichern erzeugt
 *
 */
public class WeatherData {
	/*Klassenvariablen*/
	private CityInformation cityInformation;
	private int datum;
	private int zeit;
	private Double temperaturMin;
	private Double temperaturMax;
//	private int windRichtung;
//	private int windStaerke;
	private int wetterCode;
	
	/*Klassenkonstanten*/
	private static final String TAG = "WeatherData-Objekt";
	public static final Double TEMPERATURE_NOT_SET = 99999.99;
	
	/*Konstruktoren*/
	public WeatherData(){
		
	}
	public WeatherData(CityInformation city){
		this.cityInformation = city;
	}
	
	public WeatherData(CityInformation city, Double tempMax, Double tempMin, int windRichtung, int windStaerke, int wetterCode){
		this.cityInformation = city;
		this.temperaturMax = tempMax;
		this.temperaturMin = tempMin;
	//	this.windRichtung = windRichtung;
	//	this.windStaerke = windStaerke;
		this.wetterCode = wetterCode;
	}
	/*Public Deklarationen*/
	public void setTime(String timeString){
		/*Erhält den Zeitwert als String in Form z.B. 17:00*/
		int strlen = timeString.length();
		if (strlen < 4 || strlen > 5){
			if (FunctionCollection.s_getDebugState()){
				Log.d(TAG, "Zeitwertangabe ungültig: "+timeString);
			}
		}
		else {
			//L�nge ist OK
			int pos = this.strpos(timeString, ":");
			
			if (pos > -1){
				// : wurde gefunden
				
				String timeChars = timeString.substring(0, pos);
				int timeInt = 0;
				timeInt = Integer.parseInt(timeChars);
				timeInt *= 100;
				
				timeChars = timeString.substring(pos+1);
				timeInt += Integer.parseInt(timeChars);
				
				this.zeit = timeInt;
				
			}
			else if (FunctionCollection.s_getDebugState()){
				Log.d(TAG, "Zeitwertangabe ungültig: "+timeString);
			}
		}
	}
	public void setTime(int timeInt){
		/*Erhält den Zeitwert als Integer in Form z.B. 1700*/
		this.zeit = timeInt;
	}
	public void setTemperatures(Double temp1){
		this.temperaturMax = temp1;
		this.temperaturMin = TEMPERATURE_NOT_SET;
	}
	public int getTime(){
		return this.zeit;
	}
	public void setTemperatures(Double temp1, Double temp2){
		if (temp1 > temp2){
			Double tmp = temp2;
			temp2 = temp1;
			temp1 = tmp;
		}
		
		this.temperaturMin = temp1;
		this.temperaturMax = temp2;
	}
	public Double getTemperatureMax(){
		return this.temperaturMax;
	}
	public Double getTemperaturMin(){
		if (this.temperaturMin == TEMPERATURE_NOT_SET)
			return temperaturMax;
		return temperaturMin;
	}
	public Double getTemperatureSpan(){
		Double diff = 0.0;
		if (this.temperaturMin == TEMPERATURE_NOT_SET){
			diff = 0.0;
		}
		else {
			diff = this.temperaturMax - this.temperaturMin;
		}
		
		return diff;
	}
	public void setCityInformation(CityInformation city){
		this.cityInformation = city;
	}
	public String getCityCode(){
		return this.cityInformation.getCityCode();
	}
	public CityInformation getCityInformation(){
		return this.cityInformation;
	}
	/*public void setWindRichtung(int degree){
		if (degree > 360 || degree < 0)
			degree = degree%361;
		this.windRichtung = degree;
	}
	public int getWindRichtung(){
		return this.windRichtung;
	}
	public void setWindStaerke(int windStaerke){
		this.windStaerke = windStaerke;
	}
	public int getWindStaerke(){
		return this.windStaerke;
	}      */
	public int getDate(){
		return this.datum;
	}
	/*strpos findet eine str in einem anderen str und gibt deren Startposition zurück. Wenn nicht vorhanden gibt die Methode -1 zur�ck*/
	public int strpos (String haystack, String needle, int offset) {
		int i = haystack.indexOf(needle, offset); 
		return i;
	}
	public int strpos (String haystack, String needle) {
		int i = haystack.indexOf(needle, 0); 
		return i;
	}
	
	/*Private Deklarationen*/
	/*Protected Deklarationen*/
}
