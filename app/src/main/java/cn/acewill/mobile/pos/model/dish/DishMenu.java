package cn.acewill.mobile.pos.model.dish;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.acewill.mobile.pos.factory.AppDatabase;


/**
 * 菜品和菜单的关联表
 * Created by Acewill on 2016/8/18.
 */
//当把一个菜品添加到订单中时，需要详细信息,比如口味和做法
@com.raizlabs.android.dbflow.annotation.Table( name = "dish_menu", database = AppDatabase.class )
@ModelContainer
public class DishMenu extends BaseModel {
    @Column
    @PrimaryKey
    private int dishId;
    @Column
    @PrimaryKey
    private int menuId;

    public DishMenu() {

    }

    public DishMenu(int dishId, int menuId) {
        this.dishId = dishId;
        this.menuId = menuId;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
