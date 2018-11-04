package cn.acewill.mobile.pos.model.dish;

import java.math.BigDecimal;

/**
 * Created by aqw on 2016/10/13.
 */
public class DiscountInfoObject {
    private int discountType; // 0:满减， 1:折扣  2:赠送优惠(暂时不需要) 3:自由配置金额
    private BigDecimal supportMinPrice; //高于某价格开始执行
    private BigDecimal supportMaxPrice; //高于某价格开始不执行
    private BigDecimal unsupportMinPrice; //低于某价格部分不参与
    private BigDecimal unsupportMaxPrice; //高于某价格部分不参与
    private BigDecimal rate; //折扣率 2位小数，例如0.8，8折
    private BigDecimal reduce; //直减价

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getSupportMinPrice() {
        return supportMinPrice;
    }

    public void setSupportMinPrice(BigDecimal supportMinPrice) {
        this.supportMinPrice = supportMinPrice;
    }

    public BigDecimal getSupportMaxPrice() {
        return supportMaxPrice;
    }

    public void setSupportMaxPrice(BigDecimal supportMaxPrice) {
        this.supportMaxPrice = supportMaxPrice;
    }

    public BigDecimal getUnsupportMinPrice() {
        return unsupportMinPrice;
    }

    public void setUnsupportMinPrice(BigDecimal unsupportMinPrice) {
        this.unsupportMinPrice = unsupportMinPrice;
    }

    public BigDecimal getUnsupportMaxPrice() {
        return unsupportMaxPrice;
    }

    public void setUnsupportMaxPrice(BigDecimal unsupportMaxPrice) {
        this.unsupportMaxPrice = unsupportMaxPrice;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
