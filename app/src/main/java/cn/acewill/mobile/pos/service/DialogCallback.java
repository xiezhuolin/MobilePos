package cn.acewill.mobile.pos.service;

/**
 * Created by DHH on 2016/8/23.
 */
public interface DialogCallback<T>  {
    /**
     * 确认  dialogStyle： Constant.DialogStyle.ADD_PRINTER=添加  ， Constant.DialogStyle.MODIFY_PRINTER=修改
     */
    public void onConfirm();
    /**
     * 取消
     */
    public void onCancle();
}
