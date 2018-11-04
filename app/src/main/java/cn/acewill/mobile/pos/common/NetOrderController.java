package cn.acewill.mobile.pos.common;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2017/2/7.
 */

public class NetOrderController {
    public static List<Order> netOrderList = new ArrayList<>();//网络订单列表
    public static Map<Long,Order> netOrderMap = new HashMap<>();

    public static List<Order> setNetOrderList(List<Order> netOrderListInfo)
    {
        if(netOrderList!= null && netOrderList.size() >0)
        {
            netOrderList.clear();
        }
        if(netOrderListInfo != null && netOrderListInfo.size() >0)
        {
            for(Order order :netOrderListInfo)
            {
                if(!TextUtils.isEmpty(order.tableid) && ToolsUtils.isNumeric(order.tableid))
                {
                    order.setTableId(Long.valueOf(order.tableid));
                }
                Log.i("NetOrder网上订单id=====",order.getId()+"");
                Order copyOrder = netOrderMap.get(order.getId());
                if(copyOrder == null)
                {
                    netOrderMap.put(order.getId(),order);
                    MyApplication.getInstance().playSound();
                }
                netOrderList.add(order);
            }
        }
        return netOrderList;
    }

    public static List<Order> getNetOrderList() {
        return netOrderList;
    }

    public static Map<Long, Order> getNetOrderMap() {
        return netOrderMap;
    }

    public static void cleanNetOrderData()
    {
        netOrderMap.clear();
        if(netOrderList != null && netOrderList.size() >0)
        {
            netOrderList.clear();
        }
    }

}
