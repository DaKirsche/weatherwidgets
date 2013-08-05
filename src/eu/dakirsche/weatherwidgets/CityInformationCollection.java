package eu.dakirsche.weatherwidgets;

/**
* 
* Diese Klasse erzeugt eine Sequenz von Wetterdaten, durch die iteriert werden kann.
* Wird vom DB-Handler mit Daten gefüllt und von der Statistik-Activity ausgewertet zu einem Graphen
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

		/*Konstruktor*/
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

     /**
      * Prüft, ob die Iterationsliste der CityInformationCollection [ArrayList<CityInformation>]
      * noch ein Objekt nach der aktuellen Zeigerposition enthält.
      * @return Bool ob noch ein weiterer Datensatz existiert
      */
		public boolean hasNext(){
				boolean hasNextItem = false;
				if (this.iteratorPosition < (this.getSize() - 1)) hasNextItem = true;
				return hasNextItem;
			}

     /**
      * Liefert das, der aktuellen Zeigerposition folgende CityInformation-Objekt zurück
      * @return CityInformation-Objekt
      */
		public CityInformation getNext(){
				if (this.hasNext()){
						this.iteratorPosition++;
						return this.datensaetze.get(this.iteratorPosition);
					}
				else return null;
			}

     /**
      * Setzt den Zeiger auf den ersten Datensatz in der ArrayList und liefert diesen zurück
      * @return CityInformation-Objekt
      */
		public CityInformation getFirst(){
				if (this.getSize() > 0){
						this.iteratorPosition = 0;
						return this.datensaetze.get(this.iteratorPosition);
					}
				else return null;
			}

     /**
      * Liefert das CityInformation-Objekt zurück, welches an der übergebenen Position in der ArrayList zu finden ist oder NULL
      * @param position int Position des gesuchten Objekt in der ArrayList
      * @return CityInformation-Objekt
      */
        public CityInformation getItem(int position){
            int size = this.getSize();
            if (FunctionCollection.s_getDebugState())
                Log.d("CICollection", "Angefragter Datensatz: " + (position+1) + " von " + this.getSize());
            if ( size >= 0 && size > position){
                return this.datensaetze.get(position);
            }
            else return null;
        }

     /**
      * Liefert die Größe der ArrayList bzw. die Anzahl der enthöltenen Datensätze zurück
      * @return int Größe der ArrayList
      */
		public int getSize(){
				int size = 0;
				if (this.datensaetze != null && !this.datensaetze.isEmpty())
					size = this.datensaetze.size();
				
				return size;
			}
	}
