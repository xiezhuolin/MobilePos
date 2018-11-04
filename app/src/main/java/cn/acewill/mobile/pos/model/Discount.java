package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by DHH on 2016/12/9.
 */

public class Discount implements Serializable{
    public int discountId;
    public String discountStr;
    /**
     * 折扣名称
     */
    public String discountName;
    /**
     * 折扣类型，0：折扣；1：金额
     */
    public int discountType;
    /**
     * 折扣率（0-100）
     */
    public float discountRate;
    /**
     * 金额
     */
    public BigDecimal discountAmount;


    public Discount(int discountId, String discountStr) {
        this.discountId = discountId;
        this.discountStr = discountStr;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getDiscountStr() {
        return discountStr;
    }

    public void setDiscountStr(String discountStr) {
        this.discountStr = discountStr;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public float getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(float discountRate) {
        this.discountRate = discountRate;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }
}
