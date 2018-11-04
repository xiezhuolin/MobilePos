package cn.acewill.mobile.pos.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import cn.acewill.mobile.pos.config.MyApplication;


/**
 * 6.0系统权限申请
 * Created by aqw on 2016/12/28.
 */
public class PermissionsUtil {

    public static final int PERMISSION_REQUEST_CODE = 1;

    public static void checkPermissions(String[] permissions){
        Activity activity = MyApplication.getInstance().getPopActivity();
        if(activity==null){
            return;
        }
        ArrayList<String> permiList = new ArrayList<>();
        for (String p : permissions) {
            if (!checkSelfPermissionWrapper(activity, p)) {
                permiList.add(p);
            }
        }
        if(permiList.size()>0){
            requestPermissions(activity,permiList.toArray(new String[permiList.size()]));
        }
    }

    private static void requestPermissions(Activity activity, String[] permission) {
        ActivityCompat.requestPermissions(activity, permission, PERMISSION_REQUEST_CODE);
    }

    private static boolean checkSelfPermissionWrapper(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

}
