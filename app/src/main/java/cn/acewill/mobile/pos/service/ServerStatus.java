package cn.acewill.mobile.pos.service;

import org.greenrobot.eventbus.EventBus;

import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.utils.Constant;


/**
 * Created by Acewill on 2016/8/15.
 */
public class ServerStatus {
    private static ServerStatus instance;
    private boolean running = true; //默认是已经运行 -- 否则登录时会直接用本地登录

    public static ServerStatus getInstance() {
        if (instance == null) {
            instance = new ServerStatus();
        }

        return instance;
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (running && !this.running) {
            //只在第一次变化时记录日志
            System.out.println("---------------server is up");
            EventBus.getDefault().post(new PosEvent(Constant.EventState.SERVER_STATUS_UP));
        }

        if (!running && this.running) {
            //只在第一次变化时记录日志
            System.out.println("---------------server is down");
            EventBus.getDefault().post(new PosEvent(Constant.EventState.SERVER_STATUS_DOWN));
        }

        this.running = running;
    }
}
