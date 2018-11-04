package cn.acewill.mobile.pos.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-07-21.
 */
public class WaimaiOrderExtraData implements Serializable
{
    private String reduce_fee;
    private String remark;

    public String getReduce_fee() {
        return reduce_fee;
    }

    public void setReduce_fee(String reduce_fee) {
        this.reduce_fee = reduce_fee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
