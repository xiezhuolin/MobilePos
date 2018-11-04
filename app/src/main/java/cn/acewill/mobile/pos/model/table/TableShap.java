package cn.acewill.mobile.pos.model.table;

/**
 *
 * Created by aqw on 2016/12/19.
 */
public enum TableShap {
    circle(0),//圆桌
    rect(1);//方桌

    private int shap;

    TableShap(int shap){
        this.shap = shap;
    }
}
