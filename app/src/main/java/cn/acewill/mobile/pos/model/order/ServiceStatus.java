package cn.acewill.mobile.pos.model.order;

/**
 *
 * Created by Acewill on 2016/6/2.
 */
public enum ServiceStatus {

    WAIT_FOR_CALL(0), //等叫
    WAIT_FOR_COOK(1), //待制作
    WAIT_FOR_SERVING(2), //待传菜
    SERVED(3); //已经上菜


    private int status;
    private ServiceStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
