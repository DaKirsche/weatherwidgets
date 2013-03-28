package eu.dakirsche.weatherwidgets;

public class CityInformation
{
/*Klassenvariablen*/
	private String cityCode;
	private String cityName;
	private String zipCode;
	private String landCode;
    private String additionalLandString = null;
	
	/*Optional zus�tzliche Angaben zum Widget*/
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
    public void setLand(String landCode){
        this.landCode = landCode;
        this.additionalLandString = null;
    }
    public void setLand(String landCode, String landString){
        this.landCode = landCode;
        this.additionalLandString = landString;
    }

	public String getLandCode(){
		return this.landCode;
	}
	
	/*F�r die optionalen Informationen*/
	
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

    /**
     * Wandelt die CityInformation in einen verwertbaren String
     * @return  String CityInformation als String in Form Stadt\nPLZ, Land, Länderkürzel
     */
    public String toString(){
        String result = "";
        if (this.cityName != null)
            result += this.cityName;

        if (this.additionalLandString != null)
            result += "\n" + this.additionalLandString;
        else {
            if (this.zipCode != null)
                result += ", " + this.zipCode;
            if (this.landCode != null)
                result += " " + this.landCode;
        }
        return result;
    }
}
