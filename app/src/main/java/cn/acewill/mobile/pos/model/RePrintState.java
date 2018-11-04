package cn.acewill.mobile.pos.model;

/**
 * Created by Acewill on 2016/8/22.
 */
public enum RePrintState {
    PRIMARYSUCCESS(1),//原打印机可用
    STANDBYSUCCESS(2), //备用打印机可用
    ALLERROR(3); //都不可用
    private int value;

    RePrintState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

