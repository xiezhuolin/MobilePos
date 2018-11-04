package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by DHH on 2017/3/14.
 */

public class Market implements Serializable{
    /**
     * 该优惠方案是否启用
     */
    private boolean status;
    /**
     * 营销方案ID
     */
    private long marketid;
    /**
     * 营销方案名字
     */
    private String marketName;
    /**
     * 方案触发类型。0-满减，1-循环满减，2-单品
     */
    private int triggerType;
    /**
     * 营销方案类型。0-折扣，1-优惠金额，2-有买有送，3-第二份特价
     */
    private int marketType;
    /**
     * 是否有日期限制
     */
    private boolean dateAvailable;
    /**
     * 是否有特殊日期限制
     */
    private boolean weekAvailable;
    /**
     * 是否有时间限制
     */
    private boolean timeAvailable;
    /**
     * 开始日期
     */
    private long startDate;
    /**
     * 结束日期
     */
    private long endDate;
    /**
     * "1,2,3",特殊日期。如：1,2则表示周一、周二
     */
    private String week;
    /**
     * 开始时间如  08:00
     */
    private String startTime;
    /**
     * 结束时间如  22:00
     */
    private String endTime;
    /**
     * 折扣率
     */
    private float rate;
    /**
     * 现金优惠
     */
    private BigDecimal cash = new BigDecimal("0.00");
    /**
     * 循环满减额度float
     */
    private float loopFullCash;
    /**
     * 满减额度float
     */
    private float fullCash;
    /**
     * 是否只限会员
     */
    private boolean memberOnly;
    /**
     * 触发营销方案的菜品数量
     */
    private int triggerDishCount;
    /**
     * 赠送多少份菜品
     */
    private int giftDishCount;
    /**
     * 第二份菜品的折扣率
     */
    private BigDecimal theSecondRate;
    /**
     * 第二份菜品的实价
     */
    private BigDecimal theSecondPrice;
    /**
     * 	第二份菜品优惠类型, 0-折扣，1-优惠金额
     */
    private int theSecondType;
    /**
     * 如果该营销活动为全单营销，则表示不参与活动的菜品，反之则表示参与活动的菜品 存的是dishId
     */
    private List<Integer> marketDishList;
    /**
     * （单品营销）触发营销方案的菜品 存的是dishId
     */
    private List<Integer> triggerDishList;
    /**
     * 	grade 是否开启会员等级
     */
    private boolean grade;
    /**
     * 该营销方案可以执行的会员等级
     */
    private List<Long> gradeList;
    /**
     * 是否和会员价同时执行
     */
    private boolean commonExecute;

    public boolean isCommonExecute() {
        return commonExecute;
    }

    public void setCommonExecute(boolean commonExecute) {
        this.commonExecute = commonExecute;
    }

    public boolean isGrade() {
        return grade;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
    }

    public List<Long> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<Long> gradeList) {
        this.gradeList = gradeList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getMarketid() {
        return marketid;
    }

    public void setMarketid(long marketid) {
        this.marketid = marketid;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public boolean isDateAvailable() {
        return dateAvailable;
    }

    public void setDateAvailable(boolean dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    public boolean isWeekAvailable() {
        return weekAvailable;
    }

    public void setWeekAvailable(boolean weekAvailable) {
        this.weekAvailable = weekAvailable;
    }

    public int getMarketType() {
        return marketType;
    }

    public void setMarketType(int marketType) {
        this.marketType = marketType;
    }

    public boolean isTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(boolean timeAvailable) {
        this.timeAvailable = timeAvailable;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getRate() {
        return rate;
    }

    public BigDecimal getRateBig()
    {
        return new BigDecimal(rate+"");
    }


    public void setRate(float rate) {
        this.rate = rate;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public float getLoopFullCash() {
        return loopFullCash;
    }

    public void setLoopFullCash(float loopFullCash) {
        this.loopFullCash = loopFullCash;
    }

    public float getFullCash() {
        return fullCash;
    }

    public void setFullCash(float fullCash) {
        this.fullCash = fullCash;
    }

    public boolean isMemberOnly() {
        return memberOnly;
    }

    public void setMemberOnly(boolean memberOnly) {
        this.memberOnly = memberOnly;
    }

    public int getTriggerDishCount() {
        return triggerDishCount;
    }

    public void setTriggerDishCount(int triggerDishCount) {
        this.triggerDishCount = triggerDishCount;
    }

    public int getGiftDishCount() {
        return giftDishCount;
    }

    public void setGiftDishCount(int giftDishCount) {
        this.giftDishCount = giftDishCount;
    }

    public BigDecimal getTheSecondRate() {
        return theSecondRate;
    }

    public void setTheSecondRate(BigDecimal theSecondRate) {
        this.theSecondRate = theSecondRate;
    }

    public BigDecimal getTheSecondPrice() {
        return theSecondPrice;
    }

    public void setTheSecondPrice(BigDecimal theSecondPrice) {
        this.theSecondPrice = theSecondPrice;
    }

    public int getTheSecondType() {
        return theSecondType;
    }

    public void setTheSecondType(int theSecondType) {
        this.theSecondType = theSecondType;
    }

    public List<Integer> getMarketDishList() {
        return marketDishList;
    }

    public void setMarketDishList(List<Integer> marketDishList) {
        this.marketDishList = marketDishList;
    }

    public List<Integer> getTriggerDishList() {
        return triggerDishList;
    }

    public void setTriggerDishList(List<Integer> triggerDishList) {
        this.triggerDishList = triggerDishList;
    }
}
