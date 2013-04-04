package eu.dakirsche.weatherwidgets;

/**
 * Created with IntelliJ IDEA.
 * User: Cherry
 * Date: 04.04.13
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class WidgetInformation {
    /*Klassenvariablen*/
    private int widgetType;
    private int widgetId;
    private String cityCode;
    private CityInformation cityInformation = null;

    public WidgetInformation(int widgetId, int widgetType, String cityCode){
        this.widgetId = widgetId;
        this.widgetType = widgetType;
        this.cityCode = cityCode;
    }

    public WidgetInformation(int widgetId, int widgetType, CityInformation city){
        this.widgetId = widgetId;
        this.widgetType = widgetType;
        this.cityCode = city.getCityCode();
        this.cityInformation = city;
    }

    public int getWidgetId(){
        return this.widgetId;
    }
    public int getWidgetType(){
        return this.widgetType;
    }
    public String getCityCode(){
        return this.cityCode;
    }
    public CityInformation getCityInformation(){
        if (this.cityInformation != null) return this.cityInformation;
        return null;
    }

}
