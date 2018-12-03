package cn.acewill.mobile.pos.factory;

import android.text.Annotation;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.service.PosInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
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


//		builder.addInterceptor(new HostSelectionInterceptor());
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
