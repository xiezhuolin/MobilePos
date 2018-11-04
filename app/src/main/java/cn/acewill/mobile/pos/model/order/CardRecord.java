package cn.acewill.mobile.pos.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by DHH on 2017/12/5.
 */

public class CardRecord implements Serializable{
    public long orderid;//订单ID
    public String name;//挂账人
    public String contact;//联系方式
    public String date;//时间
    public BigDecimal price;//金额
    public int status; //状态 0:未挂账 ；1：已挂账

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }
}
