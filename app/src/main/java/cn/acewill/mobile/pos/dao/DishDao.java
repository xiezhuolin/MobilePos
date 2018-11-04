package cn.acewill.mobile.pos.dao;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishMenu;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;


/**
 * 和菜品相关的数据库读取都在这个dao中
 * 1. 菜品分类
 * 2. 菜品
 * 3. 菜单
 * Created by Acewill on 2016/8/15.
 */
public class DishDao {
    private static final  String TAG = "DishDao";

    public void saveDishType(List<DishType> dishTypeList) {
        for (DishType dt : dishTypeList) {
            dt.save();
        }
    }

    public List<DishType> getAllDishType() {
        List<DishType> sectionList = new Select().from(DishType.class).queryList();
        return sectionList != null ? sectionList : new ArrayList<DishType>();
    }

    public void saveMenu(List<Menu> dishTypeList) {
        for (Menu dt : dishTypeList) {
            dt.save();
        }
    }

    public List<Menu> getAllMenu() {
        List<Menu> sectionList = new Select().from(Menu.class).queryList();
        return sectionList != null ? sectionList : new ArrayList<Menu>();
    }

    public void saveDish(Dish dish) {
        dish.save();
    }

    public List<Dish> getAllDish() {
        List<Dish> sectionList = new Select().from(Dish.class).queryList();
        return sectionList != null ? sectionList : new ArrayList<Dish>();
    }

    public void saveDishMenuMap(DishMenu dishMenu) {
        dishMenu.save();
    }

    public List<DishMenu> getDishMenuMap() {
        List<DishMenu> sectionList = new Select().from(DishMenu.class).queryList();
        return sectionList != null ? sectionList : new ArrayList<DishMenu>();
    }

}
