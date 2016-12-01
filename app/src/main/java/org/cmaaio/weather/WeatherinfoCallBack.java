package org.cmaaio.weather;

public interface WeatherinfoCallBack {
	void onSuccess(WeatherInfo weather);
	void onFail(String msg);
}
