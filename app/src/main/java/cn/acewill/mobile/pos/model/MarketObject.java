package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by DHH on 2017/3/15.
 */

public class MarketObject implements Serializable{
    private MarketType marketType;//营销方案类型
    private String category;//营销方案类型  MANUAL手动   AUTO营销活动
    private BigDecimal reduceCash; //营销方案优惠了多少金额
    private String marketName; //营销方案名称
    private int discountType = 0;//折扣类型，0：折扣；1：金额

    public MarketObject()
    {

    }

    public MarketObject(String marketName,BigDecimal reduceCash,MarketType marketType)
    {
        this.marketName = marketName;
        this.reduceCash = reduceCash;
        this.marketType = marketType;
        if(marketType == MarketType.SALES)
        {
            category = "AUTO";// 营销活动
        }
        else{
            category = "MANUAL";//手工打折
        }
    }

    public BigDecimal getReduceCash() {
        return reduceCash.setScale(2, BigDecimal.ROUND_DOWN);
    }

    public void setReduceCash(BigDecimal reduceCash) {
        this.reduceCash = reduceCash;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }
}
