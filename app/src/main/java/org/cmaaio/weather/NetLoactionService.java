package org.cmaaio.weather;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.db.SharedPrefsConfig;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NetLoactionService extends Service {
	private Location currentLocation = null;
	private LocationManager locationManager = null;
	private String currentProvider = null;
	private boolean isDestory = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isDestory = false;
		// 获取到LocationManager对象
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 创建一个Criteria对象
		Criteria criteria = new Criteria();
		// 设置粗略精确度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// 设置是否需要返回海拔信息
		criteria.setAltitudeRequired(false);
		// 设置是否需要返回方位信息
		criteria.setBearingRequired(false);
		// 设置是否允许付费服务
		criteria.setCostAllowed(true);
		// 设置电量消耗等级
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		// 设置是否需要返回速度信息
		criteria.setSpeedRequired(false);

		// 根据设置的Criteria对象，获取最符合此标准的provider对象
		currentProvider = locationManager.getBestProvider(criteria, true);
		Log.d("Location", "currentProvider: " + currentProvider);
		// 根据当前provider对象获取最后一次位置信息
		if (currentProvider != null) {
			currentLocation = locationManager.getLastKnownLocation(currentProvider);
		}
		else
		{
			Toast.makeText(Cmaaio.getInstance(), Cmaaio.getInstance().getResources().getString(R.string.location_tips), Toast.LENGTH_LONG).show();
		}
		// 如果位置信息为null，则请求更新位置信息
		if (currentLocation == null && currentProvider != null) {
			locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestory = true;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			locThread.start();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	Thread locThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (!isDestory) {
				if (currentProvider != null) {
					currentLocation = locationManager.getLastKnownLocation(currentProvider);
				}
				if (currentLocation != null) {
					float lat = (float) currentLocation.getLatitude();
					float lon = (float) currentLocation.getLongitude();
//					Log.d("whz", "Net Latitude: " + lat);
//					Log.d("whz", "Net location: " + lon);

					SharedPrefsConfig.getSharedPrefsInstance(NetLoactionService.this).setLatitude(lat);
					SharedPrefsConfig.getSharedPrefsInstance(NetLoactionService.this).setLongitude(lon);
					// 根据经纬度取城市
					HttpOperator.getCityNameByLatlng(lat, lon, new HttpStrMsgCallBack() {

						@Override
						public void onSuccess(String msg) {
							String city = msg;
							Log.d("Location", "get city name:" + city);
							// 根据城市名取yahoo woeid
							HttpOperator.getYahooWoeidByCityName(city, new HttpStrMsgCallBack() {
								@Override
								public void onSuccess(String msg) {
									Log.d("Location", "woeid:" + msg);
									SharedPrefsConfig.getSharedPrefsInstance(NetLoactionService.this).setYahoowoeid(msg);
									Intent intent = new Intent();
									intent.setAction(GetlocationService.GETYAHOOWEATHERBROADCAST);
									sendBroadcast(intent);
								}

								@Override
								public void onFail(String msg) {
									Log.e("Location", "getYahooWoeidByCityName failed:" + msg);
								}
							});
						}

						@Override
						public void onFail(String msg) {
							Log.e("Location", "getCityNameByLatlng failed:" + msg);
						}
					});

					break;
				} else {
//					Log.d("whz", "Net Latitude: " + 0);
//					Log.d("whz", "Net location: " + 0);
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Log.d("Location", e.getMessage());
				}
			}
		}
	});

	// 创建位置监听器
	private LocationListener locationListener = new LocationListener() {
		// 位置发生改变时调用
		@Override
		public void onLocationChanged(Location location) {
			Log.d("Location", "onLocationChanged");
			Log.d("Location", "onLocationChanged Latitude" + location.getLatitude());
			Log.d("Location", "onLocationChanged location" + location.getLongitude());
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
