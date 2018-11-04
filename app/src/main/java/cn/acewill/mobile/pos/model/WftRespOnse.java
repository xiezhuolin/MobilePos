package cn.acewill.mobile.pos.model;

/**
 * Created by DHH on 2017/12/5.
 */

public class WftRespOnse {
    public boolean success;
    public String message;//:返回信息 String 成功时为null，失败返回具体消息
    public String codeImgUrl;//二维码地址 String

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeImgUrl() {
        return codeImgUrl;
    }

    public void setCodeImgUrl(String codeImgUrl) {
        this.codeImgUrl = codeImgUrl;
    }
}
