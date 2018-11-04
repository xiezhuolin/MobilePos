package cn.acewill.mobile.pos.model;

import java.io.Serializable;

/**
 * 主页面设置下拉列表实体对面
 * Created by DHH on 2017/3/9.
 */
public class MainSelect implements Serializable{
    int selectId;
    String selectName;
    public MainSelect(int selectId,String selectName)
    {
        this.selectId = selectId;
        this.selectName = selectName;
    }

    public int getSelectId() {
        return selectId;
    }

    public void setSelectId(int selectId) {
        this.selectId = selectId;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }
}
