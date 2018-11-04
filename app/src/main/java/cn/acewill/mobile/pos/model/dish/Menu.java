package cn.acewill.mobile.pos.model.dish;

import android.support.v4.util.ArrayMap;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.utils.PinyinUtils;
import cn.acewill.mobile.pos.utils.TimeUtil;


/**
 * 按时段分类的菜单, POS上按照不同的时段显示不同的菜单
 * Created by Acewill on 2016/8/3.
 */
@com.raizlabs.android.dbflow.annotation.Table( name = "menu", database = AppDatabase.class )
@ModelContainer
public class Menu extends BaseModel {
    @Column
    @PrimaryKey
    @SerializedName("dataId")
    private int id;//
    @Column
    private String stime;//时段的开始时间，格式 "08:00",
    @Column
    private String etime;//时段的结束时间， 格式 "22:00",
    @SerializedName("dishType")
    @Column
    private int dishType;//类型 0：堂食，1：外带，2：通用

    public List<Dish> dishData;//菜品列表
    public String timeName;//餐段名称
    public ArrayMap<String,ArrayList<Dish>> dishMap = new ArrayMap<>(); //菜品分类名-->菜品列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Dish> dishDataTemp = new CopyOnWriteArrayList<>();
    public List<Dish> getDishData() {
        if(dishData != null && dishData.size() >0)
        {
            for (Dish dish:dishData)
            {
                Dish tempDish = new Dish(dish);
                dishDataTemp.add(tempDish);
            }
            return dishDataTemp;
        }
        return dishData;
    }

    public void setDishData(List<Dish> dishData) {
        this.dishData = dishData;
    }

    public Menu getDishData(int dishType) {
        // dishType = -1 时不区分堂食外带
        if (this.dishType == dishType||dishType == -1) {
            if (TimeUtil.inHourTime(stime, etime)) {
                return this;
            }
        }
        return null;
    }

    public void updateDishMap() {
        for (Dish dish : dishData) {
            dish.setAlias(PinyinUtils.getFirstSpell(dish.getDishName()));
            ArrayList<Dish> arrayList = dishMap.get(dish.dishKind);
            if (arrayList == null) {
                arrayList = new ArrayList<Dish>();
                dishMap.put(dish.dishKind, arrayList);
            }
            arrayList.add(dish);
        }
    }

    public int getDishType() {
        return dishType;
    }

    public void setDishType(int dishType) {
        this.dishType = dishType;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }
}
