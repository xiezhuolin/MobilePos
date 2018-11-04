package cn.acewill.mobile.pos.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author：Anch
 * Date：2017/5/9 19:13
 * Desc：
 */
public class FileLog {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 用于格式化日期,作为日志文件名的一部分


	public static File getLogFile() {
		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "MyApp"
				+ File.separator
				+ "System_log";
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Calendar cal  = Calendar.getInstance();
		String   day  = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		File     file = new File(dir, day + ".log");

		return file;
	}

	/**
	 * 输入日志文件
	 *
	 * @param level      日志级别
	 * @param classz     类名
	 * @param methodname 方法名
	 * @param action     操作
	 * @param result     结果
	 */
	public static void log(String level, Class classz, String methodname, String action, String result) {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             file = getLogFile();
				FileOutputStream fos  = new FileOutputStream(file, true);
				result = format
						.format(new Date()) + "/" + classz.getPackage()
						.getName() + "." + methodname + " "
						+ level + "/" + action + ">>" + result + "\n========================================================================================================================================\n";
				if (result != null) {
					fos.write(result
							.getBytes());
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 输入日志文件
	 */
	public static void log(String result) {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             file = getLogFile();
				FileOutputStream fos  = new FileOutputStream(file, true);
				result = format
						.format(new Date()) + result + "\n========================================================================================================================================\n";
				if (result != null) {
					fos.write(result
							.getBytes());
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 输入日志文件
	 *
	 * @param level      日志级别
	 * @param classpath  类的包
	 * @param methodname 方法名
	 * @param action     操作
	 * @param result     结果
	 */
	public static void log(String level, String classpath, String methodname, String action, String result) {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             file = getLogFile();
				FileOutputStream fos  = new FileOutputStream(file, true);
				result = format
						.format(new Date()) + "/" + classpath + "." + methodname + " "
						+ level + "/" + action + "\n" + result
						.toString() + "\n========================================================================================================================================\n";
				if (result != null) {
					fos.write(result.toString()
							.getBytes());
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void logLine() {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             file = getLogFile();
				FileOutputStream fos    = new FileOutputStream(file, true);
				String           result = "\n\n\n\n\n";
				if (result != null) {
					fos.write(result.toString()
							.getBytes());
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
