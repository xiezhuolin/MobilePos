package cn.acewill.mobile.pos.model.newPos;

/**
 * Created by DHH on 2016/11/17.
 */

public class Reserve {
    /**
     * 客户姓名
     */
    public String customrerName;
    /**
     * 电话
     */
    public String phoneNumber;
    /**
     * 备注
     */
    public String note;
    /**
     * 桌台ID
     */
    public long tableId;
    /**
     * 桌台号
     */
    public String tableName;
    /**
     * 日期   2016-10-15
     */
    public String date;
    /**
     * 时间  10:30
     */
    public String time;
    /**
     * 就餐人数
     */
    public int repastNumber;

    public Reserve(String customrerName, int repastNumber, String tableName, String date, String time)
    {
        this.customrerName = customrerName;
        this.repastNumber = repastNumber;
        this.tableName = tableName;
        this.date = date;
        this.time = time;
    }

    public String getCustomrerName() {
        return customrerName;
    }

    public void setCustomrerName(String customrerName) {
        this.customrerName = customrerName;
    }

    public int getRepastNumber() {
        return repastNumber;
    }

    public void setRepastNumber(int repastNumber) {
        this.repastNumber = repastNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
