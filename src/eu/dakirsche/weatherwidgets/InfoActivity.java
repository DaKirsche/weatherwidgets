package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Diese Activity stellt Informationen zur App und zu den Entwicklern bereit und ggf. eine kleine Hilfe.
 * Wenn umgesetzt wird ein Link zur Webseite des Projekts implementiert
 * */
public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		/*Infotexte f�llen*/
		
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		String appVersion;
		/*Resolving App Version*/
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion = pInfo.versionName;
		} catch (NameNotFoundException e) {
			appVersion = "";
		}
		
		((TextView) findViewById(R.id.textView_info_apiv)).setText(currentApiVersion+"");
		((TextView) findViewById(R.id.textView_info_appv)).setText(appVersion);
		((TextView) findViewById(R.id.textView_info_developer)).setText(getString(R.string.info_developers));
		
		/*Link zur Wetter.com Seite implementieren*/
		((ImageView) findViewById(R.id.imageView_info_poweredby)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Internetadresse wetter.com aufrufen
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wetter.com"));
				startActivity(browser);
			}
		});
	}

}
