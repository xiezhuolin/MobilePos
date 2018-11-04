package cn.acewill.mobile.pos.service.retrofit.response;

/**
 * Created by aqw on 2016/9/12.
 */
public class KDSResponse {

    private boolean success;
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
