package cn.acewill.mobile.pos.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.math.BigDecimal;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/8/5.
 */
@com.raizlabs.android.dbflow.annotation.Table( name = "work_shift", database = AppDatabase.class )
@ModelContainer
public class WorkShift extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement=true)
    private Long id;
    @Column
    private Integer userId;
    @Column
    private String userName;
    @Column
    private Integer terminalId;
    @Column
    private String terminalName;
    @Column
    private Integer definitionId; //班次定义的id，
    @Column
    private String definitionName; //班次定义的名称，
    @Column(typeConverter = BigDecimalConverter.class)
    private BigDecimal spareCash; //备用金
    @Column(typeConverter = BigDecimalConverter.class)
    private BigDecimal cashRevenue; //现金收入 （钱箱里除去备用金意外的收入）
    @Column
    private Long startTime;
    @Column
    private Long endTime;
    @Column
    private boolean createdOffline; //是否为网络断开时本地创建的？
    private String startTimeStr;
    private String endTimeStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Integer definitionId) {
        this.definitionId = definitionId;
    }

    public BigDecimal getSpareCash() {
        return spareCash;
    }

    public void setSpareCash(BigDecimal spareCash) {
        this.spareCash = spareCash;
    }

    public BigDecimal getCashRevenue() {
        return cashRevenue;
    }

    public void setCashRevenue(BigDecimal cashRevenue) {
        this.cashRevenue = cashRevenue;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public boolean isCreatedOffline() {
        return createdOffline;
    }

    public boolean getCreatedOffline() {
        return createdOffline;
    }

    public void setCreatedOffline(boolean createdOffline) {
        this.createdOffline = createdOffline;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    @com.raizlabs.android.dbflow.annotation.TypeConverter
    public static class BigDecimalConverter extends TypeConverter<Float, BigDecimal> {

        public BigDecimalConverter() {
        }

        @Override
        public Float getDBValue(BigDecimal model) {
            return model.floatValue();
        }

        @Override
        public BigDecimal getModelValue(Float data) {
            return new BigDecimal(data);
        }
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }
}
