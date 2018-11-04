package cn.acewill.mobile.pos.model;

/**
 * Created by aqw on 2016/9/12.
 */
public class KdsDishItem {
    public String did;//菜品的ID
    public String name;//菜品名称
    public int count;//菜品数量
    public String dishKind;//菜品类别名称
    public String cook;//菜品做法
    public String alias;//菜品别名
    public String price;//菜品价格
    public String comment;//菜品备注
    public String seq;//菜品品项编码
    public String oid;//订单ID
    public int deletecount;//删除的份数；不传默认菜品全部推掉；
}
