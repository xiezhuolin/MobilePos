package cn.acewill.mobile.pos.service.retrofit.response;


import cn.acewill.mobile.pos.model.user.UserRet;

/**
 *
 *{
 "result": 0,
 "content": {
 "username": "1",
 "pwd": null,
 "userLevel": -1,
 "appid": "1",
 "brandid": null,
 "storeid": null,
 "userType": 1,
 "favorateFunctions": null,
 "status": 1,
 "etime": 0,
 "realname": null,
 "jobnumber": null,
 "openid": "",
 "etimeStr": "1970-01-01 08:00:00",
 "statusStr": "启用",
 "userLevelStr": "超级用户",
 "userTypeStr": "管理者",
 "brandidList": "null",
 "storeidList": "null"
 },
 "errmsg": "0"
 }
 * Created by DHH on 2016/7/19.
 */
public class UserResponse {
    private int result;
    private String errmsg;

    public UserRet getContent() {
        return content;
    }

    public void setContent(UserRet content) {
        this.content = content;
    }

    private UserRet content;
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


}
