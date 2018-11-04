package cn.acewill.mobile.pos.model.order;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum PaymentStatus {
    NOT_PAYED(0), //未支付
    PAYED(1), //已经支付
    REFUND(2), //已经退款
    FAILED_TO_QUERY_STATUS(3),//无法查询支付状态,
    CANCELED(4),//已取消
    DUPLICATED(5);//重复单状态
    private int status;
    private PaymentStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
