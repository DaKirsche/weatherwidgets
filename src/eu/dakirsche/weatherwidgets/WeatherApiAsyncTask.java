package eu.dakirsche.weatherwidgets;

import android.os.*;
import android.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import java.io.*;

public class WeatherApiAsyncTask extends AsyncTask<String, String, String>
{
	/*Klassenkonstanten*/
	private static final String TAG = "WeatherApiAsyncTask";
	
	/*Klassenvariablen*/
	private CallbackInterface cbI;
	private boolean hasCallbackFunction = false;
	
	/*Overridemethoden*/
	@Override
	protected String doInBackground (String... uri){
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		String resultString = null;
		
		try{
			httpResponse = httpClient.execute(new HttpGet(uri[0]));
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(outputStream);
				outputStream.close();
				resultString = outputStream.toString();
			}
			else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
		catch (ClientProtocolException e){
			Log.e(TAG, "URL konnte nicht geöffnet werden!", e);
		}
		catch (IOException e){
			Log.e(TAG, "Fehler beim Datenempfang!", e);
		}
		
		/*Die empfangene XML Struktur zurückgeben*/
		return resultString;
	}
	
	@Override
	protected void onPostExecute(String result){
		super.onPostExecute(result);
		if (this.hasCallbackFunction)
			this.cbI.callback(result);
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
