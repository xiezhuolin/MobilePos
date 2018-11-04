package cn.acewill.mobile.pos.common;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;


/**
 * Created by DHH on 2016/10/28.
 */

public class RetreatDishController {
    public static Order tableOrder;
    public static List<OrderItem> itemList;

    public static List<OrderItem> tempItemList;

    public static List<OrderItem> getItemList() {
        return itemList;
    }

    public static void setItemList(List<OrderItem> itemLists) {
        if(itemList != null && itemList.size() >0)
        {
            itemList.clear();
        }
        itemList = itemLists;
    }

    public static void setTempItemList(List<OrderItem> itemLists) {
        if(tempItemList != null && tempItemList.size() >0)
        {
            tempItemList.clear();
        }
        tempItemList = itemLists;
    }

    public static void addItem(int position ,int count)
    {
        tempItemList.get(position).setRejectedQuantity(count);
        notifyContentChange();
    }

    public static void minusItem( int position,int count)
    {
        tempItemList.get(position).setRejectedQuantity(count);
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

    public static Order getTableOrder() {
        return tableOrder;
    }

    public static void setTableOrder(Order tableOrder) {
        RetreatDishController.tableOrder = tableOrder;
    }

    public static List<OrderItem> getTempItemList() {
        return tempItemList;
    }
}
