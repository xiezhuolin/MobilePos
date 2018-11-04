package cn.acewill.mobile.pos.model.wsh;

import java.io.Serializable;

/**
 * adapter中使用
 * Created by aqw on 2016/10/19.
 */
public class Coupon implements Serializable{

    private String title;//标题
    private String couponid;//券id
    private int deno;//券面值
    private int type;//券类型 1:代金券；2:礼品券

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
    }

    public int getDeno() {
        return deno;
    }

    public void setDeno(int deno) {
        this.deno = deno;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
