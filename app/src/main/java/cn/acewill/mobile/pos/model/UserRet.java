package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DHH on 2016/8/5.
 */
public class UserRet implements Serializable{
    public String username;
    public Integer userType;
    public String etimeStr;
    public String userLevelStr;
    public String userTypeStr;
    public String realname;
    public String jobnumber;
    public String discount;
    public String statusStr;
    public List<Integer> authorityIDs;//权限id列表
    private int supportCallMaterial;//0：不支持；其他：支持

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Integer> getAuthorityIDs() {
        return authorityIDs;
    }

    public void setAuthorityIDs(List<Integer> authorityIDs) {
        this.authorityIDs = authorityIDs;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUserTypeStr() {
        return userTypeStr;
    }

    public void setUserTypeStr(String userTypeStr) {
        this.userTypeStr = userTypeStr;
    }

    public String getUserLevelStr() {
        return userLevelStr;
    }

    public void setUserLevelStr(String userLevelStr) {
        this.userLevelStr = userLevelStr;
    }

    public String getEtimeStr() {
        return etimeStr;
    }

    public void setEtimeStr(String etimeStr) {
        this.etimeStr = etimeStr;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public int getSupportCallMaterial() {
        return supportCallMaterial;
    }

    public void setSupportCallMaterial(int supportCallMaterial) {
        this.supportCallMaterial = supportCallMaterial;
    }
}
