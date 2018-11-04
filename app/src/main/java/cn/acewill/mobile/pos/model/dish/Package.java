package cn.acewill.mobile.pos.model.dish;

/**
 * Created by DHH on 2016/8/9.
 */
public class Package extends Dish{
    public Package() {
    }

    public Package(Package dish) {
        this.setDishId(dish.getDishId());
        this.setDishName(dish.getDishName());
        this.setPrice(dish.getPrice());
        this.cost = dish.getPrice();
        this.quantity = dish.quantity;
        this.setIsPackage(dish.getIsPackage());
        if (this.quantity == 0) {
            this.quantity = 1;
        }
    }

    public int extraCost;
    public int count;
}
