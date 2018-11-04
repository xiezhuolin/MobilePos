package cn.acewill.mobile.pos.model.dish;

import java.io.Serializable;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.model.order.DiscountTime;

/**
 * Created by DHH on 2016/12/12.
 */

public class DishDiscount implements Serializable{
    public DiscountTime discountTime;
    public String name = "原价";
    public String discountPrice;// : 特价价格，decimal
    public String appendOrderDiscount;// : 是否参与全单折扣, int, 0:不参与； 1：参与
    public String isOrderDiscountIncluding;// : 菜品特价价格是否计入全单折扣计算条件，
    private int discountType;//折扣类型，0：折扣；1：金额
    public BigDecimal reductiMoney = new BigDecimal("0.00");//如何是减钱的折扣 该字段表示要减的金额
    private boolean isEnable = true;
    public DishDiscount(DishDiscount dishDiscount,String discountPrice)
    {
        this.discountTime = dishDiscount.discountTime;
        this.discountPrice = discountPrice;
        this.appendOrderDiscount = dishDiscount.appendOrderDiscount;
        this.isOrderDiscountIncluding = dishDiscount.isOrderDiscountIncluding;
    }

    public DishDiscount(String disCountName,String disCountPrice,int discountType,boolean isEnable)
    {
        this.name = disCountName;
        this.discountPrice = disCountPrice;
        this.discountType = discountType;
        this.isEnable = isEnable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public BigDecimal getReductiMoney() {
        return reductiMoney;
    }

    public void setReductiMoney(BigDecimal reductiMoney) {
        this.reductiMoney = reductiMoney;
    }
}
