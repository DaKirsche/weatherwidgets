package eu.dakirsche.weatherwidgets;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class FunctionCollection {
	/*Klassenvariablen*/
	private Context context;
	
	/*Klassenkonstanten*/
	private static final String TAG = "FunktionsHandler";
	private static final boolean DEBUGMODEENABLED = true;
	private static final String APIKEY = "2e4b4d4897636d50a75b1ab37670bc03";
	private static final String APINAME = "weatherwidgetproject";
	private static final String APIFORECASTURI = "http://api.wetter.com/forecast/weather/";
	private static final String APISEARCHURI = "http://api.wetter.com/location/index/";
	private static final String APITESTPATH = "http://api.wetter.com/forecast/weather/city/DE0005862/project/weatherwidgetproject/cs/9e0fff805c222140d76835f68bd55bbe";
	
	/*Konstruktoren*/
	public FunctionCollection(Context context){
		this.context = context;
	}
	//public FunctionCollection(){}
	
	/*Public Deklarationen*/
	public boolean getDebugState(){
		return DEBUGMODEENABLED;
	}
	public static boolean s_getDebugState(){
		return DEBUGMODEENABLED;
	}
	public int getScreenWidth(){
		return 480;
	}
	public int getScreenHeight(){
		return 800;
	}
	public DisplayMetrics getMetrics(){
		DisplayMetrics metrics = new DisplayMetrics(); 
		((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		return metrics;
	}
	public String getMd5Hash(String plainCityCodeOrSearchString){
		String plainText =  APINAME + APIKEY + plainCityCodeOrSearchString;
		return this.md5(plainText);
	}  
	public String getApiCompatibleUri(CityInformation city){
		String cityCode = city.getCityCode();
		String uri = null;
		if (!city.hasCityCode()){
			cityCode = this.resolveCityCode(city);
			
			if (cityCode != null){
				//eindeutigen cityCode gefunden
				city.setCityCode(cityCode);
			}
		}
		
		if (city.hasCityCode()){
			String cs = this.getMd5Hash(cityCode);
			uri = APIFORECASTURI + "city/" + cityCode + "/project/" + APINAME + "/cs/" + cs;
		}
		return uri;
	}
	public String getApiCompatibleSearchUri(String searchStr){
			String cs = this.getMd5Hash(searchStr);
			String uri = APISEARCHURI + "search/" + searchStr + "/project/" + APINAME + "/cs/" + cs;
		return uri;
	}
	public String resolveCityCode(CityInformation city){
		
		return null;
	}
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
	public CityInformation getCityInformationByCityCode(String cityCode){
		CityInformation city;
		WeatherDataOpenHelper wdoh = new WeatherDataOpenHelper(this.context);
		city = wdoh.loadCityInformation(cityCode);
		wdoh.close();
		return city;
	}
	
	/*Private Deklarationen*/
	public String fetchDataFromApi(String uri){
		if (!this.isInternetAvaiable()){ 
			Log.i(TAG, "Internet ist nicht verfügbar!");
			CustomImageToast.makeImageToast((Activity)this.context, R.drawable.icon_failure, R.string.error_no_internet, Toast.LENGTH_LONG).show();
			return "";
		}
		else Log.i(TAG, "Internet ist verfügbar");

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
		apiTask.execute(uri);
		
		while (!apiTask.ready){};
		
		return apiTask.resultStr;
	}

	private String md5(String plainText){
		return this.md5(plainText, false);
	}
	public boolean isInternetAvaiable(){
		ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
			return false;
	}
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
