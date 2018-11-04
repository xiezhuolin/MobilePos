package com.acewill.paylibrary;

import com.acewill.paylibrary.epay.AliGoodsItem;

import java.util.List;

public class PayReqModel {
	public static int PTID_CASH = 0;
	public static int PTID_SSS_ALI = 1;
	public static int PTID_SSS_WEIXIN = 2;
	public static int PTID_SSS_WEIFUTONG = 3;

	public double totalAmount;
	public String orderNo;
	public List<AliGoodsItem> aliGoodsItem;
	public String wxGoodsDetail;
	public boolean isDebug;
	public int payType;
	public String authCode;
	public boolean isQuery;//是否只调用查询
	public String storeName;//门店名
	public String storeId;//门店id
	public String terminalId;//
	public int pay_type; // wtf支付方式 根据支付类型生成不同二维码 0：微信；1：支付宝 ，不传则默认为微信  int 类型

}
