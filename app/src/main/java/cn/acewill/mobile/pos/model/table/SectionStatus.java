package cn.acewill.mobile.pos.model.table;

/**
 * Created by Acewill on 2016/6/2.
 */
public enum SectionStatus {
    AVAIABLE(1), //可以使用
    OCCUPIED(2); //已经被使用（可能整个大厅已经被预定做宴会之类的）



    private int status;
    private SectionStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
