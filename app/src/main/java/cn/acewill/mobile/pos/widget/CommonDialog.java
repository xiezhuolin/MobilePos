package cn.acewill.mobile.pos.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by aqw on 2016/9/13.
 */
public class CommonDialog extends Dialog {

    private Context context;

    public CommonDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public  boolean isShouldHideInput(View v, MotionEvent event) {
        boolean isShould = true;
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件

                isShould =  false;
            } else {
                isShould =  true;
            }
        }
        return isShould;
    }
}
