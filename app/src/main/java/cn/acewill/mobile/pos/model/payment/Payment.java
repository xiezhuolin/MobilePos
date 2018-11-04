package cn.acewill.mobile.pos.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Acewill on 2016/8/3.
 */
public class Payment implements Serializable{
    private int id;//
    private String appId;
    private int brandId;//
    private int storeId;
    private String name;//
    private String moneyStr;
    private int status; //1启用， 0不启用
    private boolean display;//是否在界面上显示该支付方式

    private String appIDs;//应用在微信或者支付等第三方支付的id
    private String keyStr;//应用在微信或者支付宝等第三方支付上的key或者secret
    private String mchID;//在微信上的商户id
    private String subMchID;//在微信上的子商户id， （当微信支付是代理模式时需要这个id）
    private String appsecret;//微信APPSECRET
    private PaymentCategory category;//支付类型
    private boolean checkBalance = true; //是否检查订单余额

    private BigDecimal money = new BigDecimal("0.00");

    private float deductibleAmount = 0;//抵扣金额

    public Payment(int id,String name)
    {
        this.id = id;
        this.name = name;
    }

    public Payment(int id, String moneyStr, BigDecimal money)
    {
        this.id = id;
        this.moneyStr = moneyStr;
        this.money = money;
    }

    public Payment(int id,String name,float deductibleAmount)
    {
        this.id = id;
        this.name = name;
        this.deductibleAmount = deductibleAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAppIDs() {
        return appIDs;
    }

    public void setAppIDs(String appIDs) {
        this.appIDs = appIDs;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public String getSubMchID() {
        return subMchID;
    }

    public void setSubMchID(String subMchID) {
        this.subMchID = subMchID;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public PaymentCategory getCategory() {
        return category;
    }

    public void setCategory(PaymentCategory category) {
        this.category = category;
    }

    public boolean isCheckBalance() {
        return checkBalance;
    }

    public void setCheckBalance(boolean checkBalance) {
        this.checkBalance = checkBalance;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getMoneyStr() {
        return moneyStr;
    }

    public void setMoneyStr(String moneyStr) {
        this.moneyStr = moneyStr;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public float getDeductibleAmount() {
        return deductibleAmount;
    }

    public void setDeductibleAmount(float deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
    }
}
