package cn.acewill.mobile.pos.service.retrofit.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/3.
 */
public class WeiFuTongResponse implements Serializable {

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    private String transaction_id;
    private boolean success = false;
    private String message;
    private String need_query;//是否需要轮训获取支付结果
    private String errcode;//错误码

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNeed_query() {
        return need_query;
    }

    public void setNeed_query(String need_query) {
        this.need_query = need_query;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }
}
