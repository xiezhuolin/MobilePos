package cn.acewill.mobile.pos.model.wsh;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员账号
 * Created by aqw on 2016/10/19.
 */
public class Account implements Serializable{

    private String uid;// 用户编号 "1540808129041414",
    private String uno; //卡号 1281163,
    private String type;//类型，目前是"wx-wsh",
    private String name; //名字 "",
    private String phone; // "",
    private String birthday;// "",
    private String gender; // 性别,
    private String registered; //注册日期 2016-07-25",
    private String openid; //oA_oKv5kWZjJuITzX689wKLfYf4U",
    private Integer grade;//级别,
    private String gradeName; //"\u666e\u901a\u4f1a\u5458",
    private Integer balance; //余额 "0", 单位分
    private Integer credit; // 积分
    private boolean usecredit; //是否可以使用积分消费
    private boolean inEffect; //是否生效 ,
    private String uActualNo;// 实体卡号
    private List<WshAccountCoupon> coupons;//可用券
    private BigDecimal memberConsumeCost = new BigDecimal("0.00");//会员消费金额

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public boolean isUsecredit() {
        return usecredit;
    }

    public void setUsecredit(boolean usecredit) {
        this.usecredit = usecredit;
    }

    public boolean isInEffect() {
        return inEffect;
    }

    public void setInEffect(boolean inEffect) {
        this.inEffect = inEffect;
    }

    public String getuActualNo() {
        return uActualNo;
    }

    public void setuActualNo(String uActualNo) {
        this.uActualNo = uActualNo;
    }

    public List<WshAccountCoupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<WshAccountCoupon> coupons) {
        this.coupons = coupons;
    }

    public BigDecimal getMemberConsumeCost() {
        if(memberConsumeCost.compareTo(BigDecimal.ZERO) == 1)
        {
            memberConsumeCost = memberConsumeCost.setScale(2,BigDecimal.ROUND_DOWN);
        }
        return memberConsumeCost;
    }

    public void setMemberConsumeCost(BigDecimal memberConsumeCost) {
        this.memberConsumeCost = memberConsumeCost;
    }
}
