package eu.dakirsche.weatherwidgets;

import android.os.*;
import android.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import java.io.*;

/**
 * Asynchroner Task für die Herstellung des Kontakt mit der API
 */
public class WeatherApiAsyncTask extends AsyncTask<String, String, String>
{
	/*Klassenkonstanten*/
	private static final String TAG = "WeatherApiAsyncTask";
	
	/*Klassenvariablen*/
	private CallbackInterface cbI;
	private boolean hasCallbackFunction = false;
	public boolean ready = false;
	public String resultStr = "";
	
	/*Overridemethoden*/
	@Override
	protected String doInBackground (String... uri){
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		String resultString = null;
		
		try{
			httpResponse = httpClient.execute(new HttpGet(uri[0])); 
			StatusLine statusLine = httpResponse.getStatusLine();
			Log.v(TAG, "Status: " + statusLine.getStatusCode());
			if (statusLine.getStatusCode() == HttpStatus.SC_OK){
				Log.v(TAG, "Verarbeite Daten...");
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(outputStream);
				outputStream.close();
				resultString = outputStream.toString();
                this.resultStr = resultString;
               // if (FunctionCollection.s_getDebugState())
				//    Log.d(TAG, "Daten: " + resultString);
                Log.v(TAG, "Daten empfangen!");
			}
			else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
		catch (ClientProtocolException e){
			Log.e(TAG, "URL konnte nicht geöffnet werden!", e);
			this.ready = true;
		}
		catch (IOException e){
			Log.e(TAG, "Fehler beim Datenempfang!", e);
			this.ready = true;
		}
		
		/*Die empfangene XML Struktur zurückgeben*/
		return resultString;
	}
	
	@Override
	protected void onPostExecute(String result){
		super.onPostExecute(result);
		if (this.hasCallbackFunction)
			this.cbI.callback(result);
		this.ready = true;
		this.resultStr = result;
	}
	
	/*Public Deklarationen*/
	public void registerCallback(CallbackInterface cb){
		this.hasCallbackFunction = true;
		this.cbI = cb;
	}
	public void removeCallback(){
		this.cbI = null;
		this.hasCallbackFunction = false;
	}
}
