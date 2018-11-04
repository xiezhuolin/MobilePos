package cn.acewill.mobile.pos.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by aqw on 2016/6/20.
 */
public class ScreenUtil {

    public static int[] getScreenSize(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        int[] screens = new int[2];
        screens[0] = width;
        screens[1] = height;
        return screens;
    }
}
