package cn.acewill.mobile.pos.model.order;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.model.dish.DiscountInfoObject;


/**
 * 营销活动
 * Created by Acewill on 2016/8/3.
 */
public class MarketingActivity {
    private String discountName;//折扣名称
    private int discountType;//折扣类型，0：折扣；1：金额
    private BigDecimal discountRate;//折扣率（0-100）
    private BigDecimal discountAmount;//金额

    private DiscountTime discountTime;

    private List<DiscountInfoObject> discountInfo;

    private List<Long> excludeDishIdList;//不参加活动的菜品ID列表

    public List<DiscountInfoObject> getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(List<DiscountInfoObject> discountInfo) {
        this.discountInfo = discountInfo;
    }

    public DiscountTime getDiscountTime() {
        return discountTime;
    }

    public void setDiscountTime(DiscountTime discountTime) {
        this.discountTime = discountTime;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public List<Long> getExcludeDishIdList() {
        return excludeDishIdList;
    }

    public void setExcludeDishIdList(List<Long> excludeDishIdList) {
        this.excludeDishIdList = excludeDishIdList;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountRate() {
        return discountRate.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
}
