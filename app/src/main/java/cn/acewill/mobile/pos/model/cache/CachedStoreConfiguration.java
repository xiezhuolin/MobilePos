package cn.acewill.mobile.pos.model.cache;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.StoreConfiguration;


/**
 * Created by DHH on 2017/1/16.
 */

@com.raizlabs.android.dbflow.annotation.Table(name="cached_store_configuration",database =AppDatabase.class)
@ModelContainer
public class CachedStoreConfiguration extends BaseModel {
    public static Gson gson = new Gson();

    public CachedStoreConfiguration() {
    }

    public CachedStoreConfiguration(StoreConfiguration dish) {
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

    public StoreConfiguration toStoreConfiguration() {
        return gson.fromJson(this.jsonObject, StoreConfiguration.class);
    }

}
