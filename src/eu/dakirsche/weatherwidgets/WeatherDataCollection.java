package eu.dakirsche.weatherwidgets;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * Diese Klasse erzeugt eine Sequenz von Wetterdaten, durch die iteriert werden kann.
 * Wird vom DB-Handler mit Daten gefüllt und von der Statistik-Activity ausgewertet zu einem Graphen
 *
 */
public class WeatherDataCollection {

	/*Klassenvariablen*/
	private ArrayList<WeatherData> datensaetze;
	private int iteratorPosition = 0;

    private int minTemp = 9999;
    private int maxTemp = -9999;
	
	/*Klassenkonstanten*/
	
	/*Konstruktoren*/
	public WeatherDataCollection(){
        this.datensaetze = new ArrayList<WeatherData>();
    }
	
	/*Public Deklarationen*/
	public int addItem(WeatherData importableWeatherData){
		/*Fügt einen Datensatz vom Typ WeatherData in die Collection ein und liefert den Indexwert der Position zurück*/
		if (this.datensaetze.add(importableWeatherData)){
            int min = importableWeatherData.getTemperaturMinInt();
            int max = importableWeatherData.getTemperatureMaxInt();
            if (min < this.minTemp) this.minTemp = min;
            if (max > this.maxTemp) this.maxTemp = max;
			int currentPos = this.getSize();
			currentPos--;
			return currentPos;
		}
		else return -1;
	}
	public int getTemperatureSpan(){
        return this.maxTemp - this.minTemp;
    }
    public int getMinTemp(){
        return this.minTemp;
    }
    public int getMaxTemp(){
        return this.maxTemp;
    }
	public boolean hasNext(){
		boolean hasNextItem = false;
		if (this.iteratorPosition < (this.getSize() - 1)) hasNextItem = true;
		return hasNextItem;
	}
	public WeatherData getNext(){
		if (this.hasNext()){
			this.iteratorPosition++;
			return this.datensaetze.get(this.iteratorPosition);
		}
		else return null;
	}
    public WeatherData getItemAtPos(int pos){
        if (pos < 0) pos = 0;
        if (pos >= this.getSize()) pos = this.getSize() - 1;

        return this.datensaetze.get(pos);
    }
	public WeatherData getFirst(){
		if (this.getSize() > 0){
			this.iteratorPosition = 0;
			return this.datensaetze.get(this.iteratorPosition);
		}
		else return null;
	}
	
	public int getSize(){
		return this.datensaetze.size();
	}
	/*Private Deklarationen*/
}
