package cn.acewill.mobile.pos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import cn.acewill.mobile.pos.model.dish.Dish;


/**
 * Created by DHH on 2017/8/22.
 */

public class SecondScreen implements Serializable{
    private List<Dish> marketActList;
    private BigDecimal price;
    int dishItemCount;

    public List<Dish> getMarketActList() {
        return marketActList;
    }

    public void setMarketActList(List<Dish> marketActList) {
        this.marketActList = marketActList;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDishItemCount() {
        return dishItemCount;
    }

    public void setDishItemCount(int dishItemCount) {
        this.dishItemCount = dishItemCount;
    }
}
