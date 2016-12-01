package org.cmaaio.weather;

import java.util.Iterator;

import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.util.CommonUtil;

import android.app.Service;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GetlocationService extends Service {
	private LocationManager locationManager;
	private GpsStatus gpsstatus;
	private String currentProvider;
	private Location currentLocation;
	private boolean isDestory = false;

	public static String GETYAHOOWEATHERBROADCAST = "org.cmaaio.getyahooweatherbroadcast";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 获取到LocationManager对象
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// 根据设置的Criteria对象，获取最符合此标准的provider对象
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			currentProvider = locationManager.getProvider(
					LocationManager.GPS_PROVIDER).getName();
		} else {
			currentProvider = locationManager.getProvider(
					LocationManager.NETWORK_PROVIDER).getName();
		}

		// LocationManager.

		// 根据当前provider对象获取最后一次位置信息
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 如果位置信息为null，则请求更新位置信息
		if (currentLocation == null) {
			locationManager.requestLocationUpdates(currentProvider, 0, 0,
					locationListener);
		}
		// 增加GPS状态监听器
		// locationManager.addGpsStatusListener(gpsListener);

	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			mthread.start();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	Thread mthread = new Thread(new Runnable() {

		@Override
		public void run() {
			// 直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度
			// 每隔10秒获取一次位置信息
			while (!isDestory) {
				currentLocation = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (SharedPrefsConfig.getSharedPrefsInstance(
						GetlocationService.this).getLongitude() > 0
						&& SharedPrefsConfig.getSharedPrefsInstance(
								GetlocationService.this).getLongitude() > 0) {
					Log.d("Location", "getted lat and lon");
					locationManager.removeUpdates(locationListener);
					break;
				}
				if (currentLocation != null) {

					float lat = (float) currentLocation.getLatitude();
					float lon = (float) currentLocation.getLongitude();
//					Log.d("whz", "Latitude: " + lat);
//					Log.d("whz", "location: " + lon);

					SharedPrefsConfig.getSharedPrefsInstance(
							GetlocationService.this).setLatitude(lat);
					SharedPrefsConfig.getSharedPrefsInstance(
							GetlocationService.this).setLongitude(lon);
					// 根据经纬度取城市
					HttpOperator.getCityNameByLatlng(lat, lon,
							new HttpStrMsgCallBack() {

								@Override
								public void onSuccess(String msg) {
									String city = msg;
									Log.d("Location", "get city name:" + city);
									// 根据城市名取yahoo woeid
									HttpOperator.getYahooWoeidByCityName(city,
											new HttpStrMsgCallBack() {
												@Override
												public void onSuccess(String msg) {
													Log.d("Location", "woeid:"
															+ msg);
													SharedPrefsConfig
															.getSharedPrefsInstance(
																	GetlocationService.this)
															.setYahoowoeid(msg);
													Intent intent = new Intent();
													intent.setAction(GETYAHOOWEATHERBROADCAST);
													sendBroadcast(intent);
												}

												@Override
												public void onFail(String msg) {
													Log.e("Location",
															"getYahooWoeidByCityName failed:"
																	+ msg);
												}
											});
								}

								@Override
								public void onFail(String msg) {
									Log.e("Location",
											"getCityNameByLatlng failed:" + msg);
								}
							});

					break;
				} else {
//					Log.d("whz", "gps Latitude: " + 0);
//					Log.d("whz", "gps location: " + 0);
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Log.e("Location", e.getMessage());
				}
			}
		}
	});

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);// Service.START_STICKY
															// restart the
															// service
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeGpsStatusListener(gpsListener);
		isDestory = true;
	}

	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		// GPS状态发生变化时触发
		@Override
		public void onGpsStatusChanged(int event) {
			// 获取当前状态
			gpsstatus = locationManager.getGpsStatus(null);
			switch (event) {
			// 第一次定位时的事件
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				break;
			// 开始定位的事件
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			// 发送GPS卫星状态事件
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.d("Location", "GPS_EVENT_SATELLITE_STATUS");
				Iterable<GpsSatellite> allSatellites = gpsstatus
						.getSatellites();
				Iterator<GpsSatellite> it = allSatellites.iterator();
				int count = 0;
				while (it.hasNext()) {
					count++;
				}
				Log.d("Location", "Satellite Count:" + count);
				break;
			// 停止定位事件
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.d("Location", "GPS_EVENT_STOPPED");
				break;
			}
		}
	};

	// 创建位置监听器
	private LocationListener locationListener = new LocationListener() {
		// 位置发生改变时调用
		@Override
		public void onLocationChanged(Location location) {
			Log.d("Location", "onLocationChanged");
		}

		// provider失效时调用
		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Location", "onProviderDisabled");
		}

		// provider启用时调用
		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Location", "onProviderEnabled");
		}

		// 状态改变时调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("Location", "onStatusChanged");
		}
	};

}
