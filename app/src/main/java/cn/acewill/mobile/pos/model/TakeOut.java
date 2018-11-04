package cn.acewill.mobile.pos.model;

import java.math.BigDecimal;

/**
 * Created by DHH on 2016/12/9.
 */

public class TakeOut {
    public String takeOutStr;
    public BigDecimal waiDai_cost;

    public TakeOut(BigDecimal waiDai_cost, String takeOutStr) {
        this.waiDai_cost = waiDai_cost;
        this.takeOutStr = takeOutStr;
    }

    public TakeOut()
    {

    }

    public String getTakeOutStr() {
        return takeOutStr;
    }

    public void setTakeOutStr(String takeOutStr) {
        this.takeOutStr = takeOutStr;
    }

    public BigDecimal getWaiDai_cost() {
        return waiDai_cost;
    }

    public void setWaiDai_cost(BigDecimal waiDai_cost) {
        this.waiDai_cost = waiDai_cost;
    }
}
