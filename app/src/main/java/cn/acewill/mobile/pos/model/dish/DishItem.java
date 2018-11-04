package cn.acewill.mobile.pos.model.dish;

/**
 * Created by DHH on 2016/12/19.
 */

public class DishItem {
    public String itemStr;
    public int itemId;
    public DishItem(String itemStr,int itemId)
    {
        this.itemId = itemId;
        this.itemStr = itemStr;
    }

    public String getItemStr() {
        return itemStr;
    }

    public void setItemStr(String itemStr) {
        this.itemStr = itemStr;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
