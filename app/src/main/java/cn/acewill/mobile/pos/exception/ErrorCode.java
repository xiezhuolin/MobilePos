package cn.acewill.mobile.pos.exception;

/**
 * Created by Acewill on 2016/6/14.
 */
public enum ErrorCode {
    UNKNOWN_ERROR(0),
    NETWORK_ERROR(1), //无法连接服务器
    INVALID_USERNAME_PASSWORD(2), //用户名密码不匹配
    TERMINAL_LOGIN_FAIL(3), //终端登录失败
    SERVER_NOT_CONFIGURED(3001), //服务器尚未配置
    NETWORK_NOT_CONNECT(110),//网络无法连接
    TOKEN_ERROR(1035); //服务器Token错误


    private int status;
    private ErrorCode(int status) {
        this.status = status;
    }
}
