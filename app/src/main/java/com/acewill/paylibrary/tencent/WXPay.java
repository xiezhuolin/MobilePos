package com.acewill.paylibrary.tencent;

import com.acewill.paylibrary.tencent.business.ScanPayBusiness;
import com.acewill.paylibrary.tencent.business.ScanPayBusiness.ResultListener;
import com.acewill.paylibrary.tencent.common.Configure;
import com.acewill.paylibrary.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.acewill.paylibrary.tencent.protocol.pay_protocol.ScanPayReqData;
import com.acewill.paylibrary.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.acewill.paylibrary.tencent.protocol.refund_protocol.RefundReqData;
import com.acewill.paylibrary.tencent.protocol.refund_query_protocol.RefundQueryReqData;
import com.acewill.paylibrary.tencent.protocol.reverse_protocol.ReverseReqData;
import com.acewill.paylibrary.tencent.service.DownloadBillService;
import com.acewill.paylibrary.tencent.service.RefundQueryService;
import com.acewill.paylibrary.tencent.service.RefundService;
import com.acewill.paylibrary.tencent.service.ReverseService;
import com.acewill.paylibrary.tencent.service.ScanPayQueryService;
import com.acewill.paylibrary.tencent.service.ScanPayService;

/**
 * SDK总入口
 */
public class WXPay {
	// 青椒红椒
	// public final static String APPID = "wx62ee54639b17bb44";
	// public final static String KEY = "123456abcdefghijklmnopqrstuvwxyz";
	// public final static String MCH_ID = "1291271201";
	// 餐行健
//	 public final static String APPID = "wx9ee7ec62fba9e913";
//	 public final static String KEY = "abcdefghijklmn123456789012345678";
//	 public final static String MCH_ID = "1238985402";
	// 有饭
//	 public  static String APPID = "wx0c7980362d47d2ff";
//	 public  static String KEY = "vhavefun365201506211abcdefg54321";
//	 public  static String MCH_ID = "1226350702";
//	 public  static String SUB_MCH_ID = "";
	
	// 开饭(茶里)
//	public  static String APPID = "wx62536a0aca6aded0";
//	public  static String KEY = "o7lTUl0H3fjbetbSaHTNat6ftetSMFNT";
//	public  static String MCH_ID = "1231987202";
//	public  static String APPSECRET = "48d2b9a93b245fcec086b9c3826dbade";
	
	//深圳测试账号
//	public  static String APPID = "wx56e21a3b3e9e2f2e";
//	public  static String KEY = "abcdefghijklmnopqrstuvwxyz123456";
//	public  static String MCH_ID = "1345671601";
//	public  static String APPSECRET = "7011218d5ecbec25a9224fcba7b0dec7";

	public  static String APPID = "";
	public  static String KEY = "";
	public  static String MCH_ID = "";
	public  static String APPSECRET = "";
	
	public  static String SUB_MCH_ID = "";
	public  static String mch_name = "test";

	// wei lao xiang..agent — 魏老香受理模式下的微信账户(代理方为餐行健)
	// public final static String APPID = "wx41a078b5d822fa29";
	// public final static String KEY = "acewillacewillacewillacewill1234";
	// public final static String MCH_ID = "1241266502";
	// public final static String SUB_MCH_ID = "1254740901";
	// public final static String mch_name = "testAgent";

	/**
	 * 初始化SDK依赖的几个关键配置
	 * 
	 * @param key
	 *            签名算法需要用到的秘钥
	 * @param appID
	 *            公众账号ID
	 * @param mchID
	 *            商户ID
	 * @param sdbMchID
	 *            子商户ID，受理模式必填
	 * @param certLocalPath
	 *            HTTP证书在服务器中的路径，用来加载证书用
	 * @param certPassword
	 *            HTTP证书的密码，默认等于MCHID
	 */
	public static void initSDKConfiguration(String key, String appID,
			String mchID, String sdbMchID, String certLocalPath,
			String certPassword) {
		Configure.setKey(key);
		Configure.setAppID(appID);
		Configure.setMchID(mchID);
		Configure.setSubMchID(sdbMchID);
		Configure.setCertLocalPath(certLocalPath);
		Configure.setCertPassword(certPassword);
	}

	/**
	 * 请求支付服务
	 * 
	 * @param scanPayReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的数据
	 * @throws Exception
	 */
	public static String requestScanPayService(ScanPayReqData scanPayReqData)
			throws Exception {
		return new ScanPayService().request(scanPayReqData);
	}

	/**
	 * 请求支付查询服务
	 * 
	 * @param scanPayQueryReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	public static String requestScanPayQueryService(
			ScanPayQueryReqData scanPayQueryReqData) throws Exception {
		return new ScanPayQueryService().request(scanPayQueryReqData);
	}

	/**
	 * 请求退款服务
	 * 
	 * @param refundReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	public static String requestRefundService(RefundReqData refundReqData)
			throws Exception {
		return new RefundService().request(refundReqData);
	}

	/**
	 * 请求退款查询服务
	 * 
	 * @param refundQueryReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	public static String requestRefundQueryService(
			RefundQueryReqData refundQueryReqData) throws Exception {
		return new RefundQueryService().request(refundQueryReqData);
	}

	/**
	 * 请求撤销服务
	 * 
	 * @param reverseReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	public static String requestReverseService(ReverseReqData reverseReqData)
			throws Exception {
		return new ReverseService().request(reverseReqData);
	}

	/**
	 * 请求对账单下载服务
	 * 
	 * @param downloadBillReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	public static String requestDownloadBillService(
			DownloadBillReqData downloadBillReqData) throws Exception {
		return new DownloadBillService().request(downloadBillReqData);
	}

	/**
	 * 直接执行被扫支付业务逻辑（包含最佳实践流程）
	 * 
	 * @param scanPayReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @param resultListener
	 *            商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
	 * @throws Exception
	 */
	// 扫码支付(正扫)
	public static void doScanPayBusiness2(ScanPayReqData scanPayReqData,
			ResultListener resultListener) throws Exception {
		new ScanPayBusiness().run2(scanPayReqData, resultListener);
	}

	// 刷卡支付（反扫）
	public static void doScanPayBusiness(ScanPayReqData scanPayReqData,
			ScanPayBusiness.ResultListener resultListener) throws Exception {
		new ScanPayBusiness().run(scanPayReqData, resultListener);
	}

	/**
	 * 调用退款业务逻辑
	 * 
	 * @param refundReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @param resultListener
	 *            业务逻辑可能走到的结果分支，需要商户处理
	 * @throws Exception
	 */
	// public static void doRefundBusiness(RefundReqData refundReqData,
	// RefundBusiness.ResultListener resultListener) throws Exception {
	// new RefundBusiness().run(refundReqData,resultListener);
	// }

	/**
	 * 运行退款查询的业务逻辑
	 * 
	 * @param refundQueryReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @param resultListener
	 *            商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
	 * @throws Exception
	 */
	// public static void doRefundQueryBusiness(RefundQueryReqData
	// refundQueryReqData,RefundQueryBusiness.ResultListener resultListener)
	// throws Exception {
	// new RefundQueryBusiness().run(refundQueryReqData,resultListener);
	// }

	/**
	 * 请求对账单下载服务
	 * 
	 * @param downloadBillReqData
	 *            这个数据对象里面包含了API要求提交的各种数据字段
	 * @param resultListener
	 *            商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
	 * @return API返回的XML数据
	 * @throws Exception
	 */
	// public static void doDownloadBillBusiness(DownloadBillReqData
	// downloadBillReqData,DownloadBillBusiness.ResultListener resultListener)
	// throws Exception {
	// new DownloadBillBusiness().run(downloadBillReqData,resultListener);
	// }

}
