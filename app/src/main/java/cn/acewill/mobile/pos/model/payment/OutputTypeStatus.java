package cn.acewill.mobile.pos.model.payment;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum OutputTypeStatus {
    PAYOUT(0),//支出
    PAYIN(1);//收入

    private int status;
    private OutputTypeStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
