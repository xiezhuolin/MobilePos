package cn.acewill.mobile.pos.model.user;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum StaffStatus {
    DISABLED(0),//被禁用
    ENABLED(1);//正常状态

    private int status;
    private StaffStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
