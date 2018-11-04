package cn.acewill.mobile.pos.model.user;

import java.io.Serializable;

/**
 *
 * username
 status   // 0：禁用；1：正常
 realname
 jobnumber
 discount  //员工折扣，0-100
 quartersid  // 岗位ID
 quartersName  // 岗位名称

 * Created by DHH on 2017/2/20.
 */


public class Staff implements Serializable{
    public String staffName;
    public String staffRole;
    public String username;
    public StaffStatus status;
    public String realname;
    public String jobnumber;
    public float discount;
    public String quartersName;
    public int quartersid;


    public Staff(String staffName,String staffRole)
    {
        this.staffName = staffName;
        this.staffRole = staffRole;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getQuartersName() {
        return quartersName;
    }

    public void setQuartersName(String quartersName) {
        this.quartersName = quartersName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getQuartersid() {
        return quartersid;
    }

    public void setQuartersid(int quartersid) {
        this.quartersid = quartersid;
    }
}
