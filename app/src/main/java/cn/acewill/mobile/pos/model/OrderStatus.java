package cn.acewill.mobile.pos.model;

/**
 * Created by DHH on 2016/11/21.
 */

public enum OrderStatus {
    NORMAL(0), //正常发送订单到厨房
    PENDING(1); //挂起 -- 指不把该订单发送到厨房

    private int status;
    OrderStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

        public void setStatus(int status) {
        this.status = status;
    }
}
