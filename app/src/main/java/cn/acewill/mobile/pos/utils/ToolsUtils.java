package cn.acewill.mobile.pos.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.model.KitchenPrintMode;
import cn.acewill.mobile.pos.model.MarketObject;
import cn.acewill.mobile.pos.model.MarketType;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.service.PosInfo;

import static cn.acewill.mobile.pos.utils.TimeUtil.getStringTimeLong;


public class ToolsUtils {

	/**
	 * 判断是否有网络连接      * @param context      * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断是否为数字
	 *
	 * @param str 传入的字符串
	 * @return true 表示为数字
	 * @author Sxf
	 * @date 2014-9-26 下午8:23:44
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher((CharSequence) str);
		return matcher.matches();
	}


	/**
	 * 判断是否为字母
	 *
	 * @param str
	 * @return true 表示为字母
	 */
	public static boolean isLetter(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher matcher = pattern.matcher((CharSequence) str);
		return matcher.matches();
	}

	/**
	 * 将字符串16进制转成系统识别的int color颜色值
	 *
	 * @param colorStr
	 * @return
	 */
	public static int getStrColor2Int(String colorStr) {
		if (TextUtils.isEmpty(colorStr)) {
			colorStr = "76ea9f";
		}
		int n = Color.parseColor("#" + colorStr);
		return n;
	}

	// 判断字符串是否为空

	/**
	 * @param str
	 * @return true 空
	 */
	public static boolean isNull(String str) {
		if (str == null || TextUtils.isEmpty(str) || str.equals("null")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取手机内部可用存储空间
	 *
	 * @param context
	 * @return 以M, G为单位的容量
	 */
	public static String getAvailableInternalMemorySize(Context context) {
		File   file                = Environment.getDataDirectory();
		StatFs statFs              = new StatFs(file.getPath());
		long   availableBlocksLong = statFs.getAvailableBlocksLong();
		long   blockSizeLong       = statFs.getBlockSizeLong();
		return Formatter.formatFileSize(context, availableBlocksLong
				* blockSizeLong);
	}

	// 判断List数组是否为空

	/**
	 * @return true 空
	 */
	public static boolean isList(List list) {
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	//版本名
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	//版本号
	public static int getVersionCode(Context context) {
		return getPackageInfo(context).versionCode;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;

		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);

			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pi;
	}


	/**
	 * 检测是否有SDCard
	 *
	 * @return
	 */
	public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取SDCard路径
	 *
	 * @return
	 */
	public static String getSDcardPath() {
		if (!Environment.getExternalStorageDirectory().exists()) {
			return null;
		} else {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
	}

	/**
	 * 获得图片存放的路径
	 *
	 * @return
	 */
	public static String getImagePath() {
		if (TextUtils.isEmpty(getSDcardPath())) {
			return null;
		} else {
			String ImagePath = getSDcardPath() + Constant.PHOTO_PATH;
			File   destDir   = new File(ImagePath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			return ImagePath;
		}
	}

	/**
	 * 获得打印小票存放的路径
	 *
	 * @return
	 */
	public static String getFilePath(String fileName) {
		if (TextUtils.isEmpty(getSDcardPath())) {
			return null;
		} else {
			String path = getSDcardPath() + File.separator
					+ "MyApp"
					+ File.separator
					+ fileName;
			File destDir = new File(path);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			return path;
		}
	}


	/**
	 * 生成图片名字
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		getImagePath();
		Date             date       = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMddHHmmssSSS");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPrinterReceiptFile(String fileName) {
		getFilePath(fileName);
		Date             date       = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'LOG'_yyyy-MM-dd");
		return dateFormat.format(date) + ".txt";
	}

	/**
	 * 追加文件：使用FileWriter
	 *
	 * @param content
	 */
	public static void writePrinterReceiptInfo(String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			String     path    = getFilePath("PrinterReceipt_log");
			String     filPath = getPrinterReceiptFile("PrinterReceipt_log");
			FileWriter writer  = new FileWriter(path + "/" + filPath, true);
			writer.write(content);
			writer.write("\r\n");
			writer.write("\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录了用户在使用POS的过程中的操作情况
	 *
	 * @param operationRecords
	 */
	public static void writeUserOperationRecords(String operationRecords) {
		PosInfo posInfo  = PosInfo.getInstance();
		String  userName = TextUtils.isEmpty(posInfo.getRealname()) ? "" : posInfo.getRealname();
		FileLog.log("操作时间:" + getStringTimeLong(System
				.currentTimeMillis()) + ",用户(" + userName + ")操作==>>" + operationRecords);
	}

	/**
	 * 跳转到裁剪图片界面
	 *
	 * @param aty
	 * @param uri
	 * @param requestCode
	 */
	public static void intentCrop(Activity aty, Uri uri, int requestCode) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 190);
		intent.putExtra("outputY", 190);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		aty.startActivityForResult(intent, requestCode);
	}

	public static Bitmap compressImage(Bitmap image) {
		Bitmap bitmap = null;// 把ByteArrayInputStream数据生成图片
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 90, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int quality = 100;
			//        Logger.jLog().i("before" + baos.toByteArray().length);
			while (baos.toByteArray().length / 1048576 > 2) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				quality -= 5;// 每次都减少5
			}
			//        Logger.jLog().i("after" + baos.toByteArray().length);
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos
					.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			try {
				bitmap = BitmapFactory.decodeStream(isBm, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public static String saveBitmap(Bitmap bm) {
		String fileName = getPhotoFileName();
		String path     = getImagePath() + fileName;
		try {
			File f = new File(getImagePath(), fileName);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm = compressImage(bm);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			if (isNull(path)) {
				path = "";
			}
		} catch (FileNotFoundException e) {
			path = "";
		} catch (IOException e) {
			path = "";
		}
		return path;
	}

	public static void BmpToFile(Bitmap bmp, File file) {
		if (bmp != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap loadBitmapToBitmap(String imgpath, boolean adjustOritation) {
		return loadBitmapToBitmapCustom(imgpath, adjustOritation, 1242, 2208);
	}

	public static Bitmap loadBitmapToBitmapCustom(String imgpath, boolean adjustOritation, int width, int height) {
		String pathtmp = imgpath.replace("file://", "");
		if (!adjustOritation) {
			return loadBitmap(pathtmp, width, height);
		}
		Bitmap bm = loadBitmap(pathtmp, width, height);
		if (bm == null) {
			return null;
		}
		ExifInterface exif;
		int           digree = 0;
		try {
			exif = new ExifInterface(imgpath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			switch (exif.getAttributeInt("Orientation", 0)) {
				case 3:
					digree = 180;
					break;
				case 6:
					digree = 90;
					break;
				case 8:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
			}
		}

		if (digree == 0) {
			return bm;
		}
		Matrix m = new Matrix();
		m.postRotate((float) digree);
		Bitmap bmTmp = bm;
		try {
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}
		bmTmp.recycle();
		return bm;
	}


	public static Bitmap loadBitmap(String imgpath, int width, int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgpath, opts);
		opts.inSampleSize = computeSampleSize(opts, Math.min(width, height), width * height);
		opts.inJustDecodeBounds = false;
		opts.inInputShareable = true;
		opts.inPurgeable = true;
		try {
			return BitmapFactory.decodeFile(imgpath, opts);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		if (initialSize > 8) {
			return ((initialSize + 7) / 8) * 8;
		}
		int roundedSize = 1;
		while (roundedSize < initialSize) {
			roundedSize <<= 1;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = (double) options.outWidth;
		double h = (double) options.outHeight;
		int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math
				.ceil(Math.sqrt((w * h) / ((double) maxNumOfPixels)));
		int upperBound = minSideLength == -1 ? TransportMediator.FLAG_KEY_MEDIA_NEXT : (int) Math
				.min(Math.floor(w / ((double) minSideLength)), Math
						.floor(h / ((double) minSideLength)));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if (maxNumOfPixels == -1 && minSideLength == -1) {
			return 1;
		}
		if (minSideLength != -1) {
			return upperBound;
		}
		return lowerBound;
	}

	/**
	 * 压缩图片
	 *
	 * @param imagePath
	 * @param width     期望宽度
	 * @param height    期望高度
	 * @return
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
	                                       int height) {
		Bitmap                bitmap  = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;

		int h = options.outHeight;
		int w = options.outWidth;
		if (h < w) {
			int size = width;
			width = height;
			height = size;
		}
		int beWidth  = w / width;
		int beHeight = h / height;
		int be       = 1;
		if (beWidth < beHeight)
			be = beHeight;
		else {
			be = beWidth;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;

		try {
			InputStream instream = new FileInputStream(imagePath);
			bitmap = BitmapFactory.decodeStream(instream, null, options);
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * 测试字符串是否符合json格式
	 *
	 * @param json 传递来的字符串参数
	 * @return 布尔值
	 */
	public static boolean checkJsonDataFormat(String json) {
		if (json == null || json.equals(""))
			return false;
		if (json.startsWith("[") && json.endsWith("]"))
			return true;
		if (json.startsWith("{") && json.endsWith("}"))
			return true;

		return false;
	}

	/**
	 * 将一些设置属性保存到sharedpreferences文件中
	 *
	 * @param key     键
	 * @param value   值
	 * @param context 上下文对象
	 * @return 布尔值
	 * @throws Exception 异常处理
	 */
	public static boolean setStringToShares(String key, String value,
	                                        Context context) throws Exception {
		boolean returnValue = false;
		if (key == null || value == null)
			return returnValue;
		key = key.trim();
		value = value.trim();
		if (key.equals("")) {
			return returnValue;
		}
		try {
			SharedPreferences shares = context.getSharedPreferences(
					"sharefile", Context.MODE_WORLD_WRITEABLE);
			Editor editor = shares.edit();
			editor.putString(key, value);
			editor.commit();
			returnValue = true;

		} catch (Exception e) {
			throw e;

		}
		return returnValue;
	}

	/**
	 * 从sharedpreferences文件中得到设置的属性
	 *
	 * @param key
	 * @param defaultValue
	 * @param context
	 * @return 属性的设置值
	 * @throws Exception
	 */
	public static String getStringFromShares(String key, String defaultValue, Context context) throws Exception {
		String value = null;
		if (key == null || defaultValue == null)
			return value;
		key = key.trim();
		try {
			SharedPreferences shares = context.getSharedPreferences(
					"sharefile", Context.MODE_WORLD_READABLE);

			value = shares.getString(key, defaultValue);
		} catch (Exception e) {
			throw e;
		}
		return value;
	}


	public static String getMacAddress(Context context) {
		//以太网和Wifi环境下都支持
		return MacAddressUtil.getMacAddress();
	}

	public static int getDayOfWeek() {
		int            DAY_OF_WEEK = 0;
		final Calendar c           = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		DAY_OF_WEEK = c.get(Calendar.DAY_OF_WEEK);
		return DAY_OF_WEEK;
	}

	public static String getPrinterSth(Object obj) {
		Gson gs = new Gson();
		return gs.toJson(obj);
	}

	public static boolean isIP(String addr) {

		String rexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

		return addr.matches(rexp);

	}


	public static String formatTime(long timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		String           str = sdf.format(timeStr);
		return str;
	}

	private static InputMethodManager imm = null;

	// 隐藏软键盘
	public static void hideInputManager(Context ct, View v) {
		try {
			if (imm == null) {
				imm = (InputMethodManager) ct
						.getSystemService(Context.INPUT_METHOD_SERVICE);
			}
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			Log.e("InputView", "hideInputManager Catch error,skip it!", e);
		}
	}

	public static void showInputManager(Context ct, View v) {
		if (imm == null) {
			imm = (InputMethodManager) ct
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static int dp2px(Context context, int dp) {
		return (int) TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
						.getDisplayMetrics());
	}


	/**
	 * 判断是否配置桌台  true是  false否
	 *
	 * @return
	 */
	public static boolean logicIsTable() {
		boolean isTable   = true;
		String  storeMode = StoreInfor.storeMode;
		if (!TextUtils.isEmpty(storeMode) && storeMode.equals("TABLE")) {
			isTable = true;
		} else {
			isTable = false;
		}
		return isTable;
	}

	/**
	 * 先用上面方法判断是否有桌台,如果没有则再去判断是否是送餐模式  false不是  true是
	 * //true则为送餐模式， 需要在结账时弹框，输入一个餐牌号码   false：顾客自取
	 *
	 * @return
	 */
	public static boolean getCardNumberMode() {
		boolean isCardNumberMode = false;
		if (StoreInfor.cardNumberMode) {
			isCardNumberMode = true;
		}
		return isCardNumberMode;
	}

	/**
	 * 判断是否是管理员
	 *
	 * @return
	 */
	public static boolean logicIsAdministrator() {
		boolean isAdministrator = false;
		//        if()
		//        {
		//            isAdministrator = true;
		//        }
		return isAdministrator;
	}

	public static byte[] Byte2byte(Byte[] Bytes) {
		byte[] bytes = new byte[Bytes.length];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = Bytes[i].byteValue();
		}

		return bytes;
	}

	public static boolean getIsPackage(OrderItem orderItem) {
		boolean isPackage = false;
		if (orderItem != null) {
			if (orderItem.subItemList != null && orderItem.subItemList.size() > 0) {
				isPackage = true;
			}
		}
		return isPackage;
	}

	/**
	 * 是否是反结账
	 *
	 * @param reverseCheckOut
	 * @return
	 */
	public static boolean isReverseCheckOut(int reverseCheckOut) {
		if (reverseCheckOut > 0 && reverseCheckOut != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是一份一单 还是多份一单
	 *
	 * @return
	 */
	public static KitchenPrintMode getKitchenPrintMode() {
		if (StoreInfor.printConfiguration.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
			return KitchenPrintMode.PER_DISH;
		}
		return KitchenPrintMode.PER_ITEM;
	}


	/**
	 * 判断选取外卖时是否要顾客填写信息
	 *
	 * @return
	 */
	public static boolean getCustomerInfoForWaimai() {
		if (StoreInfor.customerInfoForWaimai) {
			return true;
		}
		return false;
	}

	public static boolean isPrintPackName() {
		Store store = Store.getInstance(MyApplication.getContext());
		if (store.isPrintPackageName()) {
			return true;
		}
		return false;
	}

	public static String isWaiDai(String orderType) {
		if (!orderType.equals("EAT_IN")) {
			return "(外带)";
		}
		return "";
	}

	public static <T> T cloneTo(T src) throws RuntimeException {
		ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
		ObjectOutputStream    out          = null;
		ObjectInputStream     in           = null;
		T                     dist         = null;
		try {
			out = new ObjectOutputStream(memoryBuffer);
			out.writeObject(src);
			out.flush();
			in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
			dist = (T) in.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null)
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			if (in != null)
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
		return dist;
	}

	public static String logicCardType(Account account) {
		String cardType = "";
		if (!TextUtils.isEmpty(account.getType())) {
			if (account.getType().equals("wx")) {
				cardType = "微信卡";
			} else if (account.getType().equals("dp")) {
				cardType = "点评卡";
			} else if (account.getType().equals("ph")) {
				cardType = "实体卡";
			}
		}
		return cardType;
	}

	public static String logicUserCardGender(Account account) {
		String gender = "";
		if (!TextUtils.isEmpty(account.getGender())) {
			if (account.getGender().equals("1")) {
				gender = "先生";
			} else if (account.getGender().equals("2")) {
				gender = "女士";
			}
		}
		return gender;
	}

	public static HashMap<String, String> getCardInfo(Context context) {
		SharedPreferences spf = context.getSharedPreferences("name",
				Context.MODE_PRIVATE);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("3828405866", spf.getString("3828405866", "100.00"));
		map.put("3832102874", spf.getString("3832102874", "200.00"));
		map.put("3948103991", spf.getString("3948103991", "80.00"));
		map.put("3948282887", spf.getString("3948282887", "30.00"));
		map.put("1238835296", spf.getString("1238835296", "3000.00"));
		return map;
	}

	public static void setCardInfo(Context context, String card, String price) {
		SharedPreferences spf = context.getSharedPreferences("name",
				Context.MODE_PRIVATE);
		Editor editor = spf.edit();
		editor.putString(card, price);
		editor.commit();
	}

	public static String handleCarDish(List<Dish> dishes) {
		StringBuffer sb = new StringBuffer();
		if (dishes != null && dishes.size() > 0) {
			int size = dishes.size();
			for (int i = 0; i < size; i++) {
				Dish   dish   = dishes.get(i);
				String symbol = "";
				if (i != size - 1) {
					symbol = ",";
				}
				sb.append(dish.getDishName() + "x" + dish.quantity + symbol);
			}
		}
		return sb.toString();
	}

	/**
	 * 整除次数
	 *
	 * @return
	 */
	public static int aliquotCount(float i, float j) {
		int z = 0;
		if (j == 0) {
			return 0;
		} else {
			z = (int) (i / j);
		}
		return z;
	}

	private String getDisCountStr(List<MarketObject> marketList, int quantity) {
		StringBuffer sb = new StringBuffer();
		if (marketList != null && marketList.size() > 0) {
			int size = marketList.size();
			for (int i = 0; i < size; i++) {
				MarketObject market = marketList.get(i);
				if (market.getMarketType() == MarketType.MANUAL && market.getDiscountType() == 1) {
					sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash()
							.multiply(new BigDecimal(quantity)).toString() + "元");
				} else {
					sb.append("    -" + market.getMarketName() + "-" + market.getReduceCash()
							.toString() + "元");
				}
				if (i != size - 1) {
					sb.append("\n");
				}
			}
		}
		return sb.toString();
	}

	public static String getDisCountStr(List<MarketObject> marketList, List<MarketObject> tempMarketList, Dish dish) {
		StringBuffer       sb           = new StringBuffer();
		List<MarketObject> lsMarketList = new CopyOnWriteArrayList<>();
		if (!ToolsUtils.isList(marketList)) {
			lsMarketList = ToolsUtils.cloneTo(marketList);
		}
		if (!ToolsUtils.isList(tempMarketList)) {
			lsMarketList.addAll(tempMarketList);
		}
		if (lsMarketList != null && lsMarketList.size() > 0) {
			int           size = lsMarketList.size();
			DecimalFormat df   = new DecimalFormat(".00");
			for (int i = 0; i < size; i++) {
				MarketObject market     = lsMarketList.get(i);
				String       reduceCash = "";
				if (market.getMarketType() == MarketType.MANUAL && market.getDiscountType() == 1) {
					reduceCash = market.getReduceCash().multiply(new BigDecimal(dish.getQuantity()))
							.toString();
				} else {
					reduceCash = market.getReduceCash().toString();
				}
				reduceCash = reduceCash.substring(0, reduceCash.indexOf(".") + 3);
				sb.append("- " + market.getMarketName() + "  ¥" + reduceCash);
				if (i != size - 1) {
					sb.append("\n");
				}
			}
		}
		return sb.toString();
	}

	public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
		// Retrieve all services that can match the given intent
		PackageManager    pm          = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}
		// Get component info and create ComponentName
		ResolveInfo   serviceInfo = resolveInfo.get(0);
		String        packageName = serviceInfo.serviceInfo.packageName;
		String        className   = serviceInfo.serviceInfo.name;
		ComponentName component   = new ComponentName(packageName, className);
		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);
		// Set the component to be explicit
		explicitIntent.setComponent(component);
		return explicitIntent;
	}

	/**
	 * 支付结账金额是抹零还是四舍五入还是不处理
	 *
	 * @param payMoney
	 * @return
	 */
	public static BigDecimal wipeZeroMoney(BigDecimal payMoney) {
		BigDecimal wipeZeroMoney = new BigDecimal("0.00");
		switch (StoreInfor.wipeZero) {
			// 0：自动抹零到元
			case 0:
				wipeZeroMoney = payMoney.setScale(0, BigDecimal.ROUND_DOWN);
				break;
			// 1：四舍五入到元
			case 1:
				wipeZeroMoney = payMoney.setScale(0, BigDecimal.ROUND_HALF_UP);
				break;
			//2：自动抹零到角
			case 2:
				wipeZeroMoney = payMoney.setScale(1, BigDecimal.ROUND_DOWN);
				break;
			//3：四舍五入到角
			case 3:
				wipeZeroMoney = payMoney.setScale(1, BigDecimal.ROUND_HALF_UP);
				break;
			// 不做任何处理
			default:
				wipeZeroMoney = payMoney;
				break;

		}
		return wipeZeroMoney;
	}

	public static String getIPAddress(Context context) {
		NetworkInfo info = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
				try {
					//Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface
							.getNetworkInterfaces(); en.hasMoreElements(); ) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf
								.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress
									.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo  = wifiManager.getConnectionInfo();
				String   ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
				return ipAddress;
			} else {//如果都不是 那就默认认为处在使用有线网络
				try {
					Enumeration<NetworkInterface> enumerationNi = NetworkInterface
							.getNetworkInterfaces();
					while (enumerationNi.hasMoreElements()) {
						NetworkInterface networkInterface = enumerationNi.nextElement();
						String           interfaceName    = networkInterface.getDisplayName();
						Log.i("tag", "网络名字" + interfaceName);
						// 如果是有线网卡
						if (interfaceName.equals("eth0")) {
							Enumeration<InetAddress> enumIpAddr = networkInterface
									.getInetAddresses();
							while (enumIpAddr.hasMoreElements()) {
								// 返回枚举集合中的下一个IP地址信息
								InetAddress inetAddress = enumIpAddr.nextElement();
								// 不是回环地址，并且是ipv4的地址
								if (!inetAddress.isLoopbackAddress()
										&& inetAddress instanceof Inet4Address) {
									Log.i("tag", inetAddress.getHostAddress() + "   ");

									return inetAddress.getHostAddress();
								}
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
					return null;
				}
			}

		} else {
			//当前无网络连接,请在设置中打开网络
		}
		return null;
	}

	/**
	 * 将得到的int类型的IP转换为String类型
	 *
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				(ip >> 24 & 0xFF);
	}

	/**
	 * 修改菜品列表里的临时cost
	 *
	 * @param marketObject
	 */
	public static void setDishItemMarket(MarketObject marketObject) {
		int disCount = 0;
		for (Dish dish : Cart.getDishItemList()) {
			if (dish.isParticipationDisCount()) {
				disCount += dish.getQuantity();
			}
		}
		//每份要减的钱
		BigDecimal eachSubtractMoney = marketObject.getReduceCash()
				.divide(new BigDecimal(disCount), 3, BigDecimal.ROUND_HALF_UP);
		for (Dish dish : Cart.getDishItemList()) {
			if (dish.isParticipationDisCount()) {
				dish.tempMarketList = null;
				if (dish.getTempMarketList() == null) {
					dish.tempMarketList = new ArrayList<>();
				}
				MarketObject marketObject2 = new MarketObject(marketObject
						.getMarketName(), eachSubtractMoney, MarketType.DISCOUNT);

				dish.setTempCost(dish.getOrderDishCost()
						.subtract(eachSubtractMoney));//用临时菜品COST，主要是用来存储参与打折后的单品价格 为了可以多次选择
				dish.getTempMarketList().add(marketObject2);
			}
		}
	}

	//判断手机格式是否正确
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	//判断email格式是否正确
	public static boolean isEmail(String email) {
		String  str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p   = Pattern.compile(str);
		Matcher m   = p.matcher(email);

		return m.matches();
	}


	public static String completionOrderId(String orderId) {
		if (!TextUtils.isEmpty(orderId)) {
			int strSize = orderId.length();
			if (strSize >= 5) {
				return orderId;
			} else {
				int          completionStrSize = 5 - strSize;//需要补全0的个数
				StringBuffer sb                = new StringBuffer();
				for (int i = 0; i < completionStrSize; i++) {
					sb.append("0");
				}
				return sb.append(orderId).toString();

			}
		}
		return "";
	}

	public static String ToSBC(String input) {
		//半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	public static byte[] ByteTo_byte(Vector<Byte> vector) {
		int    len  = vector.size();
		byte[] data = new byte[len];

		for (int i = 0; i < len; ++i) {
			data[i] = ((Byte) vector.get(i)).byteValue();
		}

		return data;
	}


	/**
	 * 删除菜品促销活动中指定的方案类型
	 *
	 * @param marketList
	 * @param marketType
	 */
	public static boolean removeItemForMarkType(List<MarketObject> marketList, MarketType marketType) {
		boolean isDelete = false;
		if (marketList != null && marketList.size() > 0)//如果有促销打折活动的话 要取打折后的菜品
		{
			int size = marketList.size();
			for (int i = 0; i < size; i++) {
				if (marketList.size() == i) {
					break;
				}
				boolean      isDeleteItem = false;
				MarketObject marketObject = marketList.get(i);
				if (marketObject != null) {
					if (marketObject.getMarketType() == marketType)//如果促销打折活动是全单打折
					{
						marketList.remove(marketObject);
						isDeleteItem = true;
						isDelete = true;
					}
					if (size == 1) {
						break;
					}
					if (isDeleteItem) {
						i -= 1;
					}
				}
			}
		}
		return isDelete;
	}

	public static void removeItemForSelectDish(List<OrderItem> orderItemList, boolean isDeleteState) {
		if (orderItemList != null && orderItemList.size() > 0)//如果有促销打折活动的话 要取打折后的菜品
		{
			int size = orderItemList.size();
			for (int i = 0; i < size; i++) {
				if (orderItemList.size() == i) {
					break;
				}
				boolean   isDeleteItem = false;
				OrderItem marketObject = orderItemList.get(i);
				if (marketObject != null) {
					if (marketObject.isSelectItem() == isDeleteState)//如果促销打折活动是全单打折
					{
						orderItemList.remove(marketObject);
						isDeleteItem = true;
					}
					if (size == 1) {
						break;
					}
					if (isDeleteItem) {
						i -= 1;
					}
				}
			}
		}
	}

	public static String replacePhone(String phone) {
		if (!TextUtils.isEmpty(phone)) {
			if (phone.length() == 11) {
				phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
			}
		}
		return phone;
	}

	/**
	 * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
	 *
	 * @param content  传入的字符串
	 * @param frontNum 保留前面字符的位数
	 * @param endNum   保留后面字符的位数
	 * @return 带星号的字符串
	 */

	public static String getStarString2(String content, int frontNum, int endNum) {
		if (!TextUtils.isEmpty(content)) {
			if (frontNum >= content.length() || frontNum < 0) {
				return content;
			}
			if (endNum >= content.length() || endNum < 0) {
				return content;
			}
			if (frontNum + endNum >= content.length()) {
				return content;
			}
			String starStr = "";
			for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
				starStr = starStr + "*";
			}
			return content.substring(0, frontNum) + starStr
					+ content.substring(content.length() - endNum, content.length());
		}
		return content;
	}

	public static List<Dish> removeDuplicate(List<Dish> dishList) {
		List<Dish> temp = new CopyOnWriteArrayList<>();
		for (int i = 0; i < dishList.size() - 1; i++) {
			for (int j = dishList.size() - 1; j > i; j--) {
				if (dishList.get(j).getDishId() == (dishList.get(i).getDishId())) {
					dishList.remove(j);
				}
			}
		}
		temp = dishList;
		return temp;
	}


	/**
	 * 通过XML StringName 返回标签内的具体内容
	 *
	 * @param xmlStringName
	 * @return
	 */
	public static String returnXMLStr(String xmlStringName) {
		String str = "";
		//        // string.xml内配置的ID
		//        int stringID = MyApplication.getInstance().getContext().getResources().getIdentifier(xmlStringName,// string.xml内配置的名字
		//                "string",
		//                "com.logic.managerpro");
		int stringID = getResId(xmlStringName, R.string.class);
		if (stringID > 0) {
			//string.xml内配置的具体内容
			str = MyApplication.getInstance().getContext().getResources().getString(stringID);
		}
		return str;
	}

	public static int getResId(String variableName, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(variableName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 通过打印机时间来判断打印机蜂鸣器是否鸣叫
	 *
	 * @param timeStr
	 * @return
	 */
	public static boolean logicPrintBuzzer(String timeStr) {
		boolean isBuzzer = false;
		try {
			isBuzzer = false;
			if (TextUtils.isEmpty(timeStr)) {
				return isBuzzer;
			} else {
				Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
				t.setToNow(); // 取得系统时间。  取出时间只有24小时模式.
				int    hour      = t.hour; // 0-23
				String timeArr[] = timeStr.split(",");
				int    size      = timeArr.length;
				for (int i = 0; i < size; i++) {
					String time = timeArr[i];
					if (!TextUtils.isEmpty(time)) {
						int tt = Integer.valueOf(time);
						if (tt == hour) {
							isBuzzer = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return isBuzzer;
	}

	public static String orderErrTips(Order order, long orderId) {
		String str    = "";
		String source = order.getSource();
		if (!TextUtils.isEmpty(source)) {
			if (source.equals("2")) {
				source = ToolsUtils.returnXMLStr("wx_eat_in");
			}
		} else {
			source = ToolsUtils.returnXMLStr("nofind_source");
		}
		String str1 = String.format(ToolsUtils.returnXMLStr("err_netorder_type"), source);
		String str2 = String.format(ToolsUtils.returnXMLStr("err_netorder_orderid"), orderId + "");
		str = str1 + str2;
		return str;
	}


}
