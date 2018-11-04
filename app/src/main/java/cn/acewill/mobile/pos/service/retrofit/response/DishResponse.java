package cn.acewill.mobile.pos.service.retrofit.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;


/**
 * {
 "result": 0,
 "content": "",
 "errmsg": "0",
 "dishKindData": [
 {
 "kindID": 1,
 "kindName": "菜本山四绝"
 },
 {
 "kindID": 2,
 "kindName": " 心血小食"
 },
 {
 "kindID": 3,
 "kindName": "山中特饮"
 }
 ]
 }
 * Created by DHH on 2016/7/19.\
 */
public class DishResponse {
    private int result;
    private String errmsg;
    private List<DishType> dishKindData;
    private List<Menu> menuData;
    @SerializedName("dishIDData")
    private List<DishCount> soldOutDishIdList;//这些菜品已经不多，不能出售

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<DishType> getDishKindData() {
        return dishKindData;
    }

    public void setDishKindData(List<DishType> dishKindData) {
        this.dishKindData = dishKindData;
    }

    public List<Menu> getMenuData() {
        return menuData;
    }

    public void setMenuData(List<Menu> menuData) {
        this.menuData = menuData;
    }

    public List<DishCount> getSoldOutDishIdList() {
        return soldOutDishIdList;
    }

    public void setSoldOutDishIdList(List<DishCount> soldOutDishIdList) {
        this.soldOutDishIdList = soldOutDishIdList;
    }
}
