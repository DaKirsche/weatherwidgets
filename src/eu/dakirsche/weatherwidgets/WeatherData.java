package eu.dakirsche.weatherwidgets;

import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import  java.util.Date;
import java.text.DateFormat;

/**
 * 
 * Diese Klasse beinhaltet die Daten von Wetterinformationen zu einem bestimmten Zeitpunkt.
 * Die Wetterdaten werden �ber den DB-Handler geladen oder �ber die FunctionCollection zum Speichern erzeugt
 **/
public class WeatherData {
	/*Klassenvariablen*/
	private CityInformation cityInformation;
    private String dateTimeStr = "";
	private Double temperaturMin;
	private Double temperaturMax;
	private int wetterCode;

    private int temperatureSpan = 0;
	
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

    /**
     *
     * @param dateTimeStr StringDatums- und Zeitstempel im Format yyyy-mm-dd HH:ii
     * @return Boolean ob das übergebene String dem Fromatvorgaben entspricht
     */
    public boolean setDateTimeStr(String dateTimeStr){
     //  String testPattern = "[\\d]{2,4}-[d]{2}-[d]{2}\\s[\\d]{1,2}:[\\d]{1,2}";
     //  if (dateTimeStr.matches(testPattern)){
           this.dateTimeStr = dateTimeStr;
           return true;
     //  }
     //   else return false;
    }
	public void setTemperatures(Double temp1){
		this.temperaturMax = temp1;
		this.temperaturMin = TEMPERATURE_NOT_SET;
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

    public int getTemperatureMaxInt(){
        return Integer.parseInt(""+Math.round(this.temperaturMax));
    }
    public int getTemperaturMinInt(){
        if (this.temperaturMin == TEMPERATURE_NOT_SET)
            return Integer.parseInt(""+Math.round(temperaturMax));
        return Integer.parseInt(""+Math.round(temperaturMin));
    }
    public int getTemperatureSpanInt(){
        Double diff = 0.0;
        if (this.temperaturMin == TEMPERATURE_NOT_SET){
            diff = 0.0;
        }
        else {
            diff = this.temperaturMax - this.temperaturMin;
        }

        return Integer.parseInt(""+Math.round(diff));
    }
	public void setCityInformation(CityInformation city){
        if (city != null)
		 this.cityInformation = city;
        else if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Setzen der CityInformation fehlgeschlagen. City ist null");
	}
	public String getCityCode(){
        if (this.cityInformation == null) return "";
		return this.cityInformation.getCityCode();
	}
	public CityInformation getCityInformation(){
		return this.cityInformation;
	}

    public Date getDate(){
        Date datum;
        if (this.dateTimeStr.equals("")) return new Date();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            datum = df.parse(this.dateTimeStr);
        }
        catch (ParseException e){
            Log.e(TAG, "Exception aufgetreten beim Datumparser", e);
            datum = null;
        }

        return datum;
    }
    public String getDateStr(){
        Date d = this.getDate();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.");
        return df.format(d);
    }
	
	public void setWeatherCode(int weatherCode){
		this.wetterCode = weatherCode;
	}
	
	public int getWeatherCode(){
		return this.wetterCode;
	}
    public String toString(){
        String result = "";

        result += this.getCityCode();
        result += "\n" + this.getTemperaturMin() + " - " + this.getTemperatureMax();
        result += "\n" + this.getDate();
        result += "\n" + this.getWeatherCode();

        return result;
    }
	
	/*strpos findet eine str in einem anderen str und gibt deren Startposition zurück. Wenn nicht vorhanden gibt die Methode -1 zurück*/
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
