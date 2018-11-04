package cn.acewill.mobile.pos.model.dish;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * Created by Acewill on 2016/6/2.
 */
//菜品的基本信息, 浏览菜品时只需要这些信息
@com.raizlabs.android.dbflow.annotation.Table(name="dish_summary",database = AppDatabase.class)
@ModelContainer
public class DishSummary extends BaseModel implements Serializable{
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    public String alias; // 速记码，一般就是拼音首字母
    public int displayOrder; // 在界面上的显示顺序
    private String thumbnailUrl;// 图片url
    private DishStatus status;
    private DishUnit unit; //有的菜可以选半份
    private double price; //每单位这个菜品的价格

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public DishStatus getStatus() {
        return status;
    }

    public void setStatus(DishStatus status) {
        this.status = status;
    }

    public DishUnit getUnit() {
        return unit;
    }

    public void setUnit(DishUnit unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
