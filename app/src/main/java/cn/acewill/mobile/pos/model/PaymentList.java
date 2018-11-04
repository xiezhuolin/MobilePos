package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 结账传递的Body参数
 * Created by aqw on 2016/8/19.
 */
public class PaymentList implements Serializable {
    private String appId;
    private String brandId;
    private String storeId;
    private String orderId;//订单号
    private int paymentTypeId;//支付类型
    private String paymentNo;//支付宝，刷卡等方式的本地订单号，可用于后续退款(此字段存在是因为先支付的话，
    // 订单号要在本地生成，把本地生成的这个订单号存在这个字段里，为了支付宝与盒子退款使用)
    private String transactionNo;//微信或支付宝的支付流水号，用于退款
    private String userName;//授权用户号码（进行一些折扣操作时可能需要店长授权，那么这里就是店长的名字）
    private String cardNumber;//会员卡号码
    private String couponCode;//优惠券号码
    private BigDecimal value;//订单金额
    private String operation;//"PAY" 也可以是" REFUND"
    private long createdAt; //这里不能用Date， 因为app的date格式可能和服务器不一样
    private long paidAt; //结账时间
    private String payName;//支付方式名称 比如微信，支付宝，5元代金券
    private String payTypeName;
    private String source;//用来保存威富通是微信还是支付宝支付的
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(long paidAt) {
        this.paidAt = paidAt;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
