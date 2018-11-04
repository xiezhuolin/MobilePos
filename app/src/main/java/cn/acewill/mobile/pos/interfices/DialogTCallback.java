package cn.acewill.mobile.pos.interfices;

/**
 * Created by DHH on 2016/8/23.
 */
public interface DialogTCallback<T>  {
    /**
     * 确认  dialogStyle： Constant.DialogStyle.ADD_PRINTER=添加  ， Constant.DialogStyle.MODIFY_PRINTER=修改
     */
    public void onConfirm(T t);
    /**
     * 取消
     */
    public void onCancle();
}
