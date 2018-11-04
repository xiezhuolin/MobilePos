package cn.acewill.mobile.pos.model.wsh;

import java.io.Serializable;

/**
 * Created by aqw on 2016/10/21.
 */
public class WshDealPreview implements Serializable{
    private String tcId;
    private boolean verify_sms;
    private boolean verify_password;

    public String getTcId() {
        return tcId;
    }

    public void setTcId(String tcId) {
        this.tcId = tcId;
    }

    public boolean isVerify_sms() {
        return verify_sms;
    }

    public void setVerify_sms(boolean verify_sms) {
        this.verify_sms = verify_sms;
    }

    public boolean isVerify_password() {
        return verify_password;
    }

    public void setVerify_password(boolean verify_password) {
        this.verify_password = verify_password;
    }
}
