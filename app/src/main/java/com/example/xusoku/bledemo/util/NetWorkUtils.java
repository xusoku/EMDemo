package com.example.xusoku.bledemo.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * NetWork Utils
 * <ul>
 * <strong>Attentions</strong>
 * <li>You should add <strong>android.permission.ACCESS_NETWORK_STATE</strong>
 * in manifest, to get network status.</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-11-03
 */
public class NetWorkUtils {

	public static final String NETWORK_TYPE_WIFI = "wifi";
	public static final String NETWORK_TYPE_3G = "3g";
	public static final String NETWORK_TYPE_2G = "2g";
	public static final String NETWORK_TYPE_WAP = "wap";
	public static final String NETWORK_TYPE_UNKNOWN = "unknown";
	public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

	/**
	 * Get network type
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager == null ? null
				: connectivityManager.getActiveNetworkInfo();
		return networkInfo == null ? -1 : networkInfo.getType();
	}

	/**
	 * Get network type name
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkTypeName(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo;
		String type = NETWORK_TYPE_DISCONNECT;
		if (manager == null
				|| (networkInfo = manager.getActiveNetworkInfo()) == null) {
			return type;
		}
		;
		
		if (networkInfo.isConnected()) {
			String typeName = networkInfo.getTypeName();
			if ("WIFI".equalsIgnoreCase(typeName)) {
				type = NETWORK_TYPE_WIFI;
			} else if ("MOBILE".equalsIgnoreCase(typeName)) {
				String proxyHost = android.net.Proxy.getDefaultHost();
				type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORK_TYPE_3G
						: NETWORK_TYPE_2G)
						: NETWORK_TYPE_WAP;
			} else {
				type = NETWORK_TYPE_UNKNOWN;
			}
		}
		return type;
	}

	/**
	 * Whether is fast mobile network
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager == null) {
			return false;
		}

		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}
	/**
	 * 获取ConnectivityManager
	 */
	public static ConnectivityManager getConnManager(Context context) {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	/**
	 *是否为wifi
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null)
			return false;
		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

	}
	
	/**
	 * 是否存在有效的移动连接
	 * @param context
	 * @return boolean
	 */
	public static boolean isMobileConnected(Context context) {
		
		NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
		return net != null && net.getType() == ConnectivityManager.TYPE_MOBILE && net.isConnected();
	}
	/**
	 * 检测网络是否为可用状态
	 */
	public static boolean isAvailable(Context context) {
		return isWifiAvailable(context) || (isMobileAvailable(context) && isMobileEnabled(context));
	}

	/**
	 * 判断是否有可用状态的Wifi，以下情况返回false：
	 *  1. 设备wifi开关关掉;
	 *  2. 已经打开飞行模式；
	 *  3. 设备所在区域没有信号覆盖；
	 *  4. 设备在漫游区域，且关闭了网络漫游。
	 *  
	 * @param context
	 * @return boolean wifi为可用状态（不一定成功连接，即Connected）即返回ture
	 */
	public static boolean isWifiAvailable(Context context) {
		NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
		if (nets != null) {
			for (NetworkInfo net : nets) {
				if (net.getType() == ConnectivityManager.TYPE_WIFI) {
					if (net.getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 判断有无可用状态的移动网络，注意关掉设备移动网络直接不影响此函数。
	 * 也就是即使关掉移动网络，那么移动网络也可能是可用的(彩信等服务)，即返回true。
	 * 以下情况它是不可用的，将返回false：
	 *  1. 设备打开飞行模式；
	 *  2. 设备所在区域没有信号覆盖；
	 *  3. 设备在漫游区域，且关闭了网络漫游。
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isMobileAvailable(Context context) {
		NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
		if (nets != null) {
			for (NetworkInfo net : nets) {
				if (net.getType() == ConnectivityManager.TYPE_MOBILE) { return net.isAvailable(); }
			}
		}
		return false;
	}

	/**
	 * 设备是否打开移动网络开关
	 * @param context
	 * @return boolean 打开移动网络返回true，反之false
	 */
	public static boolean isMobileEnabled(Context context) {
		try {
			Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
			getMobileDataEnabledMethod.setAccessible(true);
			return (Boolean) getMobileDataEnabledMethod.invoke(getConnManager(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 反射失败，默认开启
		return true;
	}

	/**
	 * 打印当前各种网络状态
	 * @param context
	 * @return boolean
	 */
	public static boolean printNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo in = connectivity.getActiveNetworkInfo();
		
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					// if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
//					Log.i("", "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
//					Log.i("", "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
//					Log.i("", "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
//					Log.i("", "NetworkInfo[" + i + "]: " + info[i]);
					// }
				}
				
			} else {
				//Log.i(TAG, "getAllNetworkInfo is null");
			}
		}
		return false;
	}
	/**
	 * 打开设置界面
	 */
	public static void openNetSetting(Context context) {

		context.startActivity(new Intent(
		 		android.provider.Settings.ACTION_SETTINGS));
		// context.startActivity(new Intent(
		// 		android.provider.Settings.ACTION_WIFI_SETTINGS));
		//context.startActivity(new Intent(android.provider.Settings.ACTION_APN_SETTINGS));

	}

}
