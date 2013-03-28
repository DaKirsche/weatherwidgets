package eu.dakirsche.weatherwidgets;

/**
* 
* Diese Klasse erzeugt eine Sequenz von Wetterdaten, durch die iteriert werden kann.
* Wird vom DB-Handler mit Daten gef�llt und von der Statistik-Activity ausgewertet zu einem Graphen
*
*/
import android.util.Log;

import java.util.*;

public class CityInformationCollection
 {

		/*Klassenvariablen*/
		private ArrayList<CityInformation> datensaetze;
		private int iteratorPosition = 0;  

		/*Klassenkonstanten*/

		/*Konstruktoren*/
		public CityInformationCollection(){
            this.datensaetze = new ArrayList<CityInformation>();
		}

		/*Public Deklarationen*/
		public int addItem(CityInformation importableCityInformation){
				/*Fuegt einen Datensatz vom Typ WeatherData in die Collection ein und liefert den Indexwert der Position zurueck*/
				if (this.datensaetze.add(importableCityInformation)){
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
		public CityInformation getNext(){
				if (this.hasNext()){
						this.iteratorPosition++;
						return this.datensaetze.get(this.iteratorPosition);
					}
				else return null;
			}
		public CityInformation getFirst(){
				if (this.getSize() > 0){
						this.iteratorPosition = 0;
						return this.datensaetze.get(this.iteratorPosition);
					}
				else return null;
			}
		public CityInformation getItem(int position){
			int size = this.getSize();
            if (FunctionCollection.s_getDebugState())
                Log.d("CICollection", "Angefragter Datensatz: " + (position+1) + " von " + this.getSize());
			if ( size >= 0 && size > position){
				return this.datensaetze.get(position);
			}
			else return null;
		}
		public int getSize(){
				int size = 0;
				if (this.datensaetze != null && !this.datensaetze.isEmpty())
					size = this.datensaetze.size();
				
				return size;
			}
		/*Private Deklarationen*/
	}
