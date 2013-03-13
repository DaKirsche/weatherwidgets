package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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
		
		/*Infotexte füllen*/
		
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		String appVersion;
		/*Resolving App Version*/
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			appVersion = "";
		}
		
		((TextView) findViewById(R.id.textView_info_apiv)).setText(currentApiVersion+"");
		((TextView) findViewById(R.id.textView_info_appv)).setText(appVersion);
		((TextView) findViewById(R.id.textView_info_developer)).setText(getString(R.string.info_developers));
	}

}
