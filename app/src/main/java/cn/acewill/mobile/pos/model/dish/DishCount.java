package cn.acewill.mobile.pos.model.dish;

/**
 * Created by Acewill on 2016/8/3.
 */
public class DishCount {
    public int dishid;
    public int count; //菜品的数量

    public DishCount() {

    }

    public DishCount(int dishid, int count) {
        this.dishid = dishid;
        this.count = count;
    }

    public int getDishid() {
        return dishid;
    }

    public void setDishid(int dishid) {
        this.dishid = dishid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
