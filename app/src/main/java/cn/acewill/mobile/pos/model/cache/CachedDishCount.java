package cn.acewill.mobile.pos.model.cache;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.dish.DishCount;


/**
 * Created by DHH on 2017/1/16.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="cached_dish_count",database =AppDatabase.class)
@ModelContainer
public class CachedDishCount  extends BaseModel {
    public static Gson gson = new Gson();

    public CachedDishCount() {
    }

    public CachedDishCount(DishCount dish) {
        this.id = dish.getDishid();
        this.jsonObject = gson.toJson(dish);
    }

    @Column
    @PrimaryKey
    @SerializedName( "id" )
    private long id; //菜品Id

    @Column
    @SerializedName( "json_object" )
    private String jsonObject; //菜品对象对应的json字符串

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public DishCount toDishCount() {
        return gson.fromJson(this.jsonObject, DishCount.class);
    }
}
