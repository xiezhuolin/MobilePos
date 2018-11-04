package cn.acewill.mobile.pos.model.user;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/6/2.
 */
//用户，指的是使用POS系统的人，不是客户
@com.raizlabs.android.dbflow.annotation.Table(name="user",database = AppDatabase.class)
@ModelContainer
public class User extends BaseModel implements Serializable{
    @Column
    @PrimaryKey(autoincrement = true)
    private int id;				//用户ID
    @Column
    private String account;		//登录系统的名字
    private UserStatus status;				//状态
    private String name;	//姓名，允许中文
    private String password; //密码

    private String avatarUrl;		//头像的url
    private String discription;	//用户自己的描述

    private String sessionId; //登录后服务器返回的会话信息

    private UserRet userRet;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRet getUserRet() {
        return userRet;
    }

    public void setUserRet(UserRet userRet) {
        this.userRet = userRet;
    }

}
