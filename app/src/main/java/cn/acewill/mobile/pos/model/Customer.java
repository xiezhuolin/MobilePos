package cn.acewill.mobile.pos.model;

import java.io.Serializable;

/**
 * Created by DHH on 2017/1/12.
 */

public class Customer implements Serializable{
    public String customerName;
    public String customerPhoneNumber;
    public String customerAddress;
    public String customerOuterOrderId;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerOuterOrderId() {
        return customerOuterOrderId;
    }

    public void setCustomerOuterOrderId(String customerOuterOrderId) {
        this.customerOuterOrderId = customerOuterOrderId;
    }
}
