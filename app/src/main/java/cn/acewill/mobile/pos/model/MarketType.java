package cn.acewill.mobile.pos.model;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum MarketType {
    DISCOUNT(0), //全单打折
    MANUAL(1), //手动打折
    SALES(2); //促销方案

    private int status;
    private MarketType(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
