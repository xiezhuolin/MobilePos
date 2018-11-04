package cn.acewill.mobile.pos.model;

/**
 * Created by Acewill on 2016/8/22.
 */
public enum ReceiptType {
    UNKNOWN(-1),
    ORDER_RECEIPT(0),//订单小票
    HAND_OVER_RECEIPT(1), //交班小票
    DAY_RECEIPT(2), //日结小票
    KITCHEN_RECEIPT(3); //厨房小票
    private int value;

    ReceiptType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

