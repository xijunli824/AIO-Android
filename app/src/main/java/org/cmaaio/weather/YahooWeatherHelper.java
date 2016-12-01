/******************************************************************************
 * Class       : YahooWeatherHelper.java  				   		        	  *
 * Parser helper for Yahoo                                                    *
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

import org.cmaaio.activity.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import android.util.Log;

/*******************************************************************************
 * This class for provider list country support For performance issue it is
 * better to prepare WOEID by country and city
 * 
 * Alternative for dynamic solution is use yahoo API for get country then get
 * city then get WOEID http://developer.yahoo.com/geo/geoplanet/ Ref:
 * http://where.yahooapis.com/v1/countries?appid=[yourappidhere]
 * 
 * @author DatNQ
 * 
 ******************************************************************************/
public class YahooWeatherHelper {
	/** For debugging */
	private static final String TAG = "YahooWeatherHelper";

	/** Yahoo attribute */
	private static final String PARAM_YAHOO_LOCATION = "yweather:location";
	private static final String PARAM_YAHOO_UNIT = "yweather:units";
	private static final String PARAM_YAHOO_ATMOSPHERE = "yweather:atmosphere";
	private static final String PARAM_YAHOO_CONDITION = "yweather:condition";
	private static final String PARAM_YAHOO_VALUE = "yweather:wind";

	/** Attribute city */
	private static final String ATT_YAHOO_CITY = "city";
	private static final String ATT_YAHOO_COUNTRY = "country";
	private static final String ATT_YAHOO_TEMP_UNIT = "temperature";
	private static final String ATT_YAHOO_HUMIDITY = "humidity";
	private static final String ATT_YAHOO_TEXT = "text";
	private static final String ATT_YAHOO_CODE = "code";
	private static final String ATT_YAHOO_DATE = "date";
	private static final String ATT_YAHOO_TEMP = "chill";
	private static final String ATT_YAHOO_VISI = "visibility";

	/** Image array */
	public static final int[][] m_ImageArr = { { R.drawable.a0, 0 }, { R.drawable.a2, 1 }, { R.drawable.a2, 2 }, { R.drawable.a2, 3 }, { R.drawable.a2, 4 }, { R.drawable.a5, 5 },
			{ R.drawable.a5, 6 }, { R.drawable.a5, 7 }, { R.drawable.a8, 8 }, { R.drawable.a9, 9 }, { R.drawable.a9, 10 }, { R.drawable.a8, 11 }, { R.drawable.a8, 12 }, { R.drawable.a13, 13 },
			{ R.drawable.a13, 14 }, { R.drawable.a13, 15 }, { R.drawable.a13, 16 }, { R.drawable.a19, 17 }, { R.drawable.a19, 18 }, { R.drawable.a19, 19 }, { R.drawable.a19, 20 },
			{ R.drawable.a19, 21 }, { R.drawable.a19, 22 }, { R.drawable.a19, 23 }, { R.drawable.a24, 24 }, { R.drawable.a25, 25 }, { R.drawable.a26, 26 }, { R.drawable.a27, 27 },
			{ R.drawable.a28, 28 }, { R.drawable.a29, 29 }, { R.drawable.a30, 30 }, { R.drawable.a31, 31 }, { R.drawable.a32, 32 }, { R.drawable.a33, 33 }, { R.drawable.a34, 34 },
			{ R.drawable.a35, 35 }, { R.drawable.a36, 36 }, { R.drawable.a2, 37 }, { R.drawable.a2, 38 }, { R.drawable.a2, 39 }, { R.drawable.a2, 40 }, { R.drawable.a41, 41 }, { R.drawable.a41, 42 },
			{ R.drawable.a41, 43 }, { R.drawable.a44, 44 }, { R.drawable.a45, 45 }, { R.drawable.a46, 46 }, { R.drawable.a46, 47 }, { R.drawable.a3200, 3200 }, };

	/***************************************************************************
	 * Parser yahoo weather
	 * 
	 * @param docWeather
	 * @return
	 * @date May 9, 2011
	 * @time 2:08:58 AM
	 * @author DatNQ
	 **************************************************************************/
	public static WeatherInfo parserYahooWeatherInfo(Document docWeather) {
		if (docWeather == null) {
			Log.e(TAG, "Invalid doc weatehr");
			return null;
		}

		String strCity = null;
		String strCountry = null;
		String strTempUnit = null;
		String strTempValue = null;
		String strHumidity = null;
		String strText = null;
		;
		String strCode = null;
		;
		String strDate = null;
		;
		String strVisi = null;
		try {
			Element root = docWeather.getDocumentElement();
			root.normalize();

			NamedNodeMap locationNode = root.getElementsByTagName(PARAM_YAHOO_LOCATION).item(0).getAttributes();

			if (locationNode != null) {
				strCity = locationNode.getNamedItem(ATT_YAHOO_CITY).getNodeValue();
				strCountry = locationNode.getNamedItem(ATT_YAHOO_COUNTRY).getNodeValue();
			}

			NamedNodeMap unitNode = root.getElementsByTagName(PARAM_YAHOO_UNIT).item(0).getAttributes();
			if (unitNode != null) {
				strTempUnit = unitNode.getNamedItem(ATT_YAHOO_TEMP_UNIT).getNodeValue();
			}

			NamedNodeMap atmosNode = root.getElementsByTagName(PARAM_YAHOO_ATMOSPHERE).item(0).getAttributes();
			if (atmosNode != null) {
				strHumidity = atmosNode.getNamedItem(ATT_YAHOO_HUMIDITY).getNodeValue();
				strVisi = atmosNode.getNamedItem(ATT_YAHOO_VISI).getNodeValue();
			}

			NamedNodeMap conditionNode = root.getElementsByTagName(PARAM_YAHOO_CONDITION).item(0).getAttributes();
			if (conditionNode != null) {
				strText = conditionNode.getNamedItem(ATT_YAHOO_TEXT).getNodeValue();
				strCode = conditionNode.getNamedItem(ATT_YAHOO_CODE).getNodeValue();
				strDate = conditionNode.getNamedItem(ATT_YAHOO_DATE).getNodeValue();
			}

			NamedNodeMap temNode = root.getElementsByTagName(PARAM_YAHOO_VALUE).item(0).getAttributes();
			if (temNode != null) {
				strTempValue = temNode.getNamedItem(ATT_YAHOO_TEMP).getNodeValue();
			}
		} catch (Exception e) {
			Log.e(TAG, "Something wroing with parser data");
			return null;
		}

		/* Weather info */
		WeatherInfo yahooWeatherInfo = new WeatherInfo(strCity, strCountry, strTempValue, strHumidity, strText, strCode, strDate, strTempUnit, strVisi);
		return yahooWeatherInfo;
	}

	/**
	 * 根据code取中文字符
	 * 
	 * @param code
	 * @return
	 */
	public static String getStringByCode(int code) {
		switch (code) {
		case 0:
			return "龙卷风";
		case 1:
			return "热带风暴";
		case 2:
			return "暴风";
		case 3:
			return "大雷雨";
		case 4:
			return "雷阵雨";
		case 5:
			return "雨夹雪";
		case 6:
			return "雨夹雹";
		case 7:
			return "雪夹雹";
		case 8:
			return "冻雾雨";
		case 9:
			return "细雨";
		case 10:
			return "冻雨";
		case 11:
			return "阵雨";
		case 12:
			return "阵雨";
		case 13:
			return "阵雪";
		case 14:
			return "小阵雪";
		case 15:
			return "高吹雪";
		case 16:
			return "雪";
		case 17:
			return "冰雹";
		case 18:
			return "雨淞";
		case 19:
			return "粉尘";
		case 20:
			return "雾";
		case 21:
			return "薄雾";
		case 22:
			return "烟雾";
		case 23:
			return "大风";
		case 24:
			return "风";
		case 25:
			return "冷";
		case 26:
			return "阴";
		case 27:
			return "多云";
		case 28:
			return "多云";
		case 29:
			return "局部多云";
		case 30:
			return "局部多云";
		case 31:
			return "晴";
		case 32:
			return "晴";
		case 33:
			return "转晴";
		case 34:
			return "转晴";
		case 35:
			return "雨夹冰雹";
		case 36:
			return "热";
		case 37:
			return "局部雷雨";
		case 38:
			return "偶有雷雨";
		case 39:
			return "偶有雷雨";
		case 40:
			return "偶有阵雨";
		case 41:
			return "大雪";
		case 42:
			return "零星阵雪";
		case 43:
			return "大雪";
		case 44:
			return "局部多云";
		case 45:
			return "雷阵雨";
		case 46:
			return "阵雪";
		case 47:
			return "局部雷阵雨";
		default:
			return "水深火热";
		}
	}
}
