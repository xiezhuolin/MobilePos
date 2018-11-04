package cn.acewill.mobile.pos.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * Created by aqw on 2016/12/27.
 */
public class ThreadUtils {
    private static ThreadUtils instance;
    /** 总共多少任务（根据CPU个数决定创建活动线程的个数,这样取的好处就是可以让手机承受得住） */
    private static final int count = Runtime.getRuntime().availableProcessors() + 2;
    /** 每次执行限定个数个任务的线程池 */
    private static ExecutorService mFixedThreadExecutor = null;

    public static ThreadUtils getInstance(){
        if (instance==null){
            instance = new ThreadUtils();
        }
        return instance;
    }

    private ThreadUtils(){
        Log.i("线程数",count+"");
        mFixedThreadExecutor = Executors.newFixedThreadPool(count);
    }

    //执行任务
    public void execute(Runnable runnable){
        mFixedThreadExecutor.execute(runnable);
    }


}
