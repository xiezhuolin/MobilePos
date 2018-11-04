package cn.acewill.mobile.pos.interfices;

import java.util.List;

import cn.acewill.mobile.pos.model.order.Order;


/**
 * Created by DHH on 2017/6/12.
 */

public interface NetOrderinfoCallBack {
    //返回轮训到的网上订单消息
    void getNetOrderInfoList(List<Order> orderList);
    //返回是否正在打打印  true正在打印中  false反之
    void printState(boolean isPrint);
}
