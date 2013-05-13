package eu.dakirsche.weatherwidgets;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
 * Klasse zum Sammeln allgemeiner Methoden, die in mehreren Activities oder Objekten verwendet werden
 */
public class FunctionCollection {
	/*Klassenvariablen*/
	private Context context;
	
	/*Klassenkonstanten*/
	private static final String TAG = "FunktionsHandler";
	private static final boolean DEBUGMODEENABLED = false;                                      //Globaler Key zum Minimieren des Logging außerhalb der Entwicklungszeit
	private static final String APIKEY = "2e4b4d4897636d50a75b1ab37670bc03";                    //Sicherheitskey  der API
	private static final String APINAME = "weatherwidgetproject";                               //API Projektname
	private static final String APIFORECASTURI = "http://api.wetter.com/forecast/weather/";     //Vorhersage URL der API
	private static final String APISEARCHURI = "http://api.wetter.com/location/index/";         //Such URL der API

	/*Konstruktoren*/
	public FunctionCollection(Context context){
		this.context = context;
	}
	
	/*Public Deklarationen*/
	public boolean getDebugState(){
		return DEBUGMODEENABLED;
	}
	public static boolean s_getDebugState(){
		return DEBUGMODEENABLED;
	}

    /**
     * Liefert einen API Konformen MD5 Hash zurück
     * @param plainCityCodeOrSearchString  Citycode oder Suchbegriff
     * @return API konformer MD5-Hask
     */
	public String getMd5Hash(String plainCityCodeOrSearchString){
		String plainText =  APINAME + APIKEY + plainCityCodeOrSearchString;
		return this.md5(plainText);
	}

    /**
     * Liefert eine API konforme URL für den Abruf von Vorhersageinformationen anhand einer City zurück
     * @param city CityInformation für die Abfrage
     * @return Vollwertige URL der Vorhersage als String
     */
	public String getApiCompatibleUri(CityInformation city){
		String cityCode = city.getCityCode();
		String uri = null;
		
		if (city.hasCityCode()){
			String cs = this.getMd5Hash(cityCode);
            cityCode =  stripSpaces(cityCode);
			uri = APIFORECASTURI + "city/" + cityCode + "/project/" + APINAME + "/cs/" + cs;
		}
		return uri;
	}

    /**
     * Liefert eine API konforme URL für die Suchabfrage
     * @param searchStr Der zu suchende Begriff
     * @return Vollwertige URL der Scuhabfrage als String
     */
	public String getApiCompatibleSearchUri(String searchStr){
        String cs = this.getMd5Hash(searchStr);
        searchStr =  stripSpaces(searchStr);
        String uri = APISEARCHURI + "search/" + searchStr + "/project/" + APINAME + "/cs/" + cs;
		return uri;
	}

    /**
     * Ersetzt in einem String Leerzeichen durch ein + Zeichen
     * @param strIn Eingangsstring
     * @return Ausgangsstring
     */
    private String stripSpaces(String strIn){
        return strIn.replace(" ", "+");
    }

    /**
     * Methode, um alle verwendeten CityInformations zu synchronisieren und aktuelle WeatherData zu sammeln
     *
     * ### DERZEIT UNUSED (siehe Dokumenation) ###
     */
	public void synchronizeData(){
		WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(this.context);
		CityInformationCollection cities = wdoh.getActiveCityCodesForSync();
		CityInformation city = cities.getFirst();
		if (cities.getSize() > 0){
			this.fetchDataFromApi(this.getApiCompatibleUri(city));
			while (cities.hasNext()){
				city = cities.getNext();
				this.fetchDataFromApi(this.getApiCompatibleUri(city));
			}
		}
	}

    /**
     * Stellt Kontakt zur API her durch Aufruf eines asynchronen Tasks mit ResponseHandler
     * @param uri Url für den API Aufruf
     * @return Liefert den XML Result zurück
     */
	public String fetchDataFromApi(String uri){
		if (!this.isInternetAvaiable()){ 
			Log.i(TAG, "Internet ist nicht verfügbar!");
			CustomImageToast.makeImageToast((Activity)this.context, R.drawable.icon_failure, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            return "Internet ist nicht verfügbar!";
		}
		else Log.i(TAG, "Internet ist verfügbar!");

		WeatherApiAsyncTask apiTask = new WeatherApiAsyncTask();
		/*Registriere eine Callback Funktion für den ApiSyncTask*/
		apiTask.registerCallback(new CallbackInterface() {
			public void callback(String result){
				if (FunctionCollection.s_getDebugState()){
					Log.d(TAG, "Callback ausgeführt. Erhaltene Response:");
					Log.d(TAG, result);
				}
			}
		});
		if (DEBUGMODEENABLED)
			Log.d(TAG, "Angefragte URL: " + uri);

       String resultStr = "";
       try {
		    resultStr = apiTask.execute(uri).get();
        }
        catch (InterruptedException e) {}
        catch (ExecutionException e) {}

		if (resultStr.equals(""))
            resultStr = "Kein Ergebnis erhalten!";
		return resultStr;
	}

    /**
     * Aufruf der md5 Methode mit erqweiterten Parametern
     * @param plainText Klartext zum hashen
     * @return String MD5-Hash
     */
	private String md5(String plainText){
		return this.md5(plainText, false);
	}

    /**
     * Prüft ob Internet verfügbar oder Verbindung hergestellt werden kann
     * @return Boolean true (Internet verfügbar) oder false
     */
	public boolean isInternetAvaiable(){
		ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ((netInfo != null) && netInfo.isConnectedOrConnecting() && netInfo.isAvailable()) return true;
		return false;
	}

    /**
     * Erzeugt aus einem String einen MD5 Hash, wie der von der API erwartet wird. Kann optional auf 32 Zeichen aufstocken
     * @param plainText Klartext String zum hashen
     * @param getFull32CharsLength Boolean ob auf 32 Zeichen aufgefüllt werden soll
     * @return der MD5 Hash als String
     */
	private String md5(String plainText, boolean getFull32CharsLength){
		String hashText;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plainText.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashText = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashText.length() < 32 ){
			  hashText = "0"+hashText;
			}
		} catch (NoSuchAlgorithmException e){
			Log.e(TAG, "Algorithmus nicht gefunden: MD5", e);
			hashText = null;
		}
		
		return hashText;
	}
	
	/*Protected Deklarationen*/
}
