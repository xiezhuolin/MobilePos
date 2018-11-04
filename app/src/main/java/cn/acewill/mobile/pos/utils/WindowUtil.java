package cn.acewill.mobile.pos.utils;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 自定义键盘弹出框
 * Created by aqw on 2016/9/9.
 */
public class WindowUtil {

    private static WindowManager windowManager;
    private static WindowManager.LayoutParams wmParams;
    private static KeyboardView keyboardView;
    private static View view;
    private static boolean isShowing = false;

    /**
     * 显示自定义键盘
     *
     * @param context
     * @return
     */
    public static void showKey(Context context, EditText editText) {

        if (!isShowing) {

            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wmParams == null) {
                wmParams = new WindowManager.LayoutParams();
                //设置window type
//                wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;

                //设置图片格式，效果为背景透明
//            wmParams.format = PixelFormat.RGBA_8888;
                //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //调整悬浮窗显示的停靠位置为左侧置顶
                wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
                wmParams.x = 0;
                wmParams.y = 0;

                //设置悬浮窗口长宽数据
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


            }
//            LayoutInflater inflater = LayoutInflater.from(context);
//            view = inflater.inflate(R.layout.layout_keyboard, null);
//            keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_view);
//            windowManager.addView(view, wmParams);
//            new KeyBoardUtil(context, keyboardView, editText);

            isShowing = true;
        }


    }

    /**
     * 隐藏键盘
     */
    public static void hiddenKey() {
        if (windowManager != null && view != null && isShowing) {
            windowManager.removeView(view);
            isShowing = false;
            view = null;
        }
    }

    public static void hiddenSysKey(EditText edit){
        int inType = edit.getInputType();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Class<EditText> cls=EditText.class;
            try {
                Method setShowSoftInputOnFocus=cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(false);
                setShowSoftInputOnFocus.invoke(edit, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else {
            edit.setInputType(android.text.InputType.TYPE_NULL); // disable soft input
            edit.setInputType(inType);
        }
    }

}
