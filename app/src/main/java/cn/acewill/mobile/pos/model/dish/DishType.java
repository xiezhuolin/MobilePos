package cn.acewill.mobile.pos.model.dish;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * 菜品分类
 * Created by Acewill on 2016/6/2.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="dish_type",database = AppDatabase.class)
@ModelContainer
public class DishType extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("kindID")
    private long id;               //品项类型ID
    @SerializedName("kindName")
    @Column
    private String name;        //品项类型
    @Column
    private DishTypeStatus status;	//某些分类可能只在特定时间段内有效
    @Column
    private int displayOrder;		//显示顺序
    @Column
    private String mnemonicCode;	//速记码
    @Column
    private String kindColor;	//分类颜色
    @Column
    private String imageName;//分类图片

    List<DishType> diskTypeList;  //子类型



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

    public DishTypeStatus getStatus() {
        return status;
    }

    public void setStatus(DishTypeStatus status) {
        this.status = status;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public List<DishType> getDiskTypeList() {
        return diskTypeList;
    }

    public void setDiskTypeList(List<DishType> diskTypeList) {
        this.diskTypeList = diskTypeList;
    }

    public String getKindColor() {
        return kindColor;
    }

    public void setKindColor(String kindColor) {
        this.kindColor = kindColor;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
