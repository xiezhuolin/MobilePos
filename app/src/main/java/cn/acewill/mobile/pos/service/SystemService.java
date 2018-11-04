package cn.acewill.mobile.pos.service;


import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.dao.SystemVariablesDao;
import cn.acewill.mobile.pos.dao.cache.CachedDao;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.CookieInterceptor;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.Discount;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.Market;
import cn.acewill.mobile.pos.model.OtherFile;
import cn.acewill.mobile.pos.model.StandByCash;
import cn.acewill.mobile.pos.model.SystemVariables;
import cn.acewill.mobile.pos.model.TerminalVersion;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.user.Staff;
import cn.acewill.mobile.pos.model.user.User;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterTemplates;
import cn.acewill.mobile.pos.printer.PrinterVendors;
import cn.acewill.mobile.pos.service.retrofit.RetrofitSystemService;
import cn.acewill.mobile.pos.service.retrofit.response.OtherFileResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.service.retrofit.response.TerminalLoginResponse;
import cn.acewill.mobile.pos.service.retrofit.response.UserResponse;
import cn.acewill.mobile.pos.utils.Constant;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 对网络服务的封装，这样可以直接在UI线程中使用
 * Created by Acewill on 2016/6/13.
 */
public class SystemService {
    private RetrofitSystemService internalService;
    private static SystemService systemService;
    private SystemVariablesDao systemVariablesDao;
    private CachedDao cachedDao;

    public static SystemService getInstance() throws PosServiceException {
        if (systemService == null) {
            RetrofitSystemService internalService = RetrofitFactory.buildService(RetrofitSystemService.class);
            systemService = new SystemService(internalService);
        }

        return systemService;
    }

    public SystemService(RetrofitSystemService internalService) {
        this.internalService = internalService;
        systemVariablesDao = new SystemVariablesDao();
        cachedDao = new CachedDao();
    }

    public void terminalLogin(final ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        final Store store = Store.getInstance(MyApplication.getInstance().getContext());
        if (!ServerStatus.getInstance().isRunning()) {

            SystemVariables sv = systemVariablesDao.getValue("terminal_id");
            if (sv != null) {
                posInfo.setTerminalId(sv.getValue());
            } else {
                posInfo.setTerminalId("-1");
            }

            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(0);
            return;
        }

        Observable ov = internalService.terminalLogin(
                posInfo.getAppId(),
                posInfo.getBrandId(),
                posInfo.getStoreId(),
                posInfo.getTerminalName(),
                posInfo.getTerminalMac(),
                posInfo.getReceiveNetOrder(),
                posInfo.getCurrentVersion(),
                posInfo.getVersionId(),
                posInfo.getTerminalIp()
        )
                .map(new Func1<Response<TerminalLoginResponse>, Integer>() {
                    @Override
                    public Integer call(Response<TerminalLoginResponse> response) {
                        TerminalLoginResponse posResponse = response.body();
                        if (response.raw().code() != 200) {
                            //    Log.e("SystemService","failed to login terminal" + response.raw().message());
                        }

                        if (posResponse != null && posResponse.getResult() == 0) {
                            System.out.println(posResponse.getToken());
                            CookieInterceptor.setToken(posResponse.getToken());
                            if (!TextUtils.isEmpty(posResponse.getBindUUID()) && !TextUtils.isEmpty(store.getBindUUID())) {
                                if (!posResponse.getBindUUID().equals(store.getBindUUID())) {
                                    PosServiceException exception = new PosServiceException(ErrorCode.TERMINAL_LOGIN_FAIL, "验证终端失败,终端授权码已被使用,请清除缓存后重新绑定终端!");
                                    throw Exceptions.propagate(exception);
                                }
                            }
                            //登录成功
                            PosInfo posInfo = PosInfo.getInstance();
                            posInfo.setToken(posResponse.getToken());
                            posInfo.setTerminalId(posResponse.getTerminalid());
                            return 0;
                        }

                        PosServiceException exception = new PosServiceException(ErrorCode.TERMINAL_LOGIN_FAIL, posResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                }).subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<Integer>() {
            @Override
            public void onResult(Integer result) {
                //从服务器成功获取后更新到本地数据库
                systemVariablesDao.save(new SystemVariables("server.url", PosInfo.getInstance().getServerUrl()));
                systemVariablesDao.save(new SystemVariables("terminal_name", PosInfo.getInstance().getTerminalName()));
                systemVariablesDao.save(new SystemVariables("terminal_id", PosInfo.getInstance().getTerminalId()));
                systemVariablesDao.save(new SystemVariables("terminal_mac", PosInfo.getInstance().getTerminalMac()));
            }

            @Override
            public void onError(PosServiceException e) {
                Log.e("LOGIN", "" + e.getMessage());
            }
        }));

        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 登录，这个是人员的登录接口
     *
     * @param username
     * @param password
     * @return 成功返回用户对象，否则返回null
     * @throws IOException
     */
    public void login(String username, final String password, final ResultCallback resultCallback) {
        if (!ServerStatus.getInstance().isRunning()) {

            User user = cachedDao.findUserByName(username);
            if (user == null) {
                PosServiceException ps = new PosServiceException(ErrorCode.INVALID_USERNAME_PASSWORD, "未连接服务器!");
                resultCallback.onError(ps);
            } else {
                UploadOrderThread.notifyLoggedOn();
                resultCallback.onResult(user);
            }
            return;
        }
        PosInfo posInfo = PosInfo.getInstance();
        Observable ov = internalService.login(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),posInfo.getTerminalMac(), username, password).map(new Func1<Response<UserResponse>, User>() {
            @Override
            public User call(Response<UserResponse> response) {
                Map<String, List<String>> headersMap = response.headers().toMultimap();
                List<String> cookies = headersMap.get("Set-Cookie");
                if (cookies != null) {
                    for (String c : cookies) {
                        //设置cookie，后续的有些操作需要cookie才能调用
                        CookieInterceptor.addCookie(c);
                        Log.e("cookie:", c);
                    }
                }
                UserResponse userResponse = response.body();
                if (userResponse.getResult() == 0) {
                    User user = new User();
                    //                    UserRet userRet = userResponse.getContent();
                    //                    user.setName(userRet.username);
                    user.setUserRet(userResponse.getContent());

                    return user;

                }
                PosServiceException exception = new PosServiceException(ErrorCode.INVALID_USERNAME_PASSWORD, userResponse.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());

        ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<User>() {
            @Override
            public void onResult(User result) {
                //成功后记录用户信息
                //通知上传线程已经登录
                UploadOrderThread.notifyLoggedOn();
                result.setPassword(password);
                result.setName(result.getUserRet().getUsername());
                cachedDao.saveUser(result);
            }

            @Override
            public void onError(PosServiceException e) {

            }
        }));

        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<User>(resultCallback));
    }

    public void isServerRunning(final ResultCallback resultCallback) {
        internalService.heartbeat().map(new Func1<Response<UserResponse>, Boolean>() {
            @Override
            public Boolean call(Response<UserResponse> response) {
                if (response.body().getResult() == 0) {
                    return true;
                }

                System.out.println("isServerRunning --" + response.isSuccessful());
                PosServiceException exception = new PosServiceException(ErrorCode.INVALID_USERNAME_PASSWORD, "");
                throw Exceptions.propagate(exception);

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Boolean>(resultCallback));
    }


    /**
     * 获取版本更新信息
     *
     * @param resultCallback
     */
    public void getTerminalVersions(ResultCallback<TerminalVersion> resultCallback) {
        internalService.getTerminalVersions(Constant.CHECK_TERMINAL_ID).map(new Func1<PosResponse<TerminalVersion>, TerminalVersion>() {
            @Override
            public TerminalVersion call(PosResponse<TerminalVersion> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<TerminalVersion>(resultCallback));
    }

    public void upLoadLogFile(File file, ResultCallback resultCallback) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "This is a description";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        PosInfo posInfo = PosInfo.getInstance();
        internalService.upLoadLog(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), posInfo.getTerminalId(), description, body).map(new Func1<PosResponse, Integer>() {
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
     * 获取后台商家设置的LOGO图片
     *
     * @param resultCallback
     */
    public void getLogoPath(ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //断网不显示logo
            resultCallback.onResult("");
            return;
        }
        internalService.getLogoResource(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<OtherFileResponse, String>() {
            @Override
            public String call(OtherFileResponse orderPosResponse) {
                PosServiceException exception = null;
                String logoPath = "";
                if (orderPosResponse.isSuccessful()) {
                    List<OtherFile> listFile = orderPosResponse.getFiles();
                    if (listFile != null && listFile.size() > 0) {
                        logoPath = listFile.get(0).getFilename();
                        return logoPath;
                    }
                    return logoPath;
                } else {
                    exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse.getErrmsg());
                }
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<String>(resultCallback));
    }

    /**
     * 获取交接班报表数据
     *
     * @param workShiftReport
     * @param resultCallback
     */
    public void workShiftReport(Integer workShiftReport, String endWorkAmount, ResultCallback<WorkShiftNewReport> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.workShiftReport(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), workShiftReport, endWorkAmount).map(new Func1<PosResponse<WorkShiftNewReport>, WorkShiftNewReport>() {
            @Override
            public WorkShiftNewReport call(PosResponse<WorkShiftNewReport> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WorkShiftNewReport>(resultCallback));
    }

    /**
     * 获取日结报表数据
     *
     * @param resultCallback
     */
    public void dailyReport(ResultCallback<WorkShiftReport> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.dailyReport(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<WorkShiftReport>, WorkShiftReport>() {
            @Override
            public WorkShiftReport call(PosResponse<WorkShiftReport> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<WorkShiftReport>(resultCallback));
    }


    /**
     * 获取门店的营销活动
     *
     * @param resultCallback
     */
    public void getStoreMarket(final ResultCallback<List<Market>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getStoreMarket(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<Market>>, List<Market>>() {
                    @Override
                    public List<Market> call(PosResponse<List<Market>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Market>>(resultCallback));
    }

    /**
     * 获取门店打印机列表
     *
     * @param resultCallback
     */
    public void getPrinterList(final ResultCallback<List<Printer>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getAllPrinter());
            return;
        }
        Observable ov = internalService.getPrinterList(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<Printer>>, List<Printer>>() {
                    @Override
                    public List<Printer> call(PosResponse<List<Printer>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            cachedDao.savePrinterList(listPosResponse.getContent());
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io());
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Printer>>(resultCallback));

    }


    /**
     * 获取门店KDS列表
     *
     * @param resultCallback
     */
    public void getKDSList(final ResultCallback<List<KDS>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getAllKDS());
            return;
        }
        Observable ov = internalService.getKDSList(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<KDS>>, List<KDS>>() {
                    @Override
                    public List<KDS> call(PosResponse<List<KDS>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            cachedDao.saveKDSList(listPosResponse.getContent());
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io());
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<KDS>>(resultCallback));

    }


    /**
     * 获取档口信息
     *
     * @param resultCallback
     */
    public void getKitchenStalls(final ResultCallback<List<KitchenStall>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getAllKitchenStall());
            return;
        }
        Observable ov = internalService.getKichenStalls(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<KitchenStall>>, List<KitchenStall>>() {
                    @Override
                    public List<KitchenStall> call(PosResponse<List<KitchenStall>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            cachedDao.saveKitchenStallList(listPosResponse.getContent());
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io());
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<KitchenStall>>(resultCallback));
    }

    /**
     * 获取打印机厂商
     *
     * @param resultCallback
     */
    public void getPrinterVendors(final ResultCallback<List<PrinterVendors>> resultCallback) {
        internalService.getPrinterVendors()
                .map(new Func1<PosResponse<List<PrinterVendors>>, List<PrinterVendors>>() {
                    @Override
                    public List<PrinterVendors> call(PosResponse<List<PrinterVendors>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<PrinterVendors>>(resultCallback));
    }


    /**
     * 添加打印机
     *
     * @param vendor
     * @param ip
     * @param description
     * @param linkType
     * @param outputType
     * @param width
     * @param labelHeight
     * @param resultCallback
     */
    public void addPrinter(String vendor, String ip, String description, String linkType, String outputType, String width, String labelHeight, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addPrinter(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), vendor, ip, description, linkType, outputType, width, labelHeight).map(new Func1<PosResponse, Integer>() {
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
     * 添加Kds
     *
     * @param ip
     * @param kdsName
     * @param resultCallback
     */
    public void addKds(String ip, String kdsName, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addKds(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), ip, kdsName).map(new Func1<PosResponse, Integer>() {
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
     * 删除打印机
     */
    public void deletePrinter(Printer printer, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.deletePrinter(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), printer.getId() + "").map(new Func1<PosResponse, Integer>() {
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
     * 删除KDS
     *
     * @param kds
     * @param resultCallback
     */
    public void deleteKds(KDS kds, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.deleteKds(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), kds.getId() + "").map(new Func1<PosResponse, Integer>() {
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
     * 查询门店的用户列表
     *
     * @param resultCallback
     */
    public void getStaff(final ResultCallback<List<Staff>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getUsers(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<Staff>>, List<Staff>>() {
                    @Override
                    public List<Staff> call(PosResponse<List<Staff>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Staff>>(resultCallback));
    }

    /**
     * 添加员工
     *
     * @param username
     * @param discount
     * @param quartersid
     * @param realname
     * @param jobnumber
     * @param resultCallback
     */
    public void addUser(String username, Integer discount, int quartersid, String realname, String jobnumber, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addUser(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), username, discount, quartersid, realname, jobnumber).map(new Func1<PosResponse, Integer>() {
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
     * 修改员工信息
     *
     * @param username
     * @param status
     * @param discount
     * @param quartersid
     * @param realname
     * @param jobnumber
     * @param resultCallback
     */
    public void modifyStaff(String username, int status, Integer discount, int quartersid, String realname, String jobnumber, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.addUser(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), username, status, discount, quartersid, realname, jobnumber).map(new Func1<PosResponse, Integer>() {
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
     * 查询岗位列表
     *
     * @param resultCallback
     */
    public void getQuarters(final ResultCallback<List<Staff>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getQuarters(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<Staff>>, List<Staff>>() {
                    @Override
                    public List<Staff> call(PosResponse<List<Staff>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Staff>>(resultCallback));
    }


    /**
     * 获取备用金使用记录
     *
     * @param resultCallback
     */
    public void getStandByCash(final ResultCallback<List<StandByCash>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getStandByCash(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
                .map(new Func1<PosResponse<List<StandByCash>>, List<StandByCash>>() {
                    @Override
                    public List<StandByCash> call(PosResponse<List<StandByCash>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<StandByCash>>(resultCallback));
    }


    public void getStandByCashType(final ResultCallback<List<StandByCash>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getStandByCashType(posInfo.getAppId(), posInfo.getBrandId())
                .map(new Func1<PosResponse<List<StandByCash>>, List<StandByCash>>() {
                    @Override
                    public List<StandByCash> call(PosResponse<List<StandByCash>> listPosResponse) {
                        if (listPosResponse.isSuccessful()) {
                            return listPosResponse.getContent();
                        }
                        PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse.getErrmsg());
                        throw Exceptions.propagate(exception);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<StandByCash>>(resultCallback));
    }

    /**
     * 新建备用金使用记录
     *
     * @param resultCallback
     */
    public void createReceiveHistoryRecord(int reasonid, String reasonName, int outputType, String amount, String createTime, ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.createReceiveHistoryRecord(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), reasonid, reasonName, outputType, amount, posInfo.getRealname(), posInfo.getTerminalName(), createTime).map(new Func1<PosResponse, Integer>() {
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
     * 获取全部打印模板
     *
     * @param resultCallback
     */
    public void getAllTemplates(ResultCallback<List<PrinterTemplates>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        if (!ServerStatus.getInstance().isRunning()) {
            //无法连接服务器，可以直接进入系统
            resultCallback.onResult(cachedDao.getAllPrinterTemplates());
            return;
        }
        Observable ov = internalService.getAllTemplates(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<PrinterTemplates>>, List<PrinterTemplates>>() {
            @Override
            public List<PrinterTemplates> call(PosResponse<List<PrinterTemplates>> response) {
                if (response.getResult() == 0) {
                    cachedDao.savePrinterTemplatesList(response.getContent());
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io());
        ov.observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<PrinterTemplates>>(resultCallback));
    }

    /**
     * 查询全单的折扣信息列表
     *
     * @param resultCallback
     */
    public void getOrderDiscountTypes(ResultCallback<List<Discount>> resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.getOrderDiscountTypes(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId()).map(new Func1<PosResponse<List<Discount>>, List<Discount>>() {
            @Override
            public List<Discount> call(PosResponse<List<Discount>> response) {
                if (response.getResult() == 0) {
                    return response.getContent();
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<List<Discount>>(resultCallback));
    }

    /**
     * 重置密码
     *
     * @param userName
     * @param resultCallback
     */
    public void forgetPwd(String userName, ResultCallback resultCallback) {
        internalService.forgetPwd(userName).map(new Func1<PosResponse, Integer>() {
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
     * 修改密码
     *
     * @param userName
     * @param oldPw
     * @param newPw
     * @param resultCallback
     */
    public void changePwd(String userName, String oldPw, String newPw, ResultCallback resultCallback) {
        internalService.changePwd(userName, oldPw, newPw).map(new Func1<PosResponse, Integer>() {
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
     * 记录打印钱箱操作
     *
     * @param resultCallback
     */
    public void openCashboxHistory(ResultCallback resultCallback) {
        PosInfo posInfo = PosInfo.getInstance();
        internalService.openCashboxHistory(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), posInfo.getRealname(), System.currentTimeMillis(), posInfo.getTerminalId(), posInfo.getTerminalName(), posInfo.getWorkShiftName()).map(new Func1<PosResponse, Integer>() {
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

}
