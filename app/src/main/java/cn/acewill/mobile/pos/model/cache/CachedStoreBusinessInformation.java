package cn.acewill.mobile.pos.model.cache;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.StoreBusinessInformation;

/**
 * Created by DHH on 2017/1/16.
 */

@com.raizlabs.android.dbflow.annotation.Table(name="cached_storebusiness_information",database =AppDatabase.class)
@ModelContainer
public class CachedStoreBusinessInformation extends BaseModel {
    public static Gson gson = new Gson();

    public CachedStoreBusinessInformation() {
    }

    public CachedStoreBusinessInformation(StoreBusinessInformation dish) {
        this.jsonObject = gson.toJson(dish);
    }

    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName( "id" )
    private long id; //班次定义在服务器上的id

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

    public StoreBusinessInformation toStoreBusinessInformation() {
        return gson.fromJson(this.jsonObject, StoreBusinessInformation.class);
    }

}
