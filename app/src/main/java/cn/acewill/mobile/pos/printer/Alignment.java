package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/6/8.
 */
public enum Alignment {
    LEFT(1),
    CENTER(2),
    RIGHT(3);
    private int status;
    Alignment(int status) {
        this.status = status;
    }
}
