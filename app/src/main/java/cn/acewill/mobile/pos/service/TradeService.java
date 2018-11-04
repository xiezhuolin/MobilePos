package cn.acewill.mobile.pos.service;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.OrderSingleReason;
import cn.acewill.mobile.pos.service.retrofit.RetrofitTradeService;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.utils.Constant;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 结账、退款等
 * Created by aqw on 2016/8/19.
 */
public class TradeService {
    private RetrofitTradeService retrofitTradeService;
    private static RetrofitTradeService retrofitJYJTradeService;
    public static TradeService tradeService;
    public static TradeService tradeJyjService;

    public static TradeService getInstance() throws PosServiceException {
        if (tradeService == null) {
            RetrofitTradeService internalService  = RetrofitFactory.buildService(RetrofitTradeService.class);
            tradeService = new TradeService(internalService);
        }

        return tradeService;
    }


    public static TradeService getJyjTradeService() throws PosServiceException {
        Store store = Store.getInstance(MyApplication.getInstance().getContext());
        String baseUrl = "http://" + store.getServiceAddressJyj() + ":" + store.getStorePortJyj() + "/";
        retrofitJYJTradeService = RetrofitFactory.buildKdsService(baseUrl,RetrofitTradeService.class);
        tradeJyjService = new TradeService();
        return tradeJyjService;
    }

    private TradeService() {
    }

    private TradeService(RetrofitTradeService retrofitTradeService){
        this.retrofitTradeService = retrofitTradeService;
    }


    /**
     * 结账
     * @param paymentList
     * @param orderId
     * @param resultCallback
     */
    public void checkOut(List<PaymentList> paymentList, String orderId, final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        retrofitTradeService.checkOut(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),orderId, paymentList)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
    }

    /**
     * 反结账
     * @param paymentList
     * @param orderId
     * @param resultCallback
     */
    public void reCheckOut(List<PaymentList> paymentList, String orderId, int reasonId,boolean isReCheckOut,final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        retrofitTradeService.reCheckout(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),orderId,reasonId, posInfo.getRealname(),posInfo.getTerminalName(),isReCheckOut,paymentList)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
    }

    /**
     * 获取退单原因
     * @param resultCallback
     */
    public void getSingleReason(final ResultCallback<List<OrderSingleReason>> resultCallback){
        retrofitTradeService.getReason()
                .map(new Func1<PosResponse<List<OrderSingleReason>>, List<OrderSingleReason>>() {
                    @Override
                    public List<OrderSingleReason> call(PosResponse<List<OrderSingleReason>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<OrderSingleReason>>(resultCallback));
    }

    /**
     * 关闭订单
     * @param resultCallback
     */
    public void closeOrder(final int reasonId, final Order order, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitTradeService.closeOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), reasonId,String.valueOf(order.getId()),posInfo.getRealname(),posInfo.getAuthUserName()).map(new Func1<PosResponse, Integer>() {
                @Override
                public Integer call(PosResponse response) {
                    if (response.getResult() == 0) {
                        return 0;
                    }
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退单(先支付后下单使用)
     * @param resultCallback
     */
    public void refund(final Order order, Integer reasonId, String userName, final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitTradeService.refund(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),String.valueOf(order.getId()),reasonId,userName,posInfo.getRealname(),posInfo.getAuthUserName()).map(new Func1<PosResponse, PosResponse>() {
                @Override
                public PosResponse call(PosResponse response) {
                    if (response.isSuccessful()) {
                        //退单的时候  要把先前已经退的菜品给过滤掉
                        int size = order.getItemList().size();
                        for (int i = 0; i < size; i++) {
                            if (order.getItemList().size() == i) {
                                break;
                            }
                            OrderItem orderitem = order.getItemList().get(i);
                            boolean isDeleteItem = false;
                            if (0 >= orderitem.getQuantity()) {
                                order.getItemList().remove(i);
                                isDeleteItem = true;
                            }
                            if (size == 1) {
                                break;
                            }
                            if (isDeleteItem) {
                                i -= 1;
                            }
                        }
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_ORDER, order));
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_KITCHEN_ORDER, order));
                        return response;
                    }

                    PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * JYJ退单(先支付后下单使用)
     * @param resultCallback
     */
    public void refundJyj(final Order order, Integer reasonId, String userName, final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitJYJTradeService.refund(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),String.valueOf(order.getId()),reasonId,userName,posInfo.getRealname(),posInfo.getAuthUserName()).map(new Func1<PosResponse, PosResponse>() {
                @Override
                public PosResponse call(PosResponse response) {
                    if (response.isSuccessful()) {
                        //退单的时候  要把先前已经退的菜品给过滤掉
                        int size = order.getItemList().size();
                        for (int i = 0; i < size; i++) {
                            if (order.getItemList().size() == i) {
                                break;
                            }
                            OrderItem orderitem = order.getItemList().get(i);
                            boolean isDeleteItem = false;
                            if (0 >= orderitem.getQuantity()) {
                                order.getItemList().remove(i);
                                isDeleteItem = true;
                            }
                            if (size == 1) {
                                break;
                            }
                            if (isDeleteItem) {
                                i -= 1;
                            }
                        }
                        return response;
                    }

                    PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退菜(先下单后支付使用)
     * @param orderId
     * @param itemList
     * @param resultCallback
     */
    public void remove(int reasonId, String orderId, List<OrderItem> itemList, final ResultCallback<PosResponse<Order>> resultCallback){
        PosInfo posInfo = PosInfo.getInstance();
        retrofitTradeService.removeDish(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),orderId,reasonId,itemList,posInfo.getRealname(),posInfo.getAuthUserName())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse<Order>>(resultCallback));
    }

    /**
     * 退菜(先支付后下单使用)
     * @param orderId
     * @param resultCallback
     */
    public void refundDish(String orderId, Integer reasonId, String userName,final Order order, final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitTradeService.refundDish(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),orderId,reasonId,userName,order.getItemList(),posInfo.getRealname(),posInfo.getAuthUserName()).map(new Func1<PosResponse, PosResponse>() {
                @Override
                public PosResponse call(PosResponse response) {
                    if (response.isSuccessful()) {
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_DISH, order));
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_DISH_GUEST, order));
                        return response;
                    }

                    PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退菜(先支付后下单使用)
     * @param orderId
     * @param resultCallback
     */
    public void refundJyjDish(String orderId, Integer reasonId, String userName,final Order order, final ResultCallback<PosResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitJYJTradeService.refundDish(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),orderId,reasonId,userName,order.getItemList(),posInfo.getRealname(),posInfo.getAuthUserName()).map(new Func1<PosResponse, PosResponse>() {
                @Override
                public PosResponse call(PosResponse response) {
                    if (response.isSuccessful()) {
                        return response;
                    }

                    PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<PosResponse>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发票冲红
     * @param revokeReson
     * @param resultCallback
     */
    public void revokeInvoice(final String orderId,final String revokeReson,final ResultCallback<Integer> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        try {
            retrofitTradeService.revokeInvoice(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), orderId,revokeReson).map(new Func1<PosResponse, Integer>() {
                @Override
                public Integer call(PosResponse response) {
                    if (response.getResult() == 0) {
                        return 0;
                    }
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
