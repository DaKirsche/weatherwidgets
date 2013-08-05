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

    /**
     * Liefert den Unterschiedswert zwischen der, in dieser Collection enthaltenen Minimalen und Maximalen Temperatur.
     * @return int Temperaturdifferenz
     */
	public int getTemperatureSpan(){
        return this.maxTemp - this.minTemp;
    }

    /**
     * Niedrigste Temperatur in dieser Collection
     * @return int Minimaltemperatur
     */
    public int getMinTemp(){
        return this.minTemp;
    }

    /**
     * Höchste Temperatur in dieser Collection
     * @return int Maximaltemperatur
     */
    public int getMaxTemp(){
        return this.maxTemp;
    }
    /**
     * Prüft, ob die Iterationsliste der WeatherDataCollection [ArrayList<WeatherData>]
     * noch ein Objekt nach der aktuellen Zeigerposition enthält.
     * @return Bool ob noch ein weiterer Datensatz existiert
     */
	public boolean hasNext(){
		boolean hasNextItem = false;
		if (this.iteratorPosition < (this.getSize() - 1)) hasNextItem = true;
		return hasNextItem;
	}    /**
     * Liefert das, der aktuellen Zeigerposition folgende WeatherData-Objekt zurück
     * @return WeatherData-Objekt
     */
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
    /**
     * Setzt den Zeiger auf den ersten Datensatz in der ArrayList und liefert diesen zurück
     * @return WeatherData-Objekt
     */
	public WeatherData getFirst(){
		if (this.getSize() > 0){
			this.iteratorPosition = 0;
			return this.datensaetze.get(this.iteratorPosition);
		}
		else return null;
	}
    /**
     * Liefert die Größe der ArrayList bzw. die Anzahl der enthöltenen Datensätze zurück
     * @return int Größe der ArrayList
     */
	public int getSize(){
		return this.datensaetze.size();
	}
	/*Private Deklarationen*/
}
