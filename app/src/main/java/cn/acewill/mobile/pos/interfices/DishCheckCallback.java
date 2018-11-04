package cn.acewill.mobile.pos.interfices;

import java.util.List;

/**
 * Created by DHH on 2016/8/23.
 */
public interface DishCheckCallback<T>  {
    /**
     * 有库存
     */
    public void haveStock();
    /**
     * 无库存
     */
    public void noStock(List<T> dataList);
}
