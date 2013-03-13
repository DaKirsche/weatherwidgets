package eu.dakirsche.weatherwidgets;

public class CityInformation
{
/*Klassenvariablen*/
	private String cityCode;
	private String cityName;
	private String zipCode;
	private String landName;
	private String landCode;
	
	/*Optional zusätzliche Angaben zum Widget*/
	private int widgetType;
	private int widgetId;
	
	/*Public Deklarationen*/
	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}
	public String getCityCode(){
		return this.cityCode;
	}
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	public boolean hasCityCode(){
		return(this.cityCode != null && this.cityCode.length() > 5);
	}
	public String getCityName(){
		return this.cityName;
	}
	public void setZipCode(String zipCode){
		this.zipCode = zipCode;
	}
	public String getZipCode(){
		return this.zipCode;
	}
	public void setLand(String landCode, String landName){
		this.landCode = landCode;
		this.landName = landName;
	}
	public String getLandName(){
		return this.landName;
	}
	public String getLandCode(){
		return this.landCode;
	}
	
	/*Für die optionalen Informationen*/
	
	public void setWidget(int widgetType, int widgetId){
		this.widgetType = widgetType;
		this.widgetId = widgetId;
	}
	public int getWidgetId(){
		return this.widgetId;
	}
	public int getWidgetType(){
		return this.widgetType;
	}
}
