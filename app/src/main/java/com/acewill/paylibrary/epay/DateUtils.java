package com.acewill.paylibrary.epay;

//处理时间戳
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static SimpleDateFormat sf = null;

	/* 获取系统时间 格式为："yyyy-MM-dd HH:mm:ss" */
	public static String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}

	/* 时间戳转换成字符窜 */
	public static String getDateToString(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}

	public static String getDateToStringForWX(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sf.format(d);
	} 
	
	/* 将字符串转为时间戳 */
	public static long getStringToDate(String time) {
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

}
