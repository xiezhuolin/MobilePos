package cn.acewill.mobile.pos.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 折扣时段
 * Created by Acewill on 2016/8/3.
 */
public class DiscountTime implements Serializable{
    @SerializedName("isNoLimit")
    public int type;// 1：永久特价
    public long sdate; //比如 1467302400000,
    public long edate; //比如 1469894400000,
    public String weekday;//每周生效的日期，比如 "1,2,3,4,5",
    public String stime; //生效时间,比如 "06:00",
    public String etime;//结束的时间: "22:00"
    public String dishType;// ：0：堂食，1：外带；2：通用

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSdate() {
        return sdate;
    }

    public void setSdate(long sdate) {
        this.sdate = sdate;
    }

    public long getEdate() {
        return edate;
    }

    public void setEdate(long edate) {
        this.edate = edate;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }
}
