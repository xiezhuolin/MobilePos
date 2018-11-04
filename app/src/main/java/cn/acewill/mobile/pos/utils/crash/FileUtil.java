package cn.acewill.mobile.pos.utils.crash;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.acewill.mobile.pos.utils.FileLog;

/**
 * 文件处理util
 */
public class FileUtil {
	static final         String FILES_PATH          = "Compressor";
	private static final int    EOF                 = -1;
	private static final int    DEFAULT_BUFFER_SIZE = 1024 * 4;

	private FileUtil() {

	}

	public static File from(Context context, Uri uri) throws IOException {
		InputStream inputStream = context.getContentResolver().openInputStream(uri);
		String      fileName    = getFileName(context, uri);
		String[]    splitName   = splitFileName(fileName);
		File        tempFile    = File.createTempFile(splitName[0], splitName[1]);
		tempFile = rename(tempFile, fileName);
		tempFile.deleteOnExit();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (inputStream != null) {
			copy(inputStream, out);
			inputStream.close();
		}

		if (out != null) {
			out.close();
		}
		return tempFile;
	}

	/**
	 * 获取文件名
	 *
	 * @param fileName
	 * @return
	 */
	static String[] splitFileName(String fileName) {
		String name      = fileName;
		String extension = "";
		int    i         = fileName.lastIndexOf(".");
		if (i != -1) {
			name = fileName.substring(0, i);
			extension = fileName.substring(i);
		}

		return new String[]{name, extension};
	}

	/**
	 * 根据uri获取文件路径
	 *
	 * @param context
	 * @param uri
	 * @return
	 */
	static String getFileName(Context context, Uri uri) {
		String result = null;
		if (uri.getScheme().equals("content")) {
			Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf(File.separator);
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}

	/**
	 * 根据uri获取真文件路径
	 *
	 * @param context
	 * @param contentUri
	 * @return
	 */
	static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int    index    = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			String realPath = cursor.getString(index);
			cursor.close();
			return realPath;
		}
	}

	static File rename(File file, String newName) {
		File newFile = new File(file.getParent(), newName);
		if (!newFile.equals(file)) {
			if (newFile.exists()) {
				if (newFile.delete()) {
					Log.d("FileUtil", "Delete old " + newName + " file");
				}
			}
			if (file.renameTo(newFile)) {
				Log.d("FileUtil", "Rename file to " + newName);
			}
		}
		return newFile;
	}

	static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
			throws IOException {
		long count = 0;
		int  n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 文件是否存在
	 *
	 * @param path
	 * @return
	 */
	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	public static String getSyncFoldPath() {
		File file = new File(Environment
				.getExternalStorageDirectory() + "/selfpos/syncdata");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String SDPATH = Environment.getExternalStorageDirectory() + "/";

	public static String getCacheSize() {
		File file = new File(SDPATH + "diancan2.0/OtherCatalog");
		if (file == null || !file.exists() || file.length() < 1024) {
			return "0 kb";
		}
		File[] files = file.listFiles();
		if (files.length <= 3) {
			return "0 kb";
		}
		return convertFileSize(file.length());
	}

	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}


	public static File[] clearLog(Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final File file2 = new File(SDPATH + "diancan2.0/crash");
				if (file2.exists()) {
					File[] files2 = file2.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file2) {
							return true;
						}
					});
					for (File file3 : files2) {
						file3.delete();
					}
				}
			}
		}).start();

		final List<String> nameList = getFileNameList();

		final File file = new File(SDPATH + "diancan2.0/OtherCatalog");
		if (!file.exists()) {
			Toast.makeText(context, "没有需要清理的缓存和日志", Toast.LENGTH_SHORT).show();
			return null;
		}
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file2) {
				for (String name : nameList) {
					//要拿到不同名字的
					if (file2.getName().equals(name)) {
						return false;
					}
				}
				return true;
			}
		});

		return files;
	}

	private static List<String> getFileNameList() {
		Calendar cal       = Calendar.getInstance();
		String   fileName1 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ".txt";
		cal.add(Calendar.DATE, -1);
		String   fileName2 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ".txt";
		Calendar cal2      = Calendar.getInstance();
		cal2.add(Calendar.DATE, -2);
		String       fileName3 = new SimpleDateFormat("yyyy-MM-dd").format(cal2.getTime()) + ".txt";
		List<String> nameList  = new ArrayList<String>();
		nameList.add(fileName1);
		nameList.add(fileName2);
		nameList.add(fileName3);
		return nameList;
	}

	public static File getUploadLog(int position) {

		final String fileName = getFileName(position);

		File file = new File(SDPATH + "MyApp"
				+ File.separator
				+ "System_log");
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file2) {
				return file2.getName().equals(fileName);
			}
		});

		if (files != null && files.length > 0) {
			return files[0];
		} else {

			return null;
		}
	}

	public static String getFileName(int position) {
		String fileName = null;
		switch (position) {
			case 0:
				Calendar cal1 = Calendar.getInstance();
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime());
				break;
			case 1:
				Calendar cal2 = Calendar.getInstance();
				cal2.add(Calendar.DATE, -1);
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal2.getTime());
				break;
			case 2:
				Calendar cal3 = Calendar.getInstance();
				cal3.add(Calendar.DATE, -2);
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal3.getTime());
				break;
			default:
				break;
		}
		return fileName + ".log";
	}

	public static void createSyncImageFile() {
		File imageFile = new File(FileUtil
				.getSyncFoldPath());
		if (!imageFile.exists()) {
			imageFile.mkdirs();
		}
	}

	public static String getCanXingJianFoldPath() {
		File file = new File(Environment
				.getExternalStorageDirectory() + "/selfpos/canxingjiandata");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getCanXingJianImagePath(String dishId) {
		File file = new File(FileUtil.getCanXingJianFoldPath() + "/imagedata/images");
		if (file.length() == 0)
			return null;
		for (File file1 : file.listFiles()) {
			if (file1.getName().contains(dishId)) {
				return file1.getAbsolutePath();
			}
		}
		return "";
	}

	public static String getSyncImagePath(String dishId) {
		File file = new File(FileUtil.getSyncFoldPath() + "/imagedata/images");
		if (file.length() == 0)
			return null;
		for (File file1 : file.listFiles()) {
			if (file1.getName().contains(dishId)) {
				return file1.getAbsolutePath();
			}
		}
		return "";
	}

	public static List<String> getSyncImageList(String fileName) {
		File              file       = new File(FileUtil.getSyncFoldPath() + "/imagedata/images");
		ArrayList<String> bannerList = new ArrayList<>();
		if (file.length() == 0)
			return null;
		for (File file1 : file.listFiles()) {
			if (file1.getName().contains(fileName)) {
				bannerList.add(file1.getAbsolutePath());
			}
		}
		return bannerList;
	}

	public static File saveCrashInfo2File(Throwable ex) {
		Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
		StringBuffer        sb   = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key   = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer      writer = new StringWriter();
		PrintWriter pw     = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             dir = FileLog.getLogFile();
				FileOutputStream fos = new FileOutputStream(dir, true);
				fos.write(sb.toString()
						.getBytes());
				fos.close();
				return dir;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File saveCrashInfo2File(Throwable ex, Map<String, String> info) {
		if (info == null)
			info = new HashMap<String, String>();// 用来存储设备信息和异常信息
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key   = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer      writer = new StringWriter();
		PrintWriter pw     = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File             dir = FileLog.getLogFile();
				FileOutputStream fos = new FileOutputStream(dir, true);
				fos.write(sb.toString()
						.getBytes());
				fos.close();
				return dir;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * 应用程序的缓存存储文件夹
	 *
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}
}
