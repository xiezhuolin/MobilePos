package cn.acewill.mobile.pos.model.payment;

/**
 * Created by DHH on 2017/3/21.
 */

public enum WaimaiType {
    MEI_TUAN(0), // 美团外卖
    ELEME(1), // 饿了么外卖
    BAI_DU(2), // 百度外卖
    WEI_XIN(3), // 微信外卖
    LOCAL(4); //门店自营外卖

    private int type;

    WaimaiType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
