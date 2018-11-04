package com.acewill.paylibrary.epay;

import android.os.SystemClock;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.alipay.util.httpClient.HttpProtocolHandler;
import com.acewill.paylibrary.alipay.util.httpClient.HttpRequest;
import com.acewill.paylibrary.alipay.util.httpClient.HttpResponse;
import com.acewill.paylibrary.alipay.util.httpClient.HttpResultType;
import com.google.gson.Gson;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class AliBarPayAction {

	// ///////////////////////////////////////////////////////////////////////////

	private static final String ALIPAY_GATE_WAY = "https://openapi.alipay.com/gateway.do?charset=utf-8";
	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?charset=utf-8";

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * 
	 * @param properties
	 *            MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(),
					entry.getValue());
		}

		return nameValuePair;
	}

	public static EPayResult micropay(String appId, String pkcs8Key,
									  String out_trade_no, String auth_code, String total_amount,
									  String subject, List<AliGoodsItem> items, String storeId,
									  String terminalId) throws HttpException, IOException {
		return AliPayMethod(appId, pkcs8Key, out_trade_no, auth_code,
				total_amount, subject, items, storeId, terminalId, false);
	}

	public static EPayResult pay(String appId, String pkcs8Key,
								 String out_trade_no, String total_amount, String subject,
								 List<AliGoodsItem> items, String storeId, String terminalId)
			throws HttpException, IOException {
		return AliPayMethod(appId, pkcs8Key, out_trade_no, total_amount,
				subject, items, storeId, terminalId, false);
	}

	// 反扫
	public static EPayResult AliPayMethod(String appId, String pkcs8Key,
										  String out_trade_no, String auth_code, String total_amount,
										  String subject, List<AliGoodsItem> items, String storeId,
										  String terminalId, boolean bUseApatchSigner) throws HttpException,
			IOException {
		// TODO Auto-generated method stub

		EPayResult ePayResult = new EPayResult();

		String time_expire = DateUtils.getDateToString(System
				.currentTimeMillis() + 85 * 1000); // 3分钟超时时间
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间
		System.out.printf("当前时间：" + timestamp + "\n" + "超时时间：" + time_expire
				+ "\n");
		//
		// String paramStr = "app_id="
		// + appId
		// + "&biz_content="// "2015050800065426&biz_content="
		// + "{\"out_trade_no\":\""
		// + out_trade_no
		// + "\",\"scene\":\"bar_code\",\"auth_code\":\""
		// + auth_code
		// + "\",\"seller_id"
		// + "\":\"\",\"total_amount\":" + total_amount + ","
		// + "\"subject\":\"" + subject + "\","
		// //+ "\"goods_detail\":\"" + getJSONString(items) + "\","
		// + "\"body\":\"餐饮消费\","
		// + "\"operator_id\":1,"
		// //+ "\"store_id\":\" " + storeId + "\","
		// //+ "\"terminal_Id\":\"" + terminalId +"\","
		// + "time_expire\":\"" + time_expire
		// +
		// "\"}&charset=utf-8&method=alipay.trade.pay&sign_flag=true&sign_type="
		// + "RSA&timestamp=" + timestamp + "&version=1.0";

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="// "2015050800065426&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\",\"scene\":\"bar_code\","
				+ "\"auth_code\":\""+ auth_code+ "\","
				+ "\"seller_id\":\"\","
				+ "\"total_amount\":"+ total_amount
				+ ",\"subject\":\""+ subject
				+ "\","
				+ "\"goods_detail\":"+ getJSONString(items)+ ","
				+ "\"body\":\"餐饮消费\","
				+ "\"operator_id\":1,"
				+ "\"store_id\":\""+ storeId+ "\","
				+ "\"terminal_Id\":\""+ terminalId+ "\","
				+ "\"time_expire\":\""+ time_expire
				+ "\"}&charset=utf-8&method=alipay.trade.pay&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = "";

		if (bUseApatchSigner) {
			sign = RSASignatureApache.sign(paramStr, pkcs8Key);
		} else {
			sign = RSASignature.sign(paramStr, pkcs8Key);
		}
		
		if(sign == null){
			return null;
		}


		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		System.out.println("ali pay response = " + strResult);

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_pay_response");

			ePayResult.desc = respJsonObj.optString("sub_msg");

			ePayResult.code = respJsonObj.optString("code");
			ePayResult.qr_code = respJsonObj.optString("qr_code");
			ePayResult.trade_no = respJsonObj.optString("trade_no");
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			}
			//返回支付处理中  需要轮训去查该笔订单的支付情况
			if (EPayResult.PAY_CODE_IN_PROGRESS.equals(ePayResult.code)) {
				ePayResult = queryAliResult(paramMap,false);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ePayResult;
	}
	/**
	 * 
	 * @param content 内容
	 * @param touser openid
	 * @param url url
	 */
	public static void senMsgToWx(String content,String touser,String url){
		HashMap<String, String> hashMap = new HashMap<String,String>();
		hashMap.put("content", content);
		Gson gson = new Gson();
		String json = gson.toJson(hashMap);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("touser", touser);
		paramMap.put("msgtype", "text");
		paramMap.put("text", json);
		
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(url);

		try {
			HttpResponse response = httpProtocolHandler.execute(request, "", "");
			System.out.println("微信推送result："+response.getStringResult());
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 正扫
	 * 
	 * @param appId
	 * @param pkcs8Key
	 * @param out_trade_no
	 * @param total_amount
	 * @param subject
	 * @param items
	 * @param storeId
	 * @param terminalId
	 * @param bUseApatchSigner
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static EPayResult AliPayMethod(String appId, String pkcs8Key,
										  String out_trade_no, String total_amount, String subject,
										  List<AliGoodsItem> items, String storeId, String terminalId,
										  boolean bUseApatchSigner) throws HttpException, IOException {
		// TODO Auto-generated method stub

		EPayResult ePayResult = new EPayResult();

		String time_expire = DateUtils.getDateToString(System
				.currentTimeMillis() + 85 * 1000); // 3分钟超时时间
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间
		// System.out.printf("当前时间：" + timestamp + "\n" + "超时时间：" + time_expire
		// + "\n");

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="// "2015050800065426&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\","
				+ "\"seller_id\":\"\","
				+ "\"total_amount\":"
				+ total_amount
				+ ",\"subject\":\""
				+ subject
				+ "\","
				+ "\"goods_detail\":"
				+ getJSONString(items)
				+ ","
				+ "\"body\":\"餐饮消费\","
				+ "\"operator_id\":1,"
				+ "\"store_id\":\""
				+ storeId
				+ "\","
				+ "\"terminal_Id\":\""
				+ terminalId
				+ "\","
				+ "\"time_expire\":\""
				+ time_expire
				+ "\"}&charset=utf-8&method=alipay.trade.precreate&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = "";

		if (bUseApatchSigner) {
			sign = RSASignatureApache.sign(paramStr, pkcs8Key);
		} else {
			sign = RSASignature.sign(paramStr, pkcs8Key);
		}

		// System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		// System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			// System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		// System.out.println("ali pay response = " + strResult );

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			// 反扫支付为 alipay_trade_pay_response
			// 正扫支付为 alipay_trade_precreate_response
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_precreate_response");

			// 此处拿到正扫二维码字符串
			String qr_code = respJsonObj.optString("qr_code");
			// System.out.println("*****正扫二维码字符串*********"+qr_code);

			ePayResult.desc = respJsonObj.optString("msg");

			ePayResult.code = respJsonObj.optString("code");
			ePayResult.qr_code = qr_code;
			ePayResult.trade_no = respJsonObj.optString("trade_no");
			
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			}
			//返回支付处理中  需要轮训去查该笔订单的支付情况
			if (EPayResult.PAY_CODE_IN_PROGRESS.equals(ePayResult.code)) {
				ePayResult = queryAliResult(paramMap,true);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ePayResult;
	}

	/**
	 * isZs 是否是正扫
	 * @param paramMap
	 * @param isZs
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	private static EPayResult queryAliResult(Map<String, String> paramMap,boolean isZs)throws HttpException, IOException
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		EPayResult ePayResult = new EPayResult();
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		// System.out.println("ali pay response = " + strResult );

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			// 反扫支付为 alipay_trade_pay_response
			// 正扫支付为 alipay_trade_precreate_response
			if(isZs)
			{
				respJsonObj = respJsonObj
						.optJSONObject("alipay_trade_precreate_response");
			}
			else{
				respJsonObj = respJsonObj
						.optJSONObject("alipay_trade_pay_response");
			}

			// 此处拿到正扫二维码字符串
			String qr_code = respJsonObj.optString("qr_code");
			// System.out.println("*****正扫二维码字符串*********"+qr_code);

			ePayResult.desc = respJsonObj.optString("msg");

			ePayResult.code = respJsonObj.optString("code");
			ePayResult.qr_code = qr_code;
			ePayResult.trade_no = respJsonObj.optString("trade_no");

			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			}
			//返回支付处理中  需要轮训去查该笔订单的支付情况
			if (EPayResult.PAY_CODE_IN_PROGRESS.equals(ePayResult.code)) {
				queryAliResult(paramMap,isZs);
			}
			//PAY_CODE_IN_ABNORMAL 返回支付异常     PAY_CODE_FALSE返回支付错误
			if (EPayResult.PAY_CODE_IN_ABNORMAL.equals(ePayResult.code) || EPayResult.PAY_CODE_FALSE.equals(ePayResult.code)) {
				ePayResult.success = false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ePayResult;
	}

	protected static EPayResult StartWaitingForPayCompletion(String out_trade_no) {
		// TODO Auto-generated method stub

		EPayResult result = new EPayResult();

		try {
			long start = System.currentTimeMillis();
			SystemClock.sleep(1000);
			while (System.currentTimeMillis() - start < 2 * 60 * 1000)// 20
			// seconds
			{
				SystemClock.sleep(1000);
				result = AliBarPayAction.aliCheckPayStatus(AlipayConfig.APPID,
						AlipayConfig.key, out_trade_no);
				if (EPayResult.PAY_STATUS_COMPLETE
						.equalsIgnoreCase(result.trade_status)) {
					result.success = true;
					return result;
				} else if (EPayResult.PAY_STATUS_WAIT.equalsIgnoreCase(result.trade_status)) {
					continue;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}


	private static String getJSONString(List<AliGoodsItem> items) {
		// TODO Auto-generated method stub
		return new Gson().toJson(items);
	}

	public static EPayResult aliCheckPayStatus(String appId, String pkcs8Key,
											   String out_trade_no) throws HttpException, IOException {
		// TODO Auto-generated method stub
		EPayResult ePayResult = new EPayResult();
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\"}&charset=utf-8&method=alipay.trade.query&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = RSASignature.sign(paramStr, pkcs8Key);

		// System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		// System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");
			if(as.length <= 1)
			{
				ePayResult.setErrMessage("支付宝参数配置有误");
				ePayResult.success = false;
				return ePayResult;
			}

			paramMap.put(as[0], as[1]);

			// System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		// System.out.println("ali query response = " + strResult );

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_query_response");

			ePayResult.desc = respJsonObj.optString("msg");
			ePayResult.trade_status = respJsonObj.optString("trade_status");
			System.out.println("////////////\ntrade_status: "
					+ ePayResult.trade_status);
			ePayResult.code = respJsonObj.optString("code");
			ePayResult.trade_no = respJsonObj.optString("trade_no");
			
			System.out.println("支付宝正扫:"+respJsonObj.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ePayResult;
	}

	public static EPayResult cancelPayment(String appId, String pkcs8Key,
										   String out_trade_no) throws HttpException, IOException {
		// TODO Auto-generated method stub
		EPayResult ePayResult = new EPayResult();
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\"}&charset=utf-8&method=alipay.trade.close&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = RSASignature.sign(paramStr, pkcs8Key);

		System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		System.out.println("ali cancel response = " + strResult);

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_cancel_response");

			ePayResult.desc = respJsonObj.optString("msg");

			ePayResult.code = respJsonObj.optString("code");
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ePayResult;
	}

}
