package cn.acewill.mobile.pos.model;

import java.util.List;

/**
 * Created by aqw on 2016/9/12.
 */
public class KdsOrderData {

    public String oid;//订单ID
    public long createTime;//下单时间,系统时间的毫秒数
    public int type;//订单类型,int, 0：堂食；1：打包外带；2：外卖
    public String total;//总价
    public int paystatus;//订单支付信息，int，0：未付款；1：已付款
    public String tablename;//桌台信息
    public String comment;//全单备注
    public String orderRange;//订单时段
    public String pos;//下单的pos编号
    public String operator;//下单人员
    public String openID;//微信支付下，顾客支付后的openID，用于微信通知
    public String fetchID;//取餐号

    public String thirdPlatformOrderId;
    public String thirdPlatformOrderIdView;
    public String thirdPlatfromOrderIdDaySeq;
    public String source;

    public List<KdsDishItem> dishitems;
    public List<KdsDishItem> deletedishes;

}
