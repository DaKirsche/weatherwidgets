package eu.dakirsche.weatherwidgets;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * Diese Klasse erzeugt eine Sequenz von Wetterdaten, durch die iteriert werden kann.
 * Wird vom DB-Handler mit Daten gefüllt und von der Statistik-Activity ausgewertet zu einem Graphen
 *
 */
public class WeatherDataCollection extends WeatherData {

	/*Klassenvariablen*/
	private ArrayList<WeatherData> datensaetze;
	private int iteratorPosition = 0;  
	
	/*Klassenkonstanten*/
	
	/*Konstruktoren*/
	public WeatherDataCollection(){
        this.datensaetze = new ArrayList<WeatherData>();
    }
	
	/*Public Deklarationen*/
	public int addItem(WeatherData importableWeatherData){
		/*Fügt einen Datensatz vom Typ WeatherData in die Collection ein und liefert den Indexwert der Position zurück*/
		if (this.datensaetze.add(importableWeatherData)){
			int currentPos = this.getSize();
			currentPos--;
			return currentPos;
		}
		else return -1;
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
