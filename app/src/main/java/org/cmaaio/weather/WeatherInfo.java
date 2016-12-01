/******************************************************************************
 * Class       : WeatherInfo.java						   		        	  *
 * Weather content                                                            *
 *                                                                            *
 * Version     : v1.0                                                         *
 * Date        : May 06, 2011                                                 *
 * Copyright (c)-2011 DatNQ some right reserved                               *
 * You can distribute, modify or what ever you want but WITHOUT ANY WARRANTY  *
 * Be honest by keep credit of this file                                      *
 *                                                                            *
 * If you have any concern, feel free to contact with me via email, i will    *
 * check email in free time                                                   * 
 * Email: nguyendatnq@gmail.com                                               *
 * ---------------------------------------------------------------------------*
 * Modification Logs:                                                         *
 *   KEYCHANGE  DATE          AUTHOR   DESCRIPTION                            *
 * ---------------------------------------------------------------------------*
 *    -------   May 06, 2011  DatNQ    Create new                             *
 ******************************************************************************/
package org.cmaaio.weather;


/*******************************************************************************
 * This class for content of weather data, currently i just support some
 * basic information, need to more detail, very easy just add here :)
 *
 ******************************************************************************/
public class WeatherInfo {
	/** Initialize description */
	private static final String DEFAULT_DATA = "No data";
	
	/** Celsius temperature */
	public static final int TEMPERATURE_FMT_CELSIUS = 1;
	
	/** Fahrenheit temperature */
	public static final int TEMPERATURE_FMT_FAHRENHEIT = 2;

	/** City */
	private String m_City;
	
	/** Country */
	private String m_Country;	

	/** Weather Temperature, in Celsius */
	private String m_Temperature;	
	
	/** Humidity */
	private String m_Humidity;	
	
	/** Weather Text */
	private String m_Text;

	/** Weather Wind */
	private String m_Code;
	
	/** Date */
	private String m_Date;
	
	/** Temp unit */
	private String m_TemperatureUnit;
	
	/** Visibility */
	private String m_Visi;

	
	/***************************************************************************
	 * Yahoo weather item constructor
	 * @date May 7, 2011
	 * @time 6:01:41 AM
	 * @author DatNQ
	 **************************************************************************/
	public WeatherInfo(){
		this(DEFAULT_DATA, DEFAULT_DATA, DEFAULT_DATA, DEFAULT_DATA, DEFAULT_DATA,
				DEFAULT_DATA, DEFAULT_DATA, DEFAULT_DATA, DEFAULT_DATA);
	}
	
	/***************************************************************************
	 * Constructor of weather informaiton
	 * @param strCity
	 * @param strCountry
	 * @param strTem
	 * @param strHum
	 * @param strText
	 * @param strCode
	 * @param strDate
	 * @date May 9, 2011
	 * @time 2:29:40 AM
	 * @author DatNQ
	 **************************************************************************/
	public WeatherInfo(String strCity, String strCountry, String strTem,
			String strHum, String strText, String strCode, String strDate, 
			String strTempUnit, String strVisi){
		m_City = strCity;
		m_Country = strCountry;
		m_Temperature = strTem;
		m_Humidity = strHum;
		m_Text = strText;
		m_Code = strCode;
		m_Date = strDate;
		m_TemperatureUnit = strTempUnit;
		m_Visi = strVisi;
	}
	
	/***************************************************************************
	 * Set temperature
	 * @param nValue Value
	 * @date May 7, 2011
	 * @time 6:17:54 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setTemperature(String nValue){
		m_Temperature = nValue;
	}
	
	/***************************************************************************
	 * Get temperature
	 * @return Temperature
	 * @date May 7, 2011
	 * @time 6:18:25 AM
	 * Note: Add logic to convert from C to F and vice versa
	 * @author DatNQ
	 **************************************************************************/
	public String getTemperature(int nTempFmt){
		if (nTempFmt == TEMPERATURE_FMT_CELSIUS){
			return m_Temperature;
		}
		
		return m_Temperature;
	}	
	
	/***************************************************************************
	 * Set temperature unit
	 * @param nValue Value
	 * @date May 7, 2011
	 * @time 6:17:54 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setTempUnit(String nValue){
		m_TemperatureUnit = nValue;
	}
	
	/***************************************************************************
	 * Get temperature
	 * @return Temperature
	 * @date May 7, 2011
	 * @time 6:18:25 AM
	 * Note: Add logic to convert from C to F and vice versa
	 * @author DatNQ
	 **************************************************************************/
	public String getTempUnit(){
		return m_TemperatureUnit;
	}		
	
	/***************************************************************************
	 * Set code
	 * @param nValue Value
	 * @date May 7, 2011
	 * @time 6:26:10 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setCode(String nValue){
		m_Code = nValue;
	}
	
	/***************************************************************************
	 * Get Code
	 * @return code
	 * @date May 7, 2011
	 * @time 6:26:35 AM
	 * @author DatNQ
	 **************************************************************************/
	public String getCode(){
		return m_Code;
	}	

	/***************************************************************************
	 * Get city
	 * @return
	 * @date May 7, 2011
	 * @time 10:47:40 PM
	 * @author DatNQ
	 **************************************************************************/
	public String getCity() {
		return m_City;
	}
	
	/***************************************************************************
	 * Get city
	 * @param city
	 * @date May 7, 2011
	 * @time 10:47:55 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setCity(String city) {
		m_City = city;
	}
	
	/***************************************************************************
	 * Get date
	 * @return
	 * @date May 7, 2011
	 * @time 10:48:18 PM
	 * @author DatNQ
	 **************************************************************************/
	public String getDate() {
		return m_Date;
	}

	/***************************************************************************
	 * Set region
	 * @param strValue
	 * @date May 7, 2011
	 * @time 10:48:32 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setDate(String strValue) {
		m_Date = strValue;
	}

	/***************************************************************************
	 * Get country
	 * @return
	 * @date May 7, 2011
	 * @time 10:48:45 PM
	 * @author DatNQ
	 **************************************************************************/
	public String getCountry() {
		return m_Country;
	}

	/***************************************************************************
	 * Get country
	 * @param country
	 * @date May 7, 2011
	 * @time 10:48:58 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setCountry(String country) {
		m_Country = country;
	}

	/***************************************************************************
	 * Get humidity
	 * @return
	 * @date May 7, 2011
	 * @time 10:49:42 PM
	 * @author DatNQ
	 **************************************************************************/
	public String getHumidity() {
		return m_Humidity;
	}

	/***************************************************************************
	 * Get humidity
	 * @param humidity
	 * @date May 7, 2011
	 * @time 10:49:56 PM
	 * @author DatNQ
	 **************************************************************************/
	public void setHumidity(String humidity) {
		m_Humidity = humidity;
	}

	/***************************************************************************
	 * Get text
	 * @return
	 * @date May 9, 2011
	 * @time 2:22:55 AM
	 * @author DatNQ
	 **************************************************************************/
	public String getText(){
		return m_Text;
	}
	
	/***************************************************************************
	 * Set text
	 * @param strText
	 * @date May 9, 2011
	 * @time 2:23:48 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setText(String strText){
		m_Text = strText;
	}
	
	/***************************************************************************
	 * Get visibility
	 * @return
	 * @date May 9, 2011
	 * @time 2:22:55 AM
	 * @author DatNQ
	 **************************************************************************/
	public String getVisibility(){
		return m_Visi;
	}
	
	/***************************************************************************
	 * Set visibility
	 * @param strText
	 * @date May 9, 2011
	 * @time 2:23:48 AM
	 * @author DatNQ
	 **************************************************************************/
	public void setVisibility(String strText){
		m_Visi = strText;
	}	
	
}
