package cn.acewill.mobile.pos.utils;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.dish.Dish;


/**
 * Created by DHH on 2016/12/14.
 */

public class DishCountUtil {
    private static DishCountUtil dishCountUtil = new DishCountUtil();

    public static DishCountUtil getInstance() {
        return dishCountUtil;
    }

    private DishCountUtil() {
    }

    public static List<Dish> dishItemList = new ArrayList<Dish>();

    public void addItem(Dish dish) {
        boolean isHave = false;
        for (Dish item : dishItemList) {
            if (item.getDishId() == dish.getDishId()) {
                isHave = true;
                break;
            }
        }
        if (isHave == false) {
            dishItemList.add(dish);
            notifyContentChange();
        }
    }


    public static void removeItem(Dish dish) {
        for (Dish item : dishItemList) {
            if (item.getDishId() == dish.getDishId()) {
                dishItemList.remove(item);
                break;
            }
        }
        notifyContentChange();
    }


    private static List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public interface ChangeListener {
        public void contentChanged();
    }

    public static void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public static void notifyContentChange() {
        for (ChangeListener listener : listeners) {
            listener.contentChanged();
        }
    }

    public static List<Dish> getDishItemList() {
        return dishItemList;
    }

    public static void setDishItemList(List<Dish> dishItemList) {
        DishCountUtil.dishItemList = dishItemList;
    }
}
