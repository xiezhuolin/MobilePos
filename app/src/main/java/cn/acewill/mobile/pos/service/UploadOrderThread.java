package cn.acewill.mobile.pos.service;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import cn.acewill.mobile.pos.dao.cache.CachedDao;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.cache.CachedOrder;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.utils.FileLog;
import rx.schedulers.Schedulers;

/**
 * 上传本地缓存的订单
 * 场景： 门店的公网不通， 在这期间下了一些订单， 在公网连上后，需要自动上传这些订单到后台服务器
 * Created by Acewill on 2016/12/3.
 */

public class UploadOrderThread extends Thread {
    private static final String TAG = "UploadOrderThread";
    private static String LOG_TAG   = "UploadOrderThread";
    private CachedDao cachedDao;
    private OrderService orderService;
    private Boolean createdOrderResult = false;
    private static boolean localOrder = true;
    private static boolean loggedOn = false;


    public UploadOrderThread() {
        cachedDao = new CachedDao();
        try {
            orderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    public static void notifyOrderCreated() {
        localOrder = true;
    }


    public static void notifyLoggedOn() {
        loggedOn = true;
    }

    //毫秒数
    private void sleep(int mseconds) {
        try {
            Thread.sleep(mseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //同步下单接口, 下单到后台的接口是异步的，需要封装成同步接口，这样可以在for循环中使用
    private boolean createOrderSync(final Order order) {
        Log.e(TAG, "getOrderId");
        FileLog.log(TAG + ">getOrderId");
        orderService.getNextOrderId(new ResultCallback<Long>() {
            @Override
            public void onResult(Long result) {
                PosInfo posInfo = PosInfo.getInstance();
                order.setAppId(posInfo.getAppId());
                order.setBrandId(posInfo.getBrandId());
                order.setStoreId(posInfo.getStoreId());
                order.setComment("本地订单id:" + order.getId());
                order.setId(result);

                orderService.getInternalService().createOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), order)
                        .subscribeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<PosResponse<Order>>() {
                    @Override
                    public void onResult(PosResponse<Order> result) {
                        if (result.getResult() == 0) {
                            Log.i(LOG_TAG, "succeed to upload order: " + order.getId());
                            createdOrderResult = true;
                            synchronized (createdOrderResult) {
                                createdOrderResult.notify();
                            }
                        } else {
                            Log.i(LOG_TAG, "failed to upload order: " + order.getId());
                            //说明订单已经被门店接收 删除本地订单
                            if (result.getResult() == 1064) {
                                cachedDao.deleteOrder(result.getContent());
                            }
                            createdOrderResult = false;
                            synchronized (createdOrderResult) {
                                createdOrderResult.notify();
                            }
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i(LOG_TAG, "failed to upload order: ");
                        createdOrderResult = false;
                        synchronized (createdOrderResult) {
                            createdOrderResult.notify();
                        }
                    }
                }));
            }

            @Override
            public void onError(PosServiceException e) {

            }
        });

        synchronized (createdOrderResult) {
            try {
                createdOrderResult.wait(20000);
            } catch (InterruptedException e) {
                //服务器超时 -- 这时候当做下单失败， 后续重新下单时 服务器需要检查
                createdOrderResult = false;
                e.printStackTrace();
            }
        }

        return createdOrderResult;
    }


    public void run() {
        Log.i(LOG_TAG, "start thread");

        while (true) {
            if (!ServerStatus.getInstance().isRunning() || !localOrder || !loggedOn) {
                //如果服务器无法连接，或者没有订单, 或者还没有登录， 忽略本次处理
                sleep(15000);
                continue;
            }

            //有本地订单， 并且可以连上服务器
            try {
                List<Order> orderList = cachedDao.getAllOrders();

                if (orderList.isEmpty()) {
                    //本地已经没有订单可以上传
                    localOrder = false;
                    continue;
                }

                Log.i(LOG_TAG, "=======================begin processing local order==========================");
                for (Order order : orderList) {
                    long originalId = order.getId();
                    createOrderSync(order);
                    order.setId(originalId);

                    if (createdOrderResult) {
                        List<CachedOrder> cachedDishList = new Select().from(CachedOrder.class).queryList();
                        //下单成功， 删除本地的缓存订单
                        cachedDao.deleteOrder(order);
                    }
                    //每个订单上传完毕后，休眠3秒再继续
                    sleep(3000);
                }

                Log.i(LOG_TAG, "=======================end processing local order==========================");


            } catch (Throwable t) {
                t.printStackTrace();
                Log.e(LOG_TAG, "failed to process local order");
            }
        }
    }
}
