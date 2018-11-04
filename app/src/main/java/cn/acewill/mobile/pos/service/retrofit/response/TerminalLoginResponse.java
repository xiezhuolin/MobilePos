package cn.acewill.mobile.pos.service.retrofit.response;

/**
 * Created by Acewill on 2016/8/1.
 * {
 "result": 0,
 "content": null,
 "errmsg": "0",
 "terminalid": 1,
 "version": null,
 "updatetime": null,
 "downloadpath": null,
 "description": null,
 "token": "b24b282e-e3e0-4d8d-af6f-e90dab3aa7ca"
 }
 */
public class TerminalLoginResponse {
    private int result;
    private String errmsg;
    private String terminalid;
    public String bindUUID;//门店绑定的唯一标识,绑定成功时需要保存到本地方便下次验证是否是在本机绑定的后台F码
    private String token;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTerminalid() {
        return terminalid;
    }

    public void setTerminalid(String terminalid) {
        this.terminalid = terminalid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBindUUID() {
        return bindUUID;
    }

    public void setBindUUID(String bindUUID) {
        this.bindUUID = bindUUID;
    }
}
