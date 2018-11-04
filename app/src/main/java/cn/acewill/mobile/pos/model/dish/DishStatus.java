package cn.acewill.mobile.pos.model.dish;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum DishStatus {
    ABUNDANT(1), //该菜品还有很多
    SOLD_OUT_SOON(2), //该菜品已经快卖完
    SOLD_OUT(3); //已经卖光


    private int status;
    private DishStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
