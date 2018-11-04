package cn.acewill.mobile.pos.model.user;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum UserStatus {
    ENABLED(1),//正常状态
    DISABLED(2);//被禁用

    private int status;
    private UserStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
