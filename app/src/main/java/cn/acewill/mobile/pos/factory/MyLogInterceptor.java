package cn.acewill.mobile.pos.factory;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import cn.acewill.mobile.pos.utils.FileLog;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Administrator on 2018/11/6 0006.
 */

public class MyLogInterceptor implements Interceptor{
	private static String currentUrl;
	private static boolean urlChanged = false;

	public static void setBaseUrl(String baseUrl) {
		currentUrl = baseUrl;
		urlChanged = true;
	}
	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		//获得请求信息，此处如有需要可以添加headers信息
		Request request = chain.request();
		//添加Cookie信息
		StringBuilder sb = new StringBuilder();
		//打印请求信息
		String query = request.url().encodedQuery();
		String path = request.url().encodedPath();

		// if (currentUrl != null && urlChanged) {
		request = request.newBuilder()
				.url(currentUrl + path + "?" + query)
				.build();
		urlChanged = false;




		Log.d("Api", "url:" + request.url());
		sb.append("url:" + request.url() + "\n");
		Log.d("Api", "method:" + request.method());
		sb.append("method:" + request.method() + "\n");
		Log.d("Api", "request-body:" + request.body());
		sb.append("request-body:" + request.body() + "\n");
		//记录请求耗时
		long             startNs = System.nanoTime();
		okhttp3.Response response;
		try {
			//发送请求，获得相应，
			response = chain.proceed(request);
		} catch (Exception e) {
			throw e;
		}
		long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
		//打印请求耗时
		Log.d("Api", "耗时:" + tookMs + "ms");
		sb.append("耗时:" + tookMs + "ms" + "\n");
		//使用response获得headers(),可以更新本地Cookie。
		Log.d("Api", "headers==========");
		sb.append("headers==========" + "\n");
		Headers headers = response.headers();
		Log.d("Api", headers.toString());
		sb.append(headers.toString() + "\n");
		//获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
		ResponseBody responseBody = response.body();

		//为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
		BufferedSource source = responseBody.source();
		source.request(Long.MAX_VALUE); // Buffer the entire body.
		//获得返回的数据
		Buffer buffer = source.buffer();
		//使用前clone()下，避免直接消耗
		Log.d("Api", "response:" + buffer.clone().readString(Charset.forName("UTF-8")));
		sb.append("response:" + buffer.clone().readString(Charset.forName("UTF-8")) + "\n");

		String sUrl = request.url().toString();
		//			!sUrl.contains("terminal") && !sUrl.contains("getMemberInfo") && !sUrl
		//					.contains("getAllTemplates") && !sUrl.contains("getPrinters") && !sUrl
		//					.contains("getKichenStalls") && !sUrl.contains("getKDSes") && !sUrl
		//					.contains("getStoreConfiguration") &&

		if (sUrl
				.contains("data.action") || sUrl.contains("terminal/dishmenu") || sUrl
				.contains("terminal/dishKind") || sUrl
				.contains("terminal/paytypes") || sUrl
				.contains("getPrinters") || sUrl
				.contains("terminal/getSelfposConfiguration") || sUrl
				.contains("terminal/getOtherfiles") || sUrl
				.contains("terminal/market") || sUrl
				.contains("getKichenStalls")
				|| (sUrl.contains("getMemberInfo")) || sUrl
				.contains("downloadSqliteFile") || (sUrl.contains("today"))) {
			FileLog
					.log("Res", "", "onResponse", "", "url>" + sUrl + "请求成功" + "\n");
		} else if (sUrl.contains("test/heartbeat") || sUrl
				.contains("printTemplate/getAllTemplates") || sUrl
				.contains("terminal/dishCounts") || sUrl
				.contains("orderdiscount/getOrderDiscountTypes") || sUrl
				.contains("terminal/logo") || sUrl.contains("terminal/getAllDishmenu")|| sUrl.contains("orders/reason")|| sUrl.contains("store_operation/orderItemOnWork")) {

		} else {
			FileLog.log("Api", RetrofitFactory.class, "intercept", "log", sb.toString());
		}
		return response;
	}
}
