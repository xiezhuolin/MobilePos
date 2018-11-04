package cn.acewill.mobile.pos.utils;

import android.content.Context;
import android.util.Log;

import cn.acewill.mobile.pos.model.user.UserData;

/**
 * Created by hzc on 2017/2/9.
 * 用于生产用户操作信息的工具类
 */
public class UserAction {
    /**
     * 打印操作日志
     *
     * @param action 操作
     */
    public static void log(String action, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("操作:").append(action).append("；   员工:").append(UserData.getInstance(context).getUserName())
                .append("； 类:").append(context.getClass().getSimpleName());
        Log.e("action", sb.toString());
    }

    /**
     * 打印操作日志
     *
     * @param action 操作
     * @param method 该操作在的方法
     */
    public static void log(String action, String method, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("方法:").append(method).append("; 操作:").append(action).append("；   员工:").append(UserData.getInstance(context).getUserName())
                .append("； 类:").append(context.getClass().getSimpleName());
        Log.e("action", sb.toString());
    }

    /**
     * 打印操作日志
     *
     * @param action 操作
     * @param method 该操作在的方法 可以传null
     * @param data   操作数据 可以传null
     */
    public static void log(String action, String method, String data, Context context) {
        StringBuilder sb = new StringBuilder();
        if (method != null) {
            sb.append("方法:").append(method).append(";");
        }
        sb.append("操作:").append(action);
        if (data != null) {
            sb.append(";数据：").append(data);
        }
        sb.append("；   员工:").append(UserData.getInstance(context).getUserName())
                .append("； 类:").append(context.getClass().getSimpleName());
        Log.e("action", sb.toString());
    }

    public static void logCancelAction(String method, String data, Context context) {
        log("取消", method, data, context);
    }
    public static void logOkAction(String method, String data, Context context) {
        log("确认", method, data, context);
    }
    public static void logBackAction(String method, String data, Context context) {
        log("返回", method, data, context);
    }

}
