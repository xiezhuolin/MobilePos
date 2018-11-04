package cn.acewill.mobile.pos.service;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.acewill.mobile.pos.dao.TableDao;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.OrderSingleReason;
import cn.acewill.mobile.pos.model.table.Area;
import cn.acewill.mobile.pos.model.table.Section;
import cn.acewill.mobile.pos.model.table.Sections;
import cn.acewill.mobile.pos.model.table.Table;
import cn.acewill.mobile.pos.model.table.TableData;
import cn.acewill.mobile.pos.model.table.TableOrder;
import cn.acewill.mobile.pos.service.retrofit.RetrofitTableService;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.utils.Constant;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Acewill on 2016/6/2.
 */
public class TableService {
    private TableDao tableDao;
    private RetrofitTableService internalService;
    private static TableService tableService;

    public static TableService getInstance() throws PosServiceException {
        if (tableService == null) {
            RetrofitTableService internalService = RetrofitFactory.buildService(RetrofitTableService.class);
            tableService = new TableService(internalService);
        }

        return tableService;
    }

    private TableService(RetrofitTableService internalService) {
        this.internalService = internalService;
    }

    public List<Section> getSections() {
        return tableDao.getAllSections();
    }


    //获取所有桌台基本信息
    public void getTables(String appId, String brandId, String storeId, long id, final ResultCallback<List<Table>> resultCallback) {
        Observable<PosResponse<List<Table>>> service = null;
        if (id > 0) {
            service = internalService.getTablesAreaStatus(appId, brandId, storeId, id);
        } else {
            service = internalService.getTablesAllStatus(appId, brandId, storeId);
        }
        service.map(new Func1<PosResponse<List<Table>>, List<Table>>() {
            @Override
            public List<Table> call(PosResponse<List<Table>> response) {
                if (response.isSuccessful()) {
                    return response.getContent();
                } else {
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Table>>(resultCallback));
    }


    //获取所有桌台基本信息
    public void getAreaList(String appId, String brandId, String storeId, final ResultCallback<List<Area>> resultCallback) {
        internalService.getTablesArea(appId, brandId, storeId).map(new Func1<PosResponse<List<Area>>, List<Area>>() {
            @Override
            public List<Area> call(PosResponse<List<Area>> response) {
                if (response.isSuccessful()) {
                    return response.getContent();
                } else {
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Area>>(resultCallback));
    }

    /**
     * 清理桌台
     *
     * @param tid
     * @param resultCallback
     */
    public void cleanTable(long tid, final ResultCallback<Integer> resultCallback) {
        internalService.cleanTable(tid).map(new Func1<PosResponse<List<Table>>, Integer>() {
            @Override
            public Integer call(PosResponse<List<Table>> response) {
                if (response.isSuccessful()) {
                    return response.getResult();
                } else {
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 设置脏台
     *
     * @param tid
     * @param resultCallback
     */
    public void dirtyTable(long tid, final ResultCallback<Integer> resultCallback) {
        internalService.dirtyTable(tid).map(new Func1<PosResponse<List<Table>>, Integer>() {
            @Override
            public Integer call(PosResponse<List<Table>> response) {
                if (response.isSuccessful()) {
                    return response.getResult();
                } else {
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }


    /**
     * 把一个订单里的某个桌台转移到另外一个桌台上
     *
     * @param orderId
     * @param fromTableId
     * @param toTableId
     * @return
     */
    public boolean switchTable(long orderId, long fromTableId, long toTableId) {
        return false;
    }


    /**
     * 开台
     *
     * @param tableId          桌台id
     * @param numberOfCustomer 顾客人数
     * @param resultCallback
     */
    public void openTable(long tableId, int numberOfCustomer, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.openTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), tableId, Long.valueOf(numberOfCustomer), posInfo.getRealname()).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    public void getTableSections(final ResultCallback<List<Section>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();

        internalService.getTableSections(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<Section>>, List<Section>>() {
            @Override
            public List<Section> call(PosResponse<List<Section>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Section>>(resultCallback));
    }

    /**
     * 获取某个桌台上的订单
     *
     * @param sectionId
     * @param resultCallback
     */
    public void tablesOrderData(long sectionId, final ResultCallback<List<TableOrder>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.tablesOrderData(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), sectionId).map(new Func1<PosResponse<List<TableOrder>>, List<TableOrder>>() {
            @Override
            public List<TableOrder> call(PosResponse<List<TableOrder>> response) {
                if (response.isSuccessful()) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<TableOrder>>(resultCallback));
    }


    /**
     * 加台
     *
     * @param oldTableId
     * @param newTableId
     * @param numberOfCustomer
     * @param resultCallback
     */
    public void addTable(long oldTableId, long newTableId, long numberOfCustomer, final ResultCallback<List<Order>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), oldTableId, newTableId, numberOfCustomer, posInfo.getRealname()).map(new Func1<PosResponse<List<Order>>, List<Order>>() {
            @Override
            public List<Order> call(PosResponse<List<Order>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Order>>(resultCallback));
    }


    /**
     * 获取某个桌台上的订单
     *
     * @param tableId
     * @param resultCallback
     */
    public void ordersTable(long tableId, final ResultCallback<List<Order>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.ordersTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), tableId).map(new Func1<PosResponse<List<Order>>, List<Order>>() {
            @Override
            public List<Order> call(PosResponse<List<Order>> response) {
                if (response.isSuccessful()) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Order>>(resultCallback));
    }


    /**
     * 添加菜品
     *
     * @param order
     * @param resultCallback
     */
    public void appendDish(final Order order, final List<OrderItem> itemList, final ResultCallback<Order> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        order.setWorkShiftId(posInfo.getWorkShiftId());
        internalService.appendDish(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), order.getId(), itemList).map(new Func1<PosResponse<Order>, Order>() {
            @Override
            public Order call(PosResponse<Order> response) {
                if (response.getResult() == 0) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.NETORDER_OFF));
                    //                    //下单成功 -- 打印
                    final Order newOrder = response.getContent();
                    newOrder.setItemList(itemList);
                    newOrder.setPrinterType(Constant.JsToAndroid.JS_A_ADDDISH);
                    newOrder.setCreatedAt(System.currentTimeMillis());
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, newOrder));
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, newOrder));
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Order>(resultCallback));
    }

    /**
     * 退菜菜品
     *
     * @param order
     * @param resultCallback
     */
    public void removeDish(final int reasonId, final Order order, final List<OrderItem> itemList, final ResultCallback<Order> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        order.setWorkShiftId(posInfo.getWorkShiftId());
        try {
            internalService.removeDish(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), order.getId(), itemList, reasonId).map(new Func1<PosResponse<Order>, Order>() {
                @Override
                public Order call(PosResponse<Order> response) {
                    //                System.out.println(ToolsUtils.getPrinterSth(response));
                    if (response.getResult() == 0) {
                        //                    //下单成功 -- 打印
                        final Order newOrder = response.getContent();
                        newOrder.setItemList(itemList);
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_DISH, newOrder));
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RETREAT_DISH_GUEST, newOrder));
                        return response.getContent();
                    }
                    PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                    throw Exceptions.propagate(exception);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Order>(resultCallback));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据区域id获取桌台详情
     *
     * @param sectionId
     * @param resultCallback
     */
    public void getTableInfor(long sectionId, final ResultCallback<List<TableData>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getTableInfor(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), sectionId)
                .map(new Func1<PosResponse<List<TableData>>, List<TableData>>() {
                    @Override
                    public List<TableData> call(PosResponse<List<TableData>> listPosResponse) {
                        if (listPosResponse.getResult() == 0) {
                            return listPosResponse.getContent();
                        }
                        String errmsg = TextUtils.isEmpty(listPosResponse.getErrmsg()) ? "获取桌台信息失败" : listPosResponse.getErrmsg();
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, errmsg);
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<TableData>>(resultCallback));
    }

    /**
     * 转台
     *
     * @param oldTableId     老桌台id
     * @param newTableId     新桌台id
     * @param resultCallback
     */
    public void turnTable(long oldTableId, long newTableId, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.turnTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), oldTableId, newTableId).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 并台
     *
     * @param firstTableId   点击合并的桌台id
     * @param secondTableId  被合并的桌台id
     * @param resultCallback
     */
    public void joinTable(long firstTableId, long secondTableId, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.joinTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), firstTableId, secondTableId).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }


    /**
     * 获取桌台区域
     *
     * @param resultCallback
     */
    public void getSections(final ResultCallback<List<Sections>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getSections(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<Sections>>, List<Sections>>() {
                    @Override
                    public List<Sections> call(PosResponse<List<Sections>> listPosResponse) {
                        if (listPosResponse.getResult() == 0) {
                            return listPosResponse.getContent();
                        }
                        String errmsg = TextUtils.isEmpty(listPosResponse.getErrmsg()) ? "获取桌台区域失败" : listPosResponse.getErrmsg();
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, errmsg);
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Sections>>(resultCallback));
    }

    /**
     * 清台
     *
     * @param tableId        桌台id
     * @param resultCallback
     */
    public void clearTable(long tableId, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.clearTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), tableId).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }


    /**
     * 进入桌台需要绑定
     *
     * @param tableId
     * @param resultCallback
     */
    public void enterTable(long tableId, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.enterTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), posInfo.getTerminalName(), tableId).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    if (TextUtils.isEmpty((String) response.getContent())) {
                        return 0;
                    }
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, "此桌台可能正在被终端" + (String) response.getContent() + "使用中,请确认!");
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 退出桌台需要解绑
     *
     * @param tableId
     * @param resultCallback
     */
    public void exitTable(long tableId, final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.exitTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), posInfo.getTerminalName(), tableId).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 获取退单原因
     *
     * @param resultCallback
     */
    public void getSingleReason(final ResultCallback<List<OrderSingleReason>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getReason(posInfo.getAppId())
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
     * 获取反结原因列表
     * @param resultCallback
     */
    public void getReCheckoutReason(final ResultCallback<List<OrderSingleReason>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.reCheckoutReason(posInfo.getAppId())
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

}
