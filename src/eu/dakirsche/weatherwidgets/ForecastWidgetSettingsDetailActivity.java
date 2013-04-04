package eu.dakirsche.weatherwidgets;

/**
 * Erweiterte Activityklasse der WidgetSettingsDetailActivity, damit  eine eindeutige Zuordnung des WidgetTyps möglich ist.
 */
public class ForecastWidgetSettingsDetailActivity extends WidgetSettingsDetailActivity {
    /**
     * Gibt die Konstantenkennung des Widgets zurück
     * @return int Konstante aus CustomWidgetProvider zur Identifikation des Widgettyps
     */
    @Override
    protected int getWidgetType(){
        return CustomWidgetProvider.WIDGET_TYPE_FORECAST;
    }
}