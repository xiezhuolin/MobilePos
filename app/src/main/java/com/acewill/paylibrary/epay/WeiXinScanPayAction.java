package com.acewill.paylibrary.epay;

import com.acewill.paylibrary.tencent.WXPay;
import com.acewill.paylibrary.tencent.business.ScanPayBusiness.ResultListener;
import com.acewill.paylibrary.tencent.common.Util;
import com.acewill.paylibrary.tencent.protocol.pay_protocol.ScanPayReqData;
import com.acewill.paylibrary.tencent.protocol.pay_protocol.ScanPayResData;
import com.acewill.paylibrary.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.acewill.paylibrary.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.acewill.paylibrary.tencent.protocol.refund_protocol.RefundReqData;
import com.acewill.paylibrary.tencent.protocol.refund_protocol.RefundResData;
import com.acewill.paylibrary.tencent.protocol.reverse_protocol.ReverseReqData;
import com.acewill.paylibrary.tencent.protocol.reverse_protocol.ReverseResData;
import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WeiXinScanPayAction {

	public static final int TRANS_UNKNOWN = -1;
	public static final int TRANS_CLOSED = 2;
	public static final int TRANS_OPEN = 3;
	public static final int USERPAYING = 4;

	private static String getLocalIP() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getHostAddress().toString();// 获得本机IP
		// return "192.168.2.4";
	}

	// 扫码支付(正扫码)
	public static EPayResult unifiedorder(String appId, String mchID,
										  String key, String out_tradeNo, int fenAmount, String body,
										  String sub_mch_id, String deviceInfo, String notify_url,
										  String trade_type, String product_id) {
		final EPayResult result = new EPayResult();

		try {
			// System.out.println("key = " + key + ",appid = " + appId
			// + "mch_id = " + mchID);

			WXPay.initSDKConfiguration(key, appId, mchID, sub_mch_id, "", "");

			String time_expire = DateUtils.getDateToStringForWX(System
					.currentTimeMillis() + 85 * 1000); // 90秒超时时间
			String timestamp = DateUtils.getDateToStringForWX(System
					.currentTimeMillis());

			// fenAmount = 1;//force to set real money to 1 fen
			String attachment = out_tradeNo;
			// public ScanPayReqData2(String body,String attach,String
			// outTradeNo,
			// int totalFee,String deviceInfo,String spBillCreateIP,String
			// timeStart
			// ,String timeExpire,String goodsTag, String notify_url, String
			// trade_type, String product_id){
			ScanPayReqData data = new ScanPayReqData(body, attachment,
					out_tradeNo, fenAmount, deviceInfo, getLocalIP(),
					timestamp, time_expire, "tag", notify_url, trade_type,
					product_id, sub_mch_id);
			final EPayResult resBuf[] = new EPayResult[] { result };

			synchronized (resBuf) {
				WXPay.doScanPayBusiness2(data, new ResultListener() {

					@Override
					public void onFail(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFail " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByAuthCodeExpire(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByAuthCodeExpire " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByAuthCodeInvalid(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByAuthCodeInvalid " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByMoneyNotEnough(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByMoneyNotEnough " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByQuerySignInvalid(
							ScanPayQueryResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByQuerySignInvalid " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReturnCodeError(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByReturnCodeError " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReturnCodeFail(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onFailByReturnCodeFail "
								+ arg0.getErr_code() + ","
								+ arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReverseSignInvalid(ReverseResData arg0) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("ReverseResData " + arg0);
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
						}

						// resBuf.notifyAll();
					}

					@Override
					public void onFailBySignInvalid(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailBySignInvalid " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onSuccess(ScanPayResData arg0, String arg1) {
						// TODO Auto-generated method stub
						result.setErrMessage(arg0.getReturn_msg());
						System.out.println("onSuccess " + arg0 + " res = "
								+ arg1);
						System.out
								.println("$%^$%^%$%%$^&*(&^%$#^$&*^(&^%$---------"
										+ arg0.getCode_url());

						synchronized (resBuf) {
							resBuf[0].success = true;
							resBuf[0].desc = arg0.getErr_code_des();
							// 微信扫码
							resBuf[0].qr_code = arg0.getCode_url();
							resBuf[0].touser = arg0.getOpenid();
							resBuf[0].transaction_id = arg0.getTransaction_id();
							resBuf[0].time_end = arg0.getTime_end();
							System.out.println("微信正扫:"+arg0.toString());
							resBuf.notifyAll();
						}

					}

				});

				resBuf.wait(1000);
			}
		} catch (Exception e) {
			// Util.log(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	// 刷卡支付(反扫码)
	public static EPayResult micropay(String appId, String mchID, String key,
									  String authCode, String out_tradeNo, int fenAmount, String body,
									  String sub_mch_id, String deviceInfo) {
		final EPayResult result = new EPayResult();
		try {
			System.out.println("key = " + key + ",appid = " + appId
					+ "mch_id = " + mchID);

			WXPay.initSDKConfiguration(key, appId, mchID, sub_mch_id, "", "");
			// 90秒超时时间
			String time_expire = DateUtils.getDateToStringForWX(System
					.currentTimeMillis() + 85 * 1000);//60

			String timestamp = DateUtils.getDateToStringForWX(System
					.currentTimeMillis());

			// fenAmount = 1;//force to set real money to 1 fen
			String attachment = out_tradeNo;
			ScanPayReqData data = new ScanPayReqData(authCode, body,
					attachment, out_tradeNo, fenAmount, deviceInfo,
					getLocalIP(), timestamp, time_expire, "tag", sub_mch_id);

			final EPayResult resBuf[] = new EPayResult[] { result };

			synchronized (resBuf) {
				WXPay.doScanPayBusiness(data, new ResultListener() {

					@Override
					public void onFail(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFail " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByAuthCodeExpire(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailByAuthCodeExpire " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByAuthCodeInvalid(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailByAuthCodeInvalid " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByMoneyNotEnough(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailByMoneyNotEnough " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByQuerySignInvalid(
							ScanPayQueryResData arg0) { // TODO Auto-generated
														// method stub
						System.out.println("onFailByQuerySignInvalid " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReturnCodeError(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailByReturnCodeError " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReturnCodeFail(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailByReturnCodeFail "
								+ arg0.getErr_code() + ","
								+ arg0.getReturn_msg());
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onFailByReverseSignInvalid(ReverseResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("ReverseResData " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
						}

						resBuf.notifyAll();
					}

					@Override
					public void onFailBySignInvalid(ScanPayResData arg0) {
						// TODO Auto-generated method stub
						System.out.println("onFailBySignInvalid " + arg0);
						result.setErrMessage(arg0.getReturn_msg());
						synchronized (resBuf) {
							resBuf[0].success = false;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf.notifyAll();
						}
					}

					@Override
					public void onSuccess(ScanPayResData arg0, String arg1) {
						// TODO Auto-generated method stub
						System.out.println("onSuccess " + arg0 + " res = "
								+ arg1);

						synchronized (resBuf) {
							resBuf[0].success = true;
							resBuf[0].desc = arg0.getErr_code_des();
							resBuf[0].transaction_id = arg0.getTransaction_id();
							resBuf[0].time_end = arg0.getTime_end();

							System.out.println("微信反扫:"+arg0.toString());
							resBuf.notifyAll();
						}

					}

				});

				resBuf.wait(1000);
			}
		} catch (Exception e) {
			// Util.log(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	// 注:myState 为支付状态 SUCCESS NOTPAY ...
	public static EPayResult query(String appId, String mchID, String key,
								   String authCode, String out_tradeNo, String sub_mch_id) {

		EPayResult ePayResult = new EPayResult();
		ePayResult.result = TRANS_UNKNOWN;
		try {
			WXPay.initSDKConfiguration(key, appId, mchID, sub_mch_id, "", "");

			ScanPayQueryReqData queryReq = new ScanPayQueryReqData(null,out_tradeNo);

			System.out.println("wx query send out_tradeNo : " + out_tradeNo);

			String resp = WXPay.requestScanPayQueryService(queryReq);

			System.out.println("wx query resp : " + resp);

			ScanPayQueryResData scanPayResData = (ScanPayQueryResData) Util
					.getObjectFromXML(resp, ScanPayQueryResData.class);

//			trade_state  所有状态
//			SUCCESS—支付成功
//			REFUND—转入退款
//			NOTPAY—未支付
//			CLOSED—已关闭
//			REVOKED—已撤销（刷卡支付）
//			USERPAYING--用户支付中
//			PAYERROR--支付失败(其他原因，如银行返回失败)
			
			
			if ("SUCCESS".equalsIgnoreCase(scanPayResData.getReturn_code())) {
				// CLOSED--已关闭
				// REVOKED--已撤销
				if ("CLOSED".equalsIgnoreCase(scanPayResData.getTrade_state())
						|| "REVOKED".equalsIgnoreCase(scanPayResData
								.getTrade_state())|| "PAYERROR".equalsIgnoreCase(scanPayResData
												.getTrade_state())) {
					ePayResult.result = TRANS_CLOSED;
				} else {
					ePayResult.result = TRANS_OPEN;

					ePayResult.trade_state = scanPayResData.getTrade_state();
					if ("USERPAYING".equals(scanPayResData.getErr_code())) {
						ePayResult.result = USERPAYING;
					}else{
						ePayResult.touser = scanPayResData.getOpenid();
						ePayResult.transaction_id = scanPayResData.getTransaction_id();
					}
				}

				if ("ORDERNOTEXIST".equalsIgnoreCase(scanPayResData
						.getErr_code())) {
					ePayResult.result = TRANS_CLOSED;

				}

			}
		} catch (Exception e) {
			// Util.log(e.getMessage());
			e.printStackTrace();
		}

		return ePayResult;
	}

	// 微信扫码支付--撤销订单
	public static int cancel(String appId, String mchID, String key,
			String authCode, String out_tradeNo, String sub_mch_id) {
		int result = TRANS_UNKNOWN;

		try {
			WXPay.initSDKConfiguration(key, appId, mchID, sub_mch_id, "", "");

			ReverseReqData cancelReq = new ReverseReqData(null, out_tradeNo);

			System.out.println("wx cancel send xml : "
					+ new Gson().toJson(cancelReq));

			String resp = WXPay.requestReverseService(cancelReq);

			System.out.println("wx cancel recv : " + resp);

			ReverseResData cancelResData = (ReverseResData) Util
					.getObjectFromXML(resp, ReverseResData.class);

			if ("SUCCESS".equalsIgnoreCase(cancelResData.getReturn_code())) {
				if ("SUCCESS".equalsIgnoreCase(cancelResData.getResult_code())) {
					result = TRANS_CLOSED;
				} else {
					result = TRANS_OPEN;
				}
			}
		} catch (Exception e) {
			// Util.log(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	public static int cancel(String authCode, String out_tradeNo) {
		return cancel(WXPay.APPID, WXPay.MCH_ID, WXPay.KEY, authCode,
				out_tradeNo, WXPay.SUB_MCH_ID);
	}

	// 微信扫码支付--申请退款
	/**
	 * 请求退款服务
	 * 
	 * @param transactionID
	 *            是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。
	 *            建议优先使用
	 * @param outTradeNo
	 *            商户系统内部的订单号,transaction_id 、out_trade_no
	 *            二选一，如果同时存在优先级：transaction_id>out_trade_no
	 * @param deviceInfo
	 *            微信支付分配的终端设备号，与下单一致
	 * @param outRefundNo
	 *            商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
	 * @param totalFee
	 *            订单总金额，单位为分
	 * @param refundFee
	 *            退款总金额，单位为分,可以做部分退款
	 * @param opUserID
	 *            操作员帐号, 默认为商户号
	 * @param refundFeeType
	 *            货币类型，符合ISO 4217标准的三位字母代码，默认为CNY（人民币）
	 */
	public static int refund(String appId, String mchID, String key,
			String sub_mch_id, String transactionID, String outTradeNo,
			String deviceInfo, String outRefundNo, int totalFee, int refundFee,
			String opUserID, String refundFeeType) {
		int result = TRANS_UNKNOWN;

		try {
			WXPay.initSDKConfiguration(key, appId, mchID, sub_mch_id, "", "");

			RefundReqData refundReq = new RefundReqData(transactionID,
					outTradeNo, deviceInfo, outRefundNo, totalFee, refundFee,
					opUserID, refundFeeType);

			System.out.println("wx refund send xml : "
					+ new Gson().toJson(refundReq));

			String resp = WXPay.requestRefundService(refundReq);

			System.out.println("wx refund recv : " + resp);

			RefundResData refundResData = (RefundResData) Util
					.getObjectFromXML(resp, RefundResData.class);

			if ("SUCCESS".equalsIgnoreCase(refundResData.getReturn_code())) {
				if ("SUCCESS".equalsIgnoreCase(refundResData.getResult_code())) {
					result = TRANS_CLOSED;
				} else {
					result = TRANS_OPEN;
				}
			}
		} catch (Exception e) {
			// Util.log(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

}
