package cn.acewill.mobile.pos.model.order;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum OrderItemStatus {

    NOT_ACKNOWLEDGE(0), //未确认
    ADJUST_WEIGHT(1), //重量待调整
    ACKNOWLEDGED(2), //已经确认
    REJECTED(3); //退菜


    private int status;
    private OrderItemStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
