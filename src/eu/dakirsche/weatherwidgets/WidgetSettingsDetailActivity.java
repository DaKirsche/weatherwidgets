package eu.dakirsche.weatherwidgets;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
/**
 * Diese Activity ermöglicht es dem Nutzer für einzelne Widgets spezielle Einstellungen zu treffen (z.B. verwendeter CityCode)
 * */
public class WidgetSettingsDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_settings_detail);
	}


}
