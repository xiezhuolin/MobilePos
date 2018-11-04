package com.acewill.paylibrary.tencent.common;

import com.acewill.paylibrary.tencent.common.http.HttpProtocolHandler;
import com.acewill.paylibrary.tencent.common.http.HttpRequest;
import com.acewill.paylibrary.tencent.common.http.HttpResponse;
import com.acewill.paylibrary.tencent.common.http.HttpResultType;
import com.acewill.paylibrary.tencent.service.IServiceRequest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * User: rizenguo Date: 2014/10/29 Time: 14:36
 */
public class HttpsRequest implements IServiceRequest {

	public interface ResultListener {

		public void onConnectionPoolTimeoutError();

	}

	private static Log log = new Log(
			LoggerFactory.getLogger(HttpsRequest.class));

	// 表示请求器是否已经做了初始化工作
	private boolean hasInit = false;

	// 连接超时时间，默认10秒
	private int socketTimeout = 10000;

	// 传输超时时间，默认30秒
	private int connectTimeout = 30000;

	public HttpsRequest() throws UnrecoverableKeyException,
			KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, IOException {
		init();
	}

	private void init() throws IOException, KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyManagementException {
		hasInit = true;
	}

	/**
	 * 通过Https往API post xml数据
	 * 
	 * @param url
	 *            API地址
	 * @param xmlObj
	 *            要提交的XML数据对象
	 * @return API回包的实际数据
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */

	public String sendPost(String url, Object xmlObj) throws IOException,
			KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyManagementException {

		if (!hasInit) {
			init();
		}

		String result = null;

		HttpRequest req = new HttpRequest(HttpResultType.BYTES);

		// 解决XStream对出现双下划线的bug
		XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8"));

		// 将要提交给API的数据对象转换成XML格式数据Post给API
		/*
		 * <com.tencent.protocol.pay_protocol.ScanPayReqData>
		 * <appid>wx9ee7ec62fba9e913</appid> <mch_id>1238985402</mch_id>
		 * <device_info>simudevice</device_info>
		 * <nonce_str>4oc7dgvln476296dmwcax2xhw18q1tri</nonce_str>
		 * <sign>51F5E6459DDD75CB61A5CA68651E520C</sign> <body>body_test</body>
		 * <attach>attach_test</attach> <out_trade_no>out_tradeNo</out_trade_no>
		 * <total_fee>1</total_fee>
		 * <spbill_create_ip>192.168.1.103</spbill_create_ip>
		 * <time_start>20150906173259</time_start>
		 * <time_expire>20150906182900</time_expire> <goods_tag>tag</goods_tag>
		 * <auth_code>130234698158833199</auth_code> <sdk_version>java sdk
		 * 1.0.1</sdk_version>
		 * </com.tencent.protocol.pay_protocol.ScanPayReqData>
		 */
		// ScanPayReqData reqData = (ScanPayReqData) xmlObj;

		String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

		postDataXML = postDataXML.replace("__", "_");
		/*
		 * String postDataXML =
		 * "<com.tencent.protocol.pay_protocol.ScanPayReqData>\n" + "<appid>" +
		 * reqData.getAppid() + "</appid>\n" + "<mch_id>" +
		 * reqData.getMch_id()+"</mch_id>\n" + "<device_info>" +
		 * reqData.getDevice_info() +"</device_info>\n" + "<nonce_str>" +
		 * reqData.getNonce_str() + "</nonce_str>\n" + "<sign>" +
		 * reqData.getSign() + "</sign>\n" + "<body>" + reqData.getBody() +
		 * "</body>\n" + "<attach>" + reqData.getAttach() + "</attach>\n" +
		 * "<out_trade_no>" + reqData.getOut_trade_no() + "</out_trade_no>\n" +
		 * "<total_fee>" + reqData.getTotal_fee() + "</total_fee>\n" +
		 * "<spbill_create_ip>" + reqData.getSpbill_create_ip() +
		 * "</spbill_create_ip>\n" + "<time_start>" + reqData.getTime_start() +
		 * "</time_start>\n" + "<time_expire>" + reqData.getTime_expire() +
		 * "</time_expire>\n" + "<goods_tag>"+reqData.getGoods_tag() +
		 * "</goods_tag>\n" + "<auth_code>" + reqData.getAuth_code() +
		 * "</auth_code>\n" + "<sdk_version>java sdk 1.0.1</sdk_version>\n" +
		 * "</com.tencent.protocol.pay_protocol.ScanPayReqData>";
		 */

		Util.log("API，POST过去的数据是：");
		Util.log(postDataXML);

		System.out.println(postDataXML);
		// 证书

		// /////
		req.setQueryString(postDataXML);

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset("utf-8");
		request.setUrl(url);
		request.setMethod(HttpRequest.METHOD_POST);
		request.setQueryString(postDataXML);
		// if(url.equals(Configure.REVERSE_API)){
		// try {
		// HttpPost httpPost = new HttpPost(url);
		// httpPost.setEntity(new StringEntity(postDataXML, "UTF-8"));
		// httpPost.setHeader("Accept", "application/json");
		// httpPost.setHeader("Content-type", "application/json");
		// HttpClient httpClient = getSSL();
		// org.apache.http.HttpResponse execute = httpClient.execute(httpPost);
		// // CloseableHttpResponse execute = httpClient.execute(httpPost);
		// return execute.toString();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return "";
		}

		result = response.getStringResult();

		return result;
	}

	/**
	 * 设置连接超时时间
	 * 
	 * @param socketTimeout
	 *            连接时长，默认10秒
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		resetRequestConfig();
	}

	/**
	 * 设置传输超时时间
	 * 
	 * @param connectTimeout
	 *            传输时长，默认30秒
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		resetRequestConfig();
	}

	/**
	 * 允许商户自己做更高级更复杂的请求器配置
	 * 
	 */
	private void resetRequestConfig() {
		// requestConfig =
		// RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
	}

	// public void setRequestConfig(RequestConfig requestConfig) {
	// requestConfig = requestConfig;
	// }

	public static class SSLSocketFactoryImp extends SSLSocketFactory {
		final SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryImp(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
