package com.acewill.paylibrary;

import android.util.Log;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliBarPayAction;
import com.acewill.paylibrary.epay.EPayResult;
import com.acewill.paylibrary.epay.WeiXinScanPayAction;
import com.acewill.paylibrary.tencent.WXPay;

import org.apache.commons.httpclient.HttpException;

import java.io.IOException;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.utils.ToolsUtils;

public class MicropayTask extends EPayTask {
	@Override
	protected EPayResult doInBackground(PayReqModel... params) {
		PayReqModel payModel = params[0];
		EPayResult ePayResult = null;
		Log.v("PTID_SSS_WEIFUTONG" , ToolsUtils.getPrinterSth(payModel));
		if(!payModel.isQuery){
			if (payModel.payType == PayReqModel.PTID_SSS_ALI) {
				ePayResult = AliPay(payModel);
			}
			else if(payModel.payType == PayReqModel.PTID_SSS_WEIXIN)
			{
				ePayResult = weixinPay(payModel);
			}
			else if(payModel.payType == PayReqModel.PTID_SSS_WEIFUTONG){
				ePayResult = new EPayResult();
				ePayResult.code = payModel.authCode;
				ePayResult.WFT = true;
				ePayResult.success = true;
				ePayResult.weifutongPayStart = true;
			}
			Log.v("weifutongPayStr" , ToolsUtils.getPrinterSth(ePayResult));
			
			if(ePayResult == null){
				System.out.println("pay nul");
				return null;
			}
			if(ePayResult.success == false && ePayResult.weifutongPayStart == false){
				System.out.println("pay false");
//				return ePayResult;
			}
		}

		System.err.println("pay query");
		if (payModel.payType == PayReqModel.PTID_SSS_ALI) {
			ePayResult = StartWaitingForPayCompletion(payModel);
		} else if(payModel.payType == PayReqModel.PTID_SSS_WEIXIN){
			ePayResult = rollbackTransaction(payModel);
		}
		else if(payModel.payType == PayReqModel.PTID_SSS_WEIFUTONG){

		}
		return ePayResult;
	}


	
	private EPayResult AliPay(PayReqModel payModel) {
		EPayResult ePayResult;
		try {
			String totalAmount = payModel.totalAmount+"";
			if (payModel.isDebug) {
				totalAmount = "0.01";
			}
			ePayResult = AliBarPayAction.micropay(AlipayConfig.APPID,
					AlipayConfig.key, payModel.orderNo, payModel.authCode,
					totalAmount, payModel.storeName,
					payModel.aliGoodsItem,
					payModel.storeId, payModel.terminalId);
			return ePayResult;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private EPayResult weixinPay(PayReqModel payModel) {
		// 扫码支付
		String notify_url = "11111111";
		String trade_type = "NATIVE";
		String product_id = "0000";

		// System.out.println("weixin appid = " + WXPay.APPID + ",mchId = "
		// + WXPay.MCH_ID + ",order id = " + Common.cart.orderNo);

//		int fenShouldPay = (int) (payModel.totalAmount * 100);
		String money = String.valueOf(payModel.totalAmount);
		int fenShouldPay = new BigDecimal(money).multiply(new BigDecimal(100)).intValue();

		if (payModel.isDebug) {
			fenShouldPay = 1;
		}

		String body = payModel.wxGoodsDetail;

		body = body.substring(0, Math.min(30, body.length()));

		EPayResult result = WeiXinScanPayAction.micropay(WXPay.APPID, WXPay.MCH_ID,
				WXPay.KEY, payModel.authCode,payModel.orderNo, fenShouldPay, body,
				WXPay.SUB_MCH_ID,  product_id);
		return result;

	}
}
