package org.cmaaio.weather;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.cmaaio.common.Constants;
import org.cmaaio.ssl.EasySSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.util.Log;

public class HttpOperator {

	public static int MAX_HTTP_THREAD_COUNT = 5;

	private static ExecutorService httpThreadPool = Executors
			.newFixedThreadPool(MAX_HTTP_THREAD_COUNT, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					AtomicInteger ints = new AtomicInteger(0);
					return new Thread(r, "httpThreadPool#"
							+ ints.getAndIncrement());
				}
			});

	public static void getYahooWeather(final String httpurl,
			final WeatherinfoCallBack cb) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				HttpGet httpGet = new HttpGet(httpurl);
				HttpClient client = new DefaultHttpClient();
				try {
					HttpResponse response = client.execute(httpGet);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String strResult = EntityUtils.toString(response
								.getEntity());
						// Log.e("httpClientGet:", strResult);
						ByteArrayInputStream bais = new ByteArrayInputStream(
								strResult.getBytes());
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						Document doc = dbf.newDocumentBuilder().parse(bais);
						WeatherInfo weather = YahooWeatherHelper
								.parserYahooWeatherInfo(doc);
						// Log.e("httpClientGet:", "" + weather.getCity());
						cb.onSuccess(weather);
					} else {
						// Log.e("httpClientGet:", "" + HttpStatus.SC_OK);
						cb.onFail("" + response.getStatusLine().getStatusCode());
					}
				} catch (Exception e) {
					cb.onFail(e.getMessage());
				}
			}
		};
		httpThreadPool.execute(runnable);
	}

	public static void getCityNameByLatlng(double lat, double lon,
			final HttpStrMsgCallBack cb) {
		final String httpurl = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ lat + "," + lon + "&language=en&sensor=true";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				HttpGet httpGet = new HttpGet(httpurl);
				HttpClient client = new DefaultHttpClient();
				try {
					HttpResponse response = client.execute(httpGet);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String strResult = EntityUtils.toString(response
								.getEntity());
						JSONObject json = new JSONObject(strResult);
						JSONArray arrayRoot = (JSONArray) json.get("results");
						if (arrayRoot.length() == 0) {
							return;
						}
						JSONObject jsonAddress = (JSONObject) arrayRoot.get(0);
						JSONArray array2 = (JSONArray) jsonAddress
								.get("address_components");
						for (int i = 0; i < array2.length(); i++) {
							JSONObject obj = (JSONObject) array2.get(i);
							JSONArray arrayObj = (JSONArray) obj.get("types");
							if ("locality".equals(arrayObj.get(0).toString())) {
								Log.d("city:", "" + obj.get("long_name"));
								cb.onSuccess(obj.get("long_name").toString());
								return;
							}
						}
						cb.onFail("can not find city in current place!");
					} else {
						cb.onFail("http reuqest failed!");
					}
				} catch (Exception e) {
					cb.onFail(e.getMessage());
				}
			}
		};
		httpThreadPool.execute(runnable);
	}

	public static void getYahooWoeidByCityName(final String cityName,
			final HttpStrMsgCallBack cb) {
		final String httpurl = Constants.KServerurl + "GetCity";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					HttpPost post = new HttpPost(httpurl);
					String params = "{\"cityName\":\"" + cityName + "\"}";
					StringEntity httpbody = new StringEntity(params, HTTP.UTF_8);
					post.setEntity(httpbody);
					post.setHeader("Content-Type", "application/json");
					HttpClient httpclient = null;
					if (httpurl.contains("https://")) {
						HttpParams par = new BasicHttpParams();
						SchemeRegistry schemeRegistry = new SchemeRegistry();
						schemeRegistry.register(new Scheme("https",
								new EasySSLSocketFactory(), 443));
						ClientConnectionManager connManager = new ThreadSafeClientConnManager(
								par, schemeRegistry);
						httpclient = new DefaultHttpClient(connManager, par);
					} else {
						httpclient = new DefaultHttpClient();
					}

					HttpResponse response = httpclient.execute(post);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String strResult = EntityUtils.toString(response
								.getEntity());
						JSONArray  jsonarray=new JSONArray(strResult);
						JSONObject json = new JSONObject(jsonarray.get(0).toString());
						String woeid = json.get("woeid").toString();
						cb.onSuccess(woeid);
						Log.d("location", woeid);
					} else {
						cb.onFail("http reuqest failed!");
					}
				} catch (Exception e) {
					cb.onFail(e.getMessage());
				}
			}
		};

		httpThreadPool.execute(runnable);
	}

}
