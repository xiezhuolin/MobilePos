package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.model.payment.OutputTypeStatus;


/**
 * Created by DHH on 2017/2/20.
 */

public class StandByCash implements Serializable{
    public String standByDate;
    public String standByInfo;
    public String StandByMoney;

    public String rpid;
    public int reasonid;
    public String reasonName;
    public OutputTypeStatus outputType;
    public BigDecimal amount;
    public long createTime;
    public String createTimeStr;
    public String outputTypeStr;
    public int reasonType;


    public StandByCash(String standByDate, String standByInfo,String StandByMoney)
    {
        this.standByDate = standByDate;
        this.standByInfo = standByInfo;
        this.StandByMoney = StandByMoney;
    }

    public String getStandByDate() {
        return standByDate;
    }

    public void setStandByDate(String standByDate) {
        this.standByDate = standByDate;
    }

    public String getStandByInfo() {
        return standByInfo;
    }

    public void setStandByInfo(String standByInfo) {
        this.standByInfo = standByInfo;
    }

    public String getStandByMoney() {
        return StandByMoney;
    }

    public void setStandByMoney(String standByMoney) {
        StandByMoney = standByMoney;
    }

    public String getRpid() {
        return rpid;
    }

    public void setRpid(String rpid) {
        this.rpid = rpid;
    }

    public int getReasonid() {
        return reasonid;
    }

    public void setReasonid(int reasonid) {
        this.reasonid = reasonid;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public OutputTypeStatus getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputTypeStatus outputType) {
        this.outputType = outputType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getOutputTypeStr() {
        return outputTypeStr;
    }

    public void setOutputTypeStr(String outputTypeStr) {
        this.outputTypeStr = outputTypeStr;
    }

    public int getReasonType() {
        return reasonType;
    }

    public void setReasonType(int reasonType) {
        this.reasonType = reasonType;
    }
}
