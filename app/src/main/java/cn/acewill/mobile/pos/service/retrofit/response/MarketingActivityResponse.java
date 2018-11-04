package cn.acewill.mobile.pos.service.retrofit.response;

import java.util.List;

import cn.acewill.mobile.pos.model.order.MarketingActivity;


/**
 * Created by Acewill on 2016/8/3.
 */
public class MarketingActivityResponse extends PosResponse {

    private List<MarketingActivity> orderDiscount;

    public List<MarketingActivity> getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(List<MarketingActivity> orderDiscount) {
        this.orderDiscount = orderDiscount;
    }
}
