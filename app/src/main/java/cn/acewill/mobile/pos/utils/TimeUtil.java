package cn.acewill.mobile.pos.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.acewill.mobile.pos.model.order.DiscountTime;


public class TimeUtil {

	private static final String dayPattern = "yyyy-MM-dd";

	public static boolean isDiscountTime(DiscountTime discountTime) {
		if (discountTime != null && discountTime.type == 0) {
			long currentTimeMillis = System.currentTimeMillis();
			if (discountTime.sdate != 0
					&& discountTime.sdate > currentTimeMillis) {
				return false;
			}
			if (discountTime.edate != 0
					&& discountTime.edate < currentTimeMillis) {
				return false;
			}

			if ((!TextUtils.isEmpty(discountTime.weekday))
					&& (!discountTime.weekday.contains((ToolsUtils.getDayOfWeek() + 1)+ ""))) {
				return false;
			}

			return inHourTime(discountTime.stime, discountTime.etime);

		}

		return true;
	}

	public static boolean inHourTime(String startTime, String endTime) {
		Date date = new Date();
		int currentTime = date.getHours() * 100 + date.getMinutes();
		if (!TextUtils.isEmpty(startTime)) {
			Integer stime = Integer.valueOf(startTime.replace(":", ""));
			if (stime > currentTime) {
				return false;
			}
		}
		if (!TextUtils.isEmpty(endTime)) {
			Integer etime = Integer.valueOf(endTime.replace(":", ""));
			if (etime < currentTime) {
				return false;
			}
		}
		return true;
	}

	//时间戳转字符串
	public static String getStringTime(long time){
		SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
		return format.format(time);
	}

	//时间戳转字符串
	public static String getStringTimeLong(long time){
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(time);
	}

	//时间戳转字符串
	public static String getStringTimeMinute(long time){
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(time);
	}

	public static String getTimeStr(long time) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sf.format(new Date(time));
	}

	//获取前几天日期
	public static String getAgoDay(int day){
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,day);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	//获取当天日期
	public static String getTimeDayStr(){
		try
		{
			SimpleDateFormat format = new SimpleDateFormat(dayPattern);
			return format.format(new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getHourStr() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return format.format(date);
	}



	//获取小时分钟字符串
	public static String getHour() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		return format.format(date);
	}

	//比较两个时间，d1>d2返回1，d1<d2返回-1，d1=d2返回0
	public static int compareData(String d1,String d2){
		int i = 0;
		try {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date dateTime1 = dateFormat.parse(d1);
			Date dateTime2 = dateFormat.parse(d2);
			i = dateTime1.compareTo(dateTime2);
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}
	//获取当天星期
	public static String getWeekOfDate() {
		Date dt = new Date();
		String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static String getHours(String time){
		try {
			if(TextUtils.isEmpty(time)){
				return time;
			}
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 =  new SimpleDateFormat("HH:mm:ss");
			Date data = format.parse(time);
			return format2.format(data);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//时间戳转字符串
	public static Integer getStringData(){
		SimpleDateFormat format =  new SimpleDateFormat("MMddmmss");
		return Integer.valueOf(format.format(System.currentTimeMillis()));
	}

	public static String getDateStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public static String getDateStr() {
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		Date date = new Date();
		return format.format(date);
	}

	public static long getOrderItemId(int i)
	{
		return System.currentTimeMillis()+i;
	}

	/**
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
	 *
	 * @param time
	 * @return
	 */
	public static String times(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
//		long i = Long.valueOf(time);
		String times = sdr.format(new Date(lcc));
		return times;

	}

	public static String times2(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
//		long i = Long.valueOf(time);
		String times = sdr.format(new Date(lcc));
		return times;
	}

	public static String getTimeToken() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		return format.format(date);
	}

	public static String getDayTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return format.format(date);
	}

}
