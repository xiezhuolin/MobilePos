package cn.acewill.mobile.pos.model.table;

/**
 * Created by aqw on 2016/12/17.
 */
public enum BookingStatus {
    NOT_BOOKING(-1), //尚未预定
    WAITING_CUSTOMER(1), //等待谷歌到来
    IN_USE(2), //正在使用
    CANCELED(3),//已经取消
    OUT_OF_DATE(4);//已经过期

    private int status;
    BookingStatus(int status) {
        this.status = status;
    }
}
