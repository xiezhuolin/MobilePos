package cn.acewill.mobile.pos.model.cache;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;
import cn.acewill.mobile.pos.model.dish.Menu;


/**
 * Created by Acewill on 2016/12/1.
 */
@com.raizlabs.android.dbflow.annotation.Table(name="cached_menu",database = AppDatabase.class)
@ModelContainer
public class CachedMenu extends BaseModel {
    public static Gson gson = new Gson();

    public CachedMenu() {
    }

    public CachedMenu(Menu menu) {
        this.id = menu.getId();
        menu.updateDishMap(); //保存菜品分类和菜品的映射
        this.jsonObject = gson.toJson(menu);
    }

    public Menu toMenu() {
        return gson.fromJson(this.jsonObject, Menu.class);
    }

    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName( "id" )
    private long id; //本地SQLite的id， 菜单是服务器动态生成的，并没有id来区分他们

    @Column
    @SerializedName( "json_object" )
    private String jsonObject; //菜品对象对应的json字符串

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
