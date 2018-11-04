package cn.acewill.mobile.pos.model;

/**
 * Created by DHH on 2017/1/6.
 */

public enum KitchenPrintMode {
    PER_ITEM(0), //每份菜一张单 -- 比如一个菜点了3份， 会打印机3张单，上面的份数是1
    PER_DISH(1);//一个菜打印一张单 -- 比如一个菜点了3份， 也只打一张单，上面的份数是3

    private int value;

    KitchenPrintMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
