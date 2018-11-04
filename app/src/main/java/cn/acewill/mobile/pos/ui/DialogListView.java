package cn.acewill.mobile.pos.ui;


import java.util.List;

import cn.acewill.mobile.pos.exception.PosServiceException;


/**
 * Created by DHH on 2016/6/12.
 */
public interface DialogListView {
    void showDialog();
    void dissDialog();
    void showError(PosServiceException e);
    <T> void callBackData(List<T> dataList);
}
