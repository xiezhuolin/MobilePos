package cn.acewill.mobile.pos.model;

import java.io.Serializable;

/**
 * Created by DHH on 2017/1/17.
 */

public class TerminalInfo implements Serializable{
    public String phone;
    public String mobile;
    public String address;
    public String appid;
    public String brandid;
    public String storeid;
    public String sname;//门店名称
    public String tname;//终端名称
    public String brandName;//品牌名称;
    public String terminalTypeStr;//终端机器的描述
    public String terminalMac;
    public String terminalid;
    public int printerid;
    public int secondaryPrinterId;
    public int takeoutPrinterid;
    public int kdsid;
    public int isactive = 0;
    public Long storeEndTime;//门店结束时间
    public String bindUUID;//门店绑定的唯一标识,绑定成功时需要保存到本地方便下次验证是否是在本机绑定的后台F码
    /**
     * 0 未激活  1已激活
     * @return
     */
    public boolean isActive()
    {
        return isactive ==1;
    }

    public String getTerminalTypeStr() {
        return terminalTypeStr;
    }

    public void setTerminalTypeStr(String terminalTypeStr) {
        this.terminalTypeStr = terminalTypeStr;
    }

    public int getPrinterid() {
        return printerid;
    }

    public void setPrinterid(int printerid) {
        this.printerid = printerid;
    }

    public int getKdsid() {
        return kdsid;
    }

    public void setKdsid(int kdsid) {
        this.kdsid = kdsid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getSecondaryPrinterId() {
        return secondaryPrinterId;
    }

    public void setSecondaryPrinterId(int secondaryPrinterId) {
        this.secondaryPrinterId = secondaryPrinterId;
    }

    public Long getStoreEndTime() {
        return storeEndTime;
    }

    public void setStoreEndTime(Long storeEndTime) {
        this.storeEndTime = storeEndTime;
    }

    public int getTakeoutPrinterid() {
        return takeoutPrinterid;
    }

    public void setTakeoutPrinterid(int takeoutPrinterid) {
        this.takeoutPrinterid = takeoutPrinterid;
    }

    public String getBindUUID() {
        return bindUUID;
    }

    public void setBindUUID(String bindUUID) {
        this.bindUUID = bindUUID;
    }

}
