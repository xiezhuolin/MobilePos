package cn.acewill.mobile.pos.ui;


import cn.acewill.mobile.pos.exception.PosServiceException;

/**
 * Created by DHH on 2016/6/12.
 */
public interface DialogView {
    void showDialog();
    void dissDialog();
    void showError(PosServiceException e);
    <T> void callBackData(T t);
}
