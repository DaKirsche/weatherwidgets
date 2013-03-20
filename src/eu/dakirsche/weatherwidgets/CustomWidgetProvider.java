package eu.dakirsche.weatherwidgets;

import android.appwidget.AppWidgetProvider;

public abstract class CustomWidgetProvider extends AppWidgetProvider{
/*Konstantendeklaration*/
	//Die drei verf�gbaren Widgets
	public static final int WIDGET_TYPE_SMALL = 1;
	public static final int WIDGET_TYPE_LARGE = 2;
	public static final int WIDGET_TYPE_FORECAST = 3;
	
/*Klassenvariablen*/
	//Information �ber den aktuellen WidgetType
	protected int widgetType;

	
	/**
	 * Legt den eigenen WidgetType anhand der Konstanten WIDGET_TYPE fest
	 */
	protected abstract void setWidgetType();
	
	public CustomWidgetProvider(){
		super();
		this.setWidgetType();
	}
}