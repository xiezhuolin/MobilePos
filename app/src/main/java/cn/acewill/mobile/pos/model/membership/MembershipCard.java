package cn.acewill.mobile.pos.model.membership;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/6/2.
 * 会员卡
 */
@com.raizlabs.android.dbflow.annotation.Table(name="membership_card",database = AppDatabase.class)
@ModelContainer
public class MembershipCard extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private String number; //卡号
    @Column
    private String rank; //等级
    @Column
    private double balance; //余额
    @Column
    private double refundableBalance; //可退余额
    @Column
    private int creditPoint; //积分
    @Column
    private long saleTime; // 办卡日期
    @Column
    private long expireTime; // 过期日期

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getRefundableBalance() {
        return refundableBalance;
    }

    public void setRefundableBalance(double refundableBalance) {
        this.refundableBalance = refundableBalance;
    }

    public int getCreditPoint() {
        return creditPoint;
    }

    public void setCreditPoint(int creditPoint) {
        this.creditPoint = creditPoint;
    }

    public long getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(long saleTime) {
        this.saleTime = saleTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
