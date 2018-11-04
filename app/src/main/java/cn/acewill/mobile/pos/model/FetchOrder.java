package cn.acewill.mobile.pos.model;

import java.util.List;

import cn.acewill.mobile.pos.model.dish.Dish;


/**
 * Created by DHH on 2017/3/23.
 */

public class FetchOrder {
    private String createOrderTime;
    private List<Dish> fetchDishList;

    public String getCreateOrderTime() {
        return createOrderTime;
    }

    public void setCreateOrderTime(String createOrderTime) {
        this.createOrderTime = createOrderTime;
    }

    public List<Dish> getFetchDishList() {
        return fetchDishList;
    }

    public void setFetchDishList(List<Dish> fetchDishList) {
        this.fetchDishList = fetchDishList;
    }
}
