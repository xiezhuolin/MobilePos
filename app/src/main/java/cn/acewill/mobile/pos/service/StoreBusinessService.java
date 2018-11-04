package cn.acewill.mobile.pos.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.common.PowerController;
import cn.acewill.mobile.pos.dao.cache.CachedDao;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.OrderItemReportData;
import cn.acewill.mobile.pos.model.OtherFile;
import cn.acewill.mobile.pos.model.Receipt;
import cn.acewill.mobile.pos.model.StoreBusinessInformation;
import cn.acewill.mobile.pos.model.StoreConfiguration;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.WorkShift;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.report.BodyRequest;
import cn.acewill.mobile.pos.model.report.DishReport;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.service.retrofit.RetrofitTerminalService;
import cn.acewill.mobile.pos.service.retrofit.response.LKLResponse;
import cn.acewill.mobile.pos.service.retrofit.response.OtherFileResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.service.retrofit.response.ScreenResponse;
import cn.acewill.mobile.pos.service.retrofit.response.ValidationResponse;
import cn.acewill.mobile.pos.service.retrofit.response.WeiFuTongResponse;
import cn.acewill.mobile.pos.utils.Constant;
import cn.acewill.mobile.pos.utils.TimeUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 和门店运营相关的接口
 * Created by Acewill on 2016/8/5.
 */
public class StoreBusinessService {
    RetrofitTerminalService internalService;
    private CachedDao cachedDao;

    private static StoreBusinessService storeBusinessService;

    public static StoreBusinessService getInstance() throws PosServiceException {
        if (storeBusinessService == null) {
            RetrofitTerminalService internalService = RetrofitFactory.buildService(RetrofitTerminalService.class);
            storeBusinessService = new StoreBusinessService(internalService);
        }

        return storeBusinessService;
    }

    public StoreBusinessService(RetrofitTerminalService internalService) {
        this.internalService = internalService;
        cachedDao = new CachedDao();
    }

    public void startWorkShift(WorkShift workShift, ResultCallback<WorkShift> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //标识是本地创建的
            workShift.setCreatedOffline(true);
            workShift.setId(System.currentTimeMillis());
            //在本次创建班次记录，等到有连接时再上传
            cachedDao.saveWorkshift(workShift);
            resultCallback.onResult(workShift);
            return;
        }

        Observable ov = internalService.startWorkShift(posInfo.getAppId(), posInfo.getStoreId(), workShift).map(new Func1<PosResponse<WorkShift>, WorkShift>() {
            @Override
            public WorkShift call(PosResponse<WorkShift> response) {
                if (response.getResult() == 0) {
                    cachedDao.saveWorkshift(response.getContent());
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());

        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WorkShift>(resultCallback));
    }

    public void endWorkShift(Long workShiftId, WorkShift workShift, ResultCallback<Integer> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.endWorkShift(posInfo.getAppId(), posInfo.getStoreId(), workShiftId, workShift).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }

                PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    public void getOpenWorkShift(String userName, String terminalId, ResultCallback<WorkShift> resultCallback) {
        if (!ServerStatus.getInstance().isRunning()) {
            List<WorkShift> workShiftList = cachedDao.getAllWorkshift();
            if (!workShiftList.isEmpty()) {
                //TODO： 需要根据用户名找没有交班的那个记录
                resultCallback.onResult(workShiftList.get(0));
            } else {
                resultCallback.onError(new PosServiceException(ErrorCode.UNKNOWN_ERROR));
            }

            return;
        }

        PosInfo posInfo = PosInfo.getInstance();
        internalService.getOpenWorkShift(posInfo.getAppId(), posInfo.getStoreId(), userName, terminalId).map(new Func1<PosResponse<WorkShift>, WorkShift>() {
            @Override
            public WorkShift call(PosResponse<WorkShift> response) {
                if (response.getResult() == 0) {
                    if (response.getContent() != null) {
                        cachedDao.saveWorkshift(response.getContent());
                    }
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WorkShift>(resultCallback));
    }

    public void addPrinter(Printer printer, ResultCallback<Printer> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addPrinter(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), printer).map(new Func1<PosResponse<Printer>, Printer>() {
            @Override
            public Printer call(PosResponse<Printer> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Printer>(resultCallback));
    }

    public void listPrinters(ResultCallback<List<Printer>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.listPrinters(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<Printer>>, List<Printer>>() {
            @Override
            public List<Printer> call(PosResponse<List<Printer>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Printer>>(resultCallback));
    }

    public void getWorkShiftDefinition(ResultCallback<List<Definition>> resultCallback) {
        if (!ServerStatus.getInstance().isRunning()) {
            resultCallback.onResult(cachedDao.getAllWorkshiftDefinition());
            return;
        }

        PosInfo posInfo = PosInfo.getInstance();
        internalService.workShiftDefinition(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<Definition>>, List<Definition>>() {
            @Override
            public List<Definition> call(PosResponse<List<Definition>> response) {
                if (response.getResult() == 0) {
                    cachedDao.saveWorkshiftDefinition(response.getContent());
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Definition>>(resultCallback));
    }

    public void deletePrinter(Integer printerId, ResultCallback<Integer> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.deletePrinter(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), printerId.toString()).map(new Func1<PosResponse<Integer>, Integer>() {
            @Override
            public Integer call(PosResponse<Integer> response) {
                if (response.getResult() == 0) {
                    return response.getResult();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    public void updatePrinter(Integer printerId, Printer printer, ResultCallback<Printer> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.updatePrinter(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), printerId, printer).map(new Func1<PosResponse<Printer>, Printer>() {
            @Override
            public Printer call(PosResponse<Printer> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Printer>(resultCallback));
    }

    public void listReceipts(ResultCallback<List<Receipt>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.listReceipts(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<Receipt>>, List<Receipt>>() {
            @Override
            public List<Receipt> call(PosResponse<List<Receipt>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Receipt>>(resultCallback));
    }

    //获取门店的一些基本配置， 比如小票，打印机，扫码方式， 默认支付流程等等
    public void getStoreBusinessInformation(ResultCallback<StoreBusinessInformation> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getStoreBusinessInformation());
            return;
        }

        Observable ov = internalService.getStoreBusinessInformation(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<StoreBusinessInformation>, StoreBusinessInformation>() {
            @Override
            public StoreBusinessInformation call(PosResponse<StoreBusinessInformation> response) {
                if (response.getResult() == 0) {
                    //把打印机信息加到打印机管理器中
//                    PrintManager.getInstance().addPrinterList(response.getContent().getPrinterList());
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<StoreBusinessInformation>() {
            @Override
            public void onResult(StoreBusinessInformation result) {
                //从服务器成功获取后更新到本地数据库
                cachedDao.saveStoreBusinessInformation(result);
            }

            @Override
            public void onError(PosServiceException e) {
                Log.e("LOGIN", "" + e.getMessage());
            }
        }));

        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<StoreBusinessInformation>(resultCallback));
    }


    /**
     * 获取文件
     *
     * @param resultCallback
     */
    public void getOtherFile(ResultCallback<List<OtherFile>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getOtherFile(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<OtherFileResponse, List<OtherFile>>() {

                    @Override
                    public List<OtherFile> call(OtherFileResponse otherFileResponse) {
                        if (otherFileResponse.getResult() == 0) {
                            return otherFileResponse.getFiles();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, otherFileResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<OtherFile>>(resultCallback));
    }

    /**
     * 获取当天和当前班次报表数据
     *
     * @param workshiftId
     * @param resultCallback
     */
    public void getReportData(String workshiftId, ResultCallback<List<OrderItemReportData>> resultCallback) {
        if (!ServerStatus.getInstance().isRunning()) {
            //从本地的订单中统计数据
            List<OrderItemReportData> orderItemReportDataList = new ArrayList<>();
            OrderItemReportData workshiftReportData = new OrderItemReportData();
            orderItemReportDataList.add(workshiftReportData);

            OrderItemReportData dailyReportData = new OrderItemReportData();
            orderItemReportDataList.add(dailyReportData);
            resultCallback.onResult(orderItemReportDataList);
            return;
        }

        PosInfo posInfo = PosInfo.getInstance();
        internalService.getReportData(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), workshiftId)
                .map(new Func1<PosResponse<List<OrderItemReportData>>, List<OrderItemReportData>>() {
                    @Override
                    public List<OrderItemReportData> call(PosResponse<List<OrderItemReportData>> listPosResponse) {
                        if (listPosResponse.getResult() == 0) {
                            return listPosResponse.getContent();
                        }
                        String errmsg = TextUtils.isEmpty(listPosResponse.getErrmsg()) ? "获取报表数据失败" : listPosResponse.getErrmsg();
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, errmsg);
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<OrderItemReportData>>(resultCallback));
    }

    /**
     * 获取菜品份数，显示沽清
     *
     * @param resultCallback
     */
    public void getDishCounts(ResultCallback<List<DishCount>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            resultCallback.onResult(cachedDao.getAllDishCount());
            return;
        }

        Observable ov = internalService.getDishCounts(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<DishCount>>, List<DishCount>>() {
            @Override
            public List<DishCount> call(PosResponse<List<DishCount>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<List<DishCount>>() {
            @Override
            public void onResult(List<DishCount> result) {
                //从服务器成功获取后更新到本地数据库
                cachedDao.saveDishCount(result);
            }

            @Override
            public void onError(PosServiceException e) {
                Log.e("dishCount", "" + e.getMessage());
            }
        }));


        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<DishCount>>(resultCallback));
    }

    /**
     * 获取当天菜品销售报表
     *
     * @param resultCallback
     */
    public void getDishReport(ResultCallback<List<DishReport>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        BodyRequest bodyRequest = new BodyRequest();
        bodyRequest.setAppId(posInfo.getAppId());
        bodyRequest.setBrandId(posInfo.getBrandId());
        bodyRequest.setStoreId(posInfo.getStoreId());
        bodyRequest.setStartTime(TimeUtil.getTimeDayStr() + " 00:00:00");
        bodyRequest.setEndTime(TimeUtil.getTimeDayStr() + " 23:59:59");
        internalService.getReportDish("total", "DESC", bodyRequest)
                .map(new Func1<PosResponse<List<DishReport>>, List<DishReport>>() {
                    @Override
                    public List<DishReport> call(PosResponse<List<DishReport>> listPosResponse) {
                        if (listPosResponse.getResult() == 0) {
                            return listPosResponse.getContent();
                        }
                        String errmsg = TextUtils.isEmpty(listPosResponse.getErrmsg()) ? "获取菜品数据失败" : listPosResponse.getErrmsg();
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, errmsg);
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<DishReport>>(resultCallback));
    }


    /**
     * 通过mac地址获取门店信息
     *
     * @param macAddress
     * @param resultCallback
     */
    public void getTerminalInfo(Context context, String macAddress, ResultCallback<TerminalInfo> resultCallback) {
        if (!ToolsUtils.isNetworkConnected(context)) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getTerminalInfo());
            return;
        }
        internalService.getTerminalInfo(macAddress).map(new Func1<PosResponse<TerminalInfo>, TerminalInfo>() {
            @Override
            public TerminalInfo call(PosResponse<TerminalInfo> response) {
                if (response.getResult() == 0) {
                    cachedDao.saveTerminalInfo(response.getContent());
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<TerminalInfo>(resultCallback));
    }
    /**
     * 取消绑定终端用户所属的门店
     *
     * @param userName
     * @param password
     * @param terminalMac
     * @param resultCallback
     */
    public void unbindStore(String userName, String password, String terminalMac, ResultCallback<Integer> resultCallback) {
        internalService.unbindStore(userName, password, terminalMac).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 退菜/单时判断输入的用户权限
     *
     * @param username
     * @param password
     * @param resultCallback
     */
    public void checkUserAuthority(String username, String password, ResultCallback<Boolean> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.checkUserAuthority(username, password, posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), PowerController.REFUND_DISH)
                .map(new Func1<PosResponse, Boolean>() {
                    @Override
                    public Boolean call(PosResponse response) {
                        if (response.getResult() == 0) {
                            return true;
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Boolean>(resultCallback));
    }

    /**
     * 绑定终端到该用户所属的门店
     *
     * @param terminalMac
     */
    public void bindStore(String terminalMac, ResultCallback<TerminalInfo> resultCallback) {
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getBindTerminalInfo());
            return;
        }
        Observable ov = internalService.bindStore(terminalMac, Constant.MOBILE_POS).map(new Func1<PosResponse<TerminalInfo>, TerminalInfo>() {
            @Override
            public TerminalInfo call(PosResponse<TerminalInfo> response) {
                if (response.getResult() == 0) {
                    cachedDao.saveBindTerminalInfo(response.getContent());
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<TerminalInfo>(resultCallback));
    }

    /**
     * paymax支付
     *
     * @param amount
     * @param subject
     * @param body
     * @param order_no
     * @param channel
     * @param client_ip
     * @param user_id
     * @param resultCallback
     */
    public void creatPaymaxCharge(String amount, String subject, String body, String order_no, String channel, String client_ip, String user_id, ResultCallback<LKLResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.creatPaymaxCharge(posInfo.getAppId(), posInfo.getStoreId(), amount, subject, body, order_no, channel, client_ip, user_id)
                .map(new Func1<PosResponse<LKLResponse>, LKLResponse>() {
                    @Override
                    public LKLResponse call(PosResponse<LKLResponse> response) {
                        if (response.getResult() == 0) {
                            return response.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<LKLResponse>(resultCallback));
    }


    /**
     * 查询paymax支付结果
     *
     * @param chargeid       Charge对象ID
     * @param resultCallback
     */
    public void retrievePaymax(String chargeid, ResultCallback<LKLResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.retrievePaymax(posInfo.getAppId(), posInfo.getStoreId(), chargeid)
                .map(new Func1<PosResponse<LKLResponse>, LKLResponse>() {
                    @Override
                    public LKLResponse call(PosResponse<LKLResponse> response) {
                        if (response.getResult() == 0) {
                            return response.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<LKLResponse>(resultCallback));
    }

    public void getWayWeiFuTong(String auth_code, String body, Integer total_fee, String out_trade_no, ResultCallback<WeiFuTongResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.gateway(posInfo.getAppId(), posInfo.getStoreId(), auth_code, "192.168.1.168", body, total_fee, out_trade_no).map(new Func1<PosResponse<WeiFuTongResponse>, WeiFuTongResponse>() {
            @Override
            public WeiFuTongResponse call(PosResponse<WeiFuTongResponse> response) {
                ToolsUtils.writeUserOperationRecords(ToolsUtils.getPrinterSth(response));
                if (response.getResult() == 0) {
                    return response.getData();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WeiFuTongResponse>(resultCallback));
    }

    public void queryWeiFuTong(String out_trade_no, String transaction_id, ResultCallback<WeiFuTongResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.queryWeiFuTong(posInfo.getAppId(), posInfo.getStoreId(), out_trade_no, transaction_id)
                .map(new Func1<PosResponse<WeiFuTongResponse>, WeiFuTongResponse>() {
                    @Override
                    public WeiFuTongResponse call(PosResponse<WeiFuTongResponse> response) {
                        ToolsUtils.writeUserOperationRecords(ToolsUtils.getPrinterSth(response));
                        if (response.getResult() == 0) {
                            return response.getData();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WeiFuTongResponse>(resultCallback));
    }

    /**
     * 查询门店设置信息
     *
     * @param resultCallback
     */
    public void getStoreConfiguration(ResultCallback<StoreConfiguration> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getStoreConfiguration());
            return;
        }

        Observable ov = internalService.getStoreConfiguration(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<StoreConfiguration>, StoreConfiguration>() {
            @Override
            public StoreConfiguration call(PosResponse<StoreConfiguration> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<StoreConfiguration>() {
            @Override
            public void onResult(StoreConfiguration result) {
                //从服务器成功获取后更新到本地数据库
                cachedDao.saveStoreConfiguration(result);
            }

            @Override
            public void onError(PosServiceException e) {
                Log.e("LOGIN", "" + e.getMessage());
            }
        }));
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<StoreConfiguration>(resultCallback));
    }


    /**
     * 获取交接班记录
     *
     * @param startTime
     * @param endTime
     * @param resultCallback
     */
    public void getWorkShiftHistory(final String startTime, final String endTime, final ResultCallback<List<WorkShift>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getWorkShiftHistory(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), startTime, endTime).map(new Func1<PosResponse<List<WorkShift>>, List<WorkShift>>() {
            @Override
            public List<WorkShift> call(PosResponse<List<WorkShift>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }

                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<WorkShift>>(resultCallback));
    }


    /**
     * 获取副屏配置项
     *
     * @param resultCallback
     */
    public void getScreenConfiguration(ResultCallback<ScreenResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getScreenInfo());
            return;
        }
        Observable ov = internalService.getScreenConfiguration(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<ScreenResponse>, ScreenResponse>() {
                    @Override
                    public ScreenResponse call(PosResponse<ScreenResponse> response) {
                        ToolsUtils.writeUserOperationRecords(ToolsUtils.getPrinterSth(response));
                        if (response.getResult() == 0) {
                            return response.getData();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<ScreenResponse>() {
            @Override
            public void onResult(ScreenResponse result) {
                //从服务器成功获取后更新到本地数据库
                cachedDao.saveScreenInfo(result);
            }
            @Override
            public void onError(PosServiceException e) {
                Log.e("LOGIN", "" + e.getMessage());
            }
        }));
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<ScreenResponse>(resultCallback));
    }


    /**
     * 根据券码查询券的信息
     *
     * @param resultCallback
     */
    public void validationSetout(String code, ResultCallback<ValidationResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.validationSetout(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), code)
                .map(new Func1<PosResponse<ValidationResponse>, ValidationResponse>() {
                    @Override
                    public ValidationResponse call(PosResponse<ValidationResponse> response) {
                        ToolsUtils.writeUserOperationRecords(ToolsUtils.getPrinterSth(response));
                        if (response.getResult() == 0) {
                            return response.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<ValidationResponse>(resultCallback));
    }

    /**
     * 执行验券
     *
     * @param code
     * @param orderId
     * @param resultCallback
     */
    public void executeCode(String code, Long orderId, ResultCallback<ValidationResponse> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.executeCode(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), posInfo.getRealname(), orderId, code)
                .map(new Func1<PosResponse<ValidationResponse>, ValidationResponse>() {
                    @Override
                    public ValidationResponse call(PosResponse<ValidationResponse> response) {
                        ToolsUtils.writeUserOperationRecords(ToolsUtils.getPrinterSth(response));
                        if (response.getResult() == 0) {
                            return response.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<ValidationResponse>(resultCallback));
    }


}
