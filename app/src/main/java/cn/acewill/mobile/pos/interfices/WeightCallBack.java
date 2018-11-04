package cn.acewill.mobile.pos.interfices;

/**
 * Created by DHH on 2017/6/12.
 */

public interface WeightCallBack {
    //皮重变化通知
    void changeTareWeight(float tareWeight);
    //净重变化通知
    void changeNetWeight(float netWeight);
    //总重变化通知
    void changeGrossWeight(float netWeight);
    //小数点的位数
    void setPointnumber(int pointnumber);
    //称重服务当前重量是否稳定现实  true表示秤已稳定显示重量 (不跳字) false反之
    void signalLampState(boolean isStable);
    //设置皮重成功
    void setTareNumSuccess(boolean state);
    //设置归零成功
    void setZeroNumSuccess(boolean state);
}
