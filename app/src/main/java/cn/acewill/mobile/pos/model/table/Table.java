package cn.acewill.mobile.pos.model.table;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import cn.acewill.mobile.pos.factory.AppDatabase;

/**
 * Created by Acewill on 2016/6/1.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="table",database = AppDatabase.class)
@ModelContainer
public class Table extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    private String appId; // 商户ID
    private long brandId; //品牌ID
    private long storeId; //门店ID

    @Column
    private long sectionId; //这个桌台属于哪个区域？

    private String serialNumber; //桌台的编号, 一般来说就是1,2,3这样的数字，不过也可以起类似XX_XXX_XXXX这样格式的编号
    public TableStatus status;
    public UiProperties uiProperties;
    @Column
    private String name; //就是桌台1，桌台2这样的名字

    @Column
    private int capacity; //最大就餐人数

    @Column
    private int customerAmount; //正在使用的顾客人数

//    @Column
//    private TableStatus.UseStatus status;  //使用状态
//    @Column
//    private TableStatus.BookingStatus bookingStatus; //预定状态
//    @Column
    private long startTime; //开始使用的时间
    @Column
    private int width; // 桌台的宽
    @Column
    private int height; // 桌台的高
    @Column
    private float positionX; // 桌台在背景区域图片上的X坐标（单位px,需要转成dp使用）
    @Column
    private float positionY; // 桌台在背景区域图片上的Y坐标（单位px,需要转成dp使用）
    @Column
    public String temStu;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

//    public TableStatus.UseStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(TableStatus.UseStatus status) {
//        this.status = status;
//    }
//
//    public TableStatus.BookingStatus getBookingStatus() {
//        return bookingStatus;
//    }
//
//    public void setBookingStatus(TableStatus.BookingStatus bookingStatus) {
//        this.bookingStatus = bookingStatus;
//    }

    public int getCustomerAmount() {
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public String getTemStu() {
        return temStu;
    }

    public void setTemStu(String temStu) {
        this.temStu = temStu;
    }
}
