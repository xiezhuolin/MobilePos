package cn.acewill.mobile.pos.model.wsh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 预消费接口
 * Created by aqw on 2016/10/21.
 */
public class WshCreateDeal implements Serializable{

    public static class Request implements Serializable{
        private String uid; //用户id
        private String cno; //卡号
        private String shop_id;
        private String cashier_id; //收银员id
        private int consume_amount;//消费金额（分）
        private int payment_amount;//实际支付金额
        private int payment_mode;//数字 1:现⾦ 2:银⾏卡 3:店内微信 4:⽀付宝 6:线上微信
        private int sub_balance;// 使用了多少储值卡的金额
        private int sub_credit;//使用了多少积分
        private List<String> deno_coupon_ids = new ArrayList<>();
        private List<String> gift_coupons_ids = new ArrayList<>();
        private List<String> activity_ids = new ArrayList<>();
        private int count_num;//本次交易应该累加的次数
        private String remark ="";
        private String biz_id; //业务id，调用方保证唯一
        private List<Pruduct> products;//菜品数组

        public Request() {

        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCno() {
            return cno;
        }

        public void setCno(String cno) {
            this.cno = cno;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getCashier_id() {
            return cashier_id;
        }

        public void setCashier_id(String cashier_id) {
            this.cashier_id = cashier_id;
        }

        public String getBiz_id() {
            return biz_id;
        }

        public void setBiz_id(String biz_id) {
            this.biz_id = biz_id;
        }

        public int getConsume_amount() {
            return consume_amount;
        }

        public void setConsume_amount(int consume_amount) {
            this.consume_amount = consume_amount;
        }

        public int getPayment_amount() {
            return payment_amount;
        }

        public void setPayment_amount(int payment_amount) {
            this.payment_amount = payment_amount;
        }

        public int getPayment_mode() {
            return payment_mode;
        }

        public void setPayment_mode(int payment_mode) {
            this.payment_mode = payment_mode;
        }

        public int getSub_balance() {
            return sub_balance;
        }

        public void setSub_balance(int sub_balance) {
            this.sub_balance = sub_balance;
        }

        public int getSub_credit() {
            return sub_credit;
        }

        public void setSub_credit(int sub_credit) {
            this.sub_credit = sub_credit;
        }

        public List<String> getDeno_coupon_ids() {
            return deno_coupon_ids;
        }

        public void setDeno_coupon_ids(List<String> deno_coupon_ids) {
            this.deno_coupon_ids = deno_coupon_ids;
        }

        public List<String> getGift_coupons_ids() {
            return gift_coupons_ids;
        }

        public void setGift_coupons_ids(List<String> gift_coupons_ids) {
            this.gift_coupons_ids = gift_coupons_ids;
        }

        public List<String> getActivity_ids() {
            return activity_ids;
        }

        public void setActivity_ids(List<String> activity_ids) {
            this.activity_ids = activity_ids;
        }

        public int getCount_num() {
            return count_num;
        }

        public void setCount_num(int count_num) {
            this.count_num = count_num;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<Pruduct> getProducts() {
            return products;
        }

        public void setProducts(List<Pruduct> products) {
            this.products = products;
        }
    }

    public static class Pruduct implements Serializable{
        public String name;//菜品名称
        public String no;//菜品编号
        public int num;//产品数量
        public int price;//产品价格(分)
        public int is_activity;//是否参加活动(1:参加，2：不参加)
    }

}
