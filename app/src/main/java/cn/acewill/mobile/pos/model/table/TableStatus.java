package cn.acewill.mobile.pos.model.table;

/**
 * 桌台状态
 * Created by aqw on 2016/12/17.
 */
public enum TableStatus {

    EMPTY(1), //空台状态
    IN_USE(2),//正在使用
    DIRTY(3);// 脏台状态

    private int status;
    TableStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
