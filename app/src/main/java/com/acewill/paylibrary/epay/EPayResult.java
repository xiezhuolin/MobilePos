package com.acewill.paylibrary.epay;

public class EPayResult {
	public final static String PAY_CODE_IN_PROGRESS = "10003";
	public final static String PAY_CODE_IN_ABNORMAL = "20000";
	public final static String PAY_CODE_OK = "10000";
	public final static String PAY_CODE_FALSE = "40004";

	public final static String PAY_STATUS_COMPLETE = "TRADE_SUCCESS";
	public final static String PAY_STATUS_WAIT = "WAIT_BUYER_PAY";

	public boolean success = false;
	public boolean weifutongPayStart = false;
	public boolean WFT = false;
	public String desc = "";
	public String code = "";
	public String trade_status = "";
	public String qr_code = "";
	// 微信扫码
	public String code_url = "";

	// 支付宝流水订单号
	public String trade_no = "";
	public String total_amount = "";

	// 微信扫码支付 --- 支付状态 SUCCESS NOTPAY
	public int result;
	public String trade_state = "";
	public String touser = "";
	public String transaction_id;//微信交易单号
	public String time_end;//微信交易时间

	public String errMessage;

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public String toString() {
		return "success = " + success + "desc = " + desc + "code: " + code
				+ " trade_stats = " + trade_status;
	}
}
