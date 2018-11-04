package cn.acewill.mobile.pos.factory;

import android.text.Annotation;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.utils.FileLog;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Acewill on 2016/5/24.
 */
public class RetrofitFactory {
	public static <T> T buildService(Class<T> serviceClass) throws PosServiceException {
		String serverUrl = PosInfo.getInstance().getServerUrl();
		if (serverUrl == null) {
			//创建service时允许服务器地址是空，因为有些service需要在用户登录前创建，那时候服务器地址还没有设置
			serverUrl = "http://www.smarant.com";
			PosInfo.getInstance().setServerUrl(serverUrl);
		}

		return buildService(serverUrl, serviceClass);
	}

	public static <T> T buildService(String baseUrl, Class<T> serviceClass) {

		//        HttpLoggingInterceptor bodyInterceptor = new HttpLoggingInterceptor();
		//        bodyInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //在日志中打印http消息体(同时也会打印消息头)bodyInterceptor

		//        SSLContext sslContext = setCertificates(MyApplication.getInstance().getClass().getClassLoader().getResourceAsStream("assets/acewill.cer"));

		OkHttpClient.Builder builder = new OkHttpClient.Builder().retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);


		builder.addInterceptor(new HostSelectionInterceptor());
		builder.addInterceptor(new MyLogInterceptor());
		//        builder.addInterceptor(bodyInterceptor);
		//  builder.sslSocketFactory(sslContext.getSocketFactory());

		//登录成功后，通过CookieInterceptor设置cookie
		builder.addNetworkInterceptor(new CookieInterceptor());

		//如果想在所有的请求上加上header之类的，可以自己加一个addNetworkInterceptor

		OkHttpClient client = builder.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(client)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(new Gson()))
				.build();

		return (T) retrofit.create(serviceClass);
	}

	private static class MyLogInterceptor implements Interceptor {

		@Override
		public Response intercept(Chain chain) throws IOException {
			//获得请求信息，此处如有需要可以添加headers信息
			Request request = chain.request();
			//添加Cookie信息
			StringBuilder sb = new StringBuilder();
			//打印请求信息
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

	public static <T> T buildKdsService(String baseUrl, Class<T> serviceClass) {

		HttpLoggingInterceptor bodyInterceptor = new HttpLoggingInterceptor();
		bodyInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //在日志中打印http消息体(同时也会打印消息头)

		OkHttpClient.Builder builder = new OkHttpClient.Builder().retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS);

		builder.addInterceptor(bodyInterceptor);

		OkHttpClient client = builder.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(client)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(new Gson()))
				.build();

		return (T) retrofit.create(serviceClass);
	}

	public static SSLContext setCertificates(InputStream... certificates) {
		SSLContext sslContext = null;
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			KeyStore           keyStore           = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				keyStore.setCertificateEntry(certificateAlias, certificateFactory
						.generateCertificate(certificate));

				try {
					if (certificate != null)
						certificate.close();
				} catch (IOException e) {
				}
			}

			sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory =
					TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(keyStore);
			sslContext.init
					(
							null,
							trustManagerFactory.getTrustManagers(),
							new SecureRandom()
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sslContext;
	}

	static class FileRequestBodyConverterFactory extends Converter.Factory {

		public Converter<File, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
			return new FileRequestBodyConverter();
		}
	}

	static class FileRequestBodyConverter implements Converter<File, RequestBody> {

		@Override
		public RequestBody convert(File file) throws IOException {
			return RequestBody.create(MediaType.parse("application/otcet-stream"), file);
		}
	}

}
