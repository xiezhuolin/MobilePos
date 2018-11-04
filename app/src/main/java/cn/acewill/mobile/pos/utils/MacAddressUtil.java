package cn.acewill.mobile.pos.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import cn.acewill.mobile.pos.config.Store;


public class MacAddressUtil {
	/**
	 * 断网情况下某些机器会返回一个默认的MAC地址不会返回空字符,所以调用获取序列号的方法来替代MAC地址
	 * @return
     */
	public static String getMacAddress(Context context) {
//		String localMacAddress = getLocalMacAddress(activity);
//		if (localMacAddress != null) {
//			return localMacAddress.trim();
//		}
		return getTenTokenStr(context).trim();
	}

	/**
	 * 网线状态下获得本地MAC地址
	 */
	public static String getMacAddress() {

		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig", "HWaddr");

		// 如果返回的result == null，则说明网络不可取
		if (result == null) {
			return "";
		}

		// 对该行数据进行解析
		// 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);
			Log.i("test", "Mac:" + Mac + " Mac.length: " + Mac.length());

			if (Mac.length() > 1) {
				return Mac;
			}
			Log.i("test", result + " result.length: " + result.length());
		}
		return result;
	}

//	/**
//	 * wifi状态下的mac地址
//	 *
//	 * @param activity
//	 * @return
//	 */
//	public static String getLocalMacAddress(Activity activity) {
//		WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = wifi.getConnectionInfo();
//		return info.getMacAddress();
//	}

	public static String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			// 执行命令cmd，只取结果中含有filter的这一行
			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {
				// result += line;
				Log.i("test", "line: " + line);
			}

			result = line;
			Log.i("test", "result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getMac() {
		String macSerial = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			String line;
			while ((line = input.readLine()) != null) {
				macSerial += line.trim();
			}

			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return macSerial;
	}

	/**
	 * 获取移动设备本地IP
	 * @return
	 */
	protected static InetAddress getLocalInetAddress() {
		InetAddress ip = null;
		try {
			//列举
			Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
			while (en_netInterface.hasMoreElements()) {//是否还有元素
				NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
				Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
				while (en_ip.hasMoreElements()) {
					ip = en_ip.nextElement();
					if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
						break;
					else
						ip = null;
				}

				if (ip != null) {
					break;
				}
			}
		} catch (SocketException e) {

			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 获取本地IP
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据IP获取本地Mac
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddressFromIp(Context context) {
		String mac_s= "";
		try {
			byte[] mac;
			NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mac_s;
	}

	/**
	 * 二进制转十六进制
	 * @param b
	 * @return
	 */
	public static  String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}
	public static String getIMEI(Context context) {
		return ((TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	public static String getAndroidId(Context context) {
		return android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
	}

	public static String getSimSerialNumber(Context context) {
		return ((TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE)).getSimSerialNumber();
	}

	/**
	 * getSerialNumber
	 * @return result is same to getSerialNumber1()
	 */
	public static String getSerialNumber(){
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serial;
	}

	public static void displayDevice(Context context){
		String dest_imei = getIMEI(context);
		String androidId = getAndroidId(context);
		Log.i("TAG", "isTestDevice: "
				+" \nIMEI:"+dest_imei
				+" \nANDROID ID:" + androidId
				+" \nSerialNumber:" + getSerialNumber()
				+" \nSimSerialNumber:" + getSimSerialNumber(context)
		);
	}

	public static String getTenTokenStr(Context context)
	{
		Store store = Store.getInstance(context);
		String tokenStr = "";
		if(TextUtils.isEmpty(store.getTenTokenStr()))
		{
			String randomNumber = String.valueOf(generateRandomNumber(3));
			tokenStr = getStringData(System.currentTimeMillis())+"-"+randomNumber;
		}
		else
		{
			tokenStr = store.getTenTokenStr();
		}
		return tokenStr;
	}

	//时间戳转字符串
	public static String getStringData(long time){
		SimpleDateFormat format =  new SimpleDateFormat("MMddmmss");
		return format.format(time);
	}

	/**
	 * 产生n位随机数
	 * @return
	 */
	public static long generateRandomNumber(int n){
		if(n<1){
			throw new IllegalArgumentException("随机数位数必须大于0");
		}
		return (long)(Math.random()*9*Math.pow(10,n-1)) + (long)Math.pow(10,n-1);
	}
}
