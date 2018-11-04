package cn.acewill.mobile.pos.service.retrofit;

import java.util.List;

import cn.acewill.mobile.pos.model.Discount;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.Market;
import cn.acewill.mobile.pos.model.StandByCash;
import cn.acewill.mobile.pos.model.TerminalVersion;
import cn.acewill.mobile.pos.model.WorkShiftNewReport;
import cn.acewill.mobile.pos.model.WorkShiftReport;
import cn.acewill.mobile.pos.model.user.Staff;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.printer.PrinterTemplates;
import cn.acewill.mobile.pos.printer.PrinterVendors;
import cn.acewill.mobile.pos.service.retrofit.response.OtherFileResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.service.retrofit.response.TerminalLoginResponse;
import cn.acewill.mobile.pos.service.retrofit.response.UserResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Acewill on 2016/6/6.
 */
public interface RetrofitSystemService {
    /**
     * {"success":1,"funcodes":[100,101,102],"username":"1","rname":"\u7ba1\u7406\u5458","uid":"1"}
     * 或者{"success":0,"msg":"用户名与密码不匹配"}
     * 登陆接口
     */
    @FormUrlEncoded
    @POST( "api/terminal/userlogin" )
    Observable<Response<UserResponse>> login(@Query( "appid" ) String appid,
                                             @Query( "brandid" ) String brandid,
                                             @Query( "storeid" ) String storeid,
                                             @Query( "terminalmac" ) String terminalmac,
                                             @Field( "username" ) String username,
                                             @Field( "pwd" ) String password);

    /**
     * 终端登录， 访问API接口必须先登录终端
     *
     * @param tname           终端名称
     * @param terminalmac     终端的mac地址
     * @param receiveNetOrder 是否接受网络订单 0:不接收
     * @param currentVersion  终端的当前版本号
     * @param versionid       终端的当前版本id
     * @return
     */
    @POST( "api/terminal/login" )
    Observable<Response<TerminalLoginResponse>> terminalLogin(@Query( "appid" ) String appid,
                                                              @Query( "brandid" ) String brandid,
                                                              @Query( "storeid" ) String storeid,
                                                              @Query( "tname" ) String tname,
                                                              @Query( "terminalmac" ) String terminalmac,
                                                              @Query( "receiveNetOrder" ) int receiveNetOrder,
                                                              @Query( "currentVersion" ) String currentVersion,
                                                              @Query( "versionid" ) String versionid,
                                                              @Query( "terminalIp" ) String terminalIp);

    @GET( "test/heartbeat" )
    Observable<Response<UserResponse>> heartbeat();

    /**
     * 检测版本更新
     *
     * @param versionid
     * @return
     */
    @POST( "api/terminal/getLatestTerminalVersion" )
    Observable<PosResponse<TerminalVersion>> getTerminalVersions(@Query( "versionid" ) String versionid);


    @Multipart
    @POST( "api/terminal/uploadLog" )
    Observable<PosResponse> upLoadLog(@Query( "appid" ) String appid,
                                      @Query( "brandid" ) String brandid,
                                      @Query( "storeid" ) String storeid,
                                      @Query( "terminalid" ) String terminalid,
                                      @Part( "description" ) RequestBody description,
                                      @Part MultipartBody.Part file);

    /**
     * 获取后台商家设置的LOGO图片
     *
     * @param appid
     * @param brandid
     * @param storeid
     * @return
     */
    @POST( "api/terminal/logo" )
    Observable<OtherFileResponse> getLogoResource(@Query( "appid" ) String appid,
                                                  @Query( "brandid" ) String brandid,
                                                  @Query( "storeid" ) String storeid);

    /**
     * 交接班报表数据
     *
     * @param appid
     * @param brandid
     * @param storeid
     * @param workshiftId 班次Id
     * @return
     */
    @GET( "api/report/workShift" )
    Observable<PosResponse<WorkShiftNewReport>> workShiftReport(@Query( "appId" ) String appid,
                                                                @Query( "brandId" ) String brandid,
                                                                @Query( "storeId" ) String storeid,
                                                                @Query( "workShiftId" ) Integer workshiftId,
                                                                @Query( "endWorkAmount" ) String endWorkAmount);

    /**
     * 获取日结报表
     *
     * @param appid
     * @param brandid
     * @param storeid
     * @return
     */
    @GET( "api/report/daily" )
    Observable<PosResponse<WorkShiftReport>> dailyReport(@Query( "appId" ) String appid,
                                                         @Query( "brandId" ) String brandid,
                                                         @Query( "storeId" ) String storeid
    );


    /**
     * 获取门店营销方案
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/terminal/market" )
    Observable<PosResponse<List<Market>>> getStoreMarket(@Query( "appid" ) String appId,
                                                         @Query( "brandid" ) String brandId,
                                                         @Query( "storeid" ) String storeId);

    /**
     * 获取门店打印机列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/kichenStalls/getPrinters" )
    Observable<PosResponse<List<Printer>>> getPrinterList(@Query( "appid" ) String appId,
                                                          @Query( "brandid" ) String brandId,
                                                          @Query( "storeid" ) String storeId);

    /**
     * 获取门店KDS列表
     */
    @POST( "api/kichenStalls/getKDSes" )
    Observable<PosResponse<List<KDS>>> getKDSList(@Query( "appid" ) String appId,
                                                  @Query( "brandid" ) String brandId,
                                                  @Query( "storeid" ) String storeId);

    /**
     * 获取门店档口列表
     */
    @POST( "api/kichenStalls/getKichenStalls" )
    Observable<PosResponse<List<KitchenStall>>> getKichenStalls(@Query( "appid" ) String appId,
                                                                @Query( "brandid" ) String brandId,
                                                                @Query( "storeid" ) String storeId);

    /**
     * 获取打印机厂商列表
     *
     * @return
     */
    @POST( "api/kichenStalls/getPrinterVendors" )
    Observable<PosResponse<List<PrinterVendors>>> getPrinterVendors();

    /**
     * 添加打印机
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param vendor
     * @param ip
     * @param description
     * @param width
     * @param linkType
     * @param outputType
     * @param labelHeight
     * @return
     */
    @POST( "api/kichenStalls/newPrinter" )
    Observable<PosResponse> addPrinter(@Query( "appid" ) String appId,
                                       @Query( "brandid" ) String brandId,
                                       @Query( "storeid" ) String storeId,
                                       @Query( "vendor" ) String vendor,
                                       @Query( "ip" ) String ip,
                                       @Query( "description" ) String description,
                                       @Query( "linkType" ) String linkType,
                                       @Query( "outputType" ) String outputType,
                                       @Query( "width" ) String width,
                                       @Query( "labelHeight" ) String labelHeight);

    @POST( "api/kichenStalls/newKDS" )
    Observable<PosResponse> addKds(@Query( "appid" ) String appId,
                                   @Query( "brandid" ) String brandId,
                                   @Query( "storeid" ) String storeId,
                                   @Query( "ip" ) String ip,
                                   @Query( "kdsName" ) String kdsName);

    /**
     * 删除打印机
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param printerid
     * @return
     */
    @POST( "api/kichenStalls/deletePrinter" )
    Observable<PosResponse> deletePrinter(@Query( "appid" ) String appId,
                                          @Query( "brandid" ) String brandId,
                                          @Query( "storeid" ) String storeId,
                                          @Query( "printerid" ) String printerid);


    /**
     * 删除KDS
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param kdsid
     * @return
     */
    @POST( "api/kichenStalls/deleteKDS" )
    Observable<PosResponse> deleteKds(@Query( "appid" ) String appId,
                                      @Query( "brandid" ) String brandId,
                                      @Query( "storeid" ) String storeId,
                                      @Query( "kdsid" ) String kdsid);


    /**
     * 查询门店的用户列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/user/getUsers" )
    Observable<PosResponse<List<Staff>>> getUsers(@Query( "appid" ) String appId,
                                                  @Query( "brandid" ) String brandId,
                                                  @Query( "storeid" ) String storeId);

    /**
     * 添加新员工
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param kdsid
     * @param discount
     * @param quartersid
     * @param realname
     * @param jobnumber
     * @return
     */
    @POST( "api/user/newUser" )
    Observable<PosResponse<Staff>> addUser(@Query( "appid" ) String appId,
                                           @Query( "brandid" ) String brandId,
                                           @Query( "storeid" ) String storeId,
                                           @Query( "username" ) String kdsid,
                                           @Query( "discount" ) Integer discount,
                                           @Query( "quartersid" ) Integer quartersid,
                                           @Query( "realname" ) String realname,
                                           @Query( "jobnumber" ) String jobnumber);

    /**
     * 修改员工信息
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param kdsid
     * @param status
     * @param discount
     * @param quartersid
     * @param realname
     * @param jobnumber
     * @return
     */
    @POST( "api/user/newUser" )
    Observable<PosResponse<Staff>> addUser(@Query( "appid" ) String appId,
                                           @Query( "brandid" ) String brandId,
                                           @Query( "storeid" ) String storeId,
                                           @Query( "username" ) String kdsid,
                                           @Query( "status " ) Integer status,
                                           @Query( "discount" ) Integer discount,
                                           @Query( "quartersid" ) Integer quartersid,
                                           @Query( "realname" ) String realname,
                                           @Query( "jobnumber" ) String jobnumber);

    /**
     * 查询岗位列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/user/getQuarters" )
    Observable<PosResponse<List<Staff>>> getQuarters(@Query( "appid" ) String appId,
                                                     @Query( "brandid" ) String brandId,
                                                     @Query( "storeid" ) String storeId);


    /**
     * 获取备用金使用记录
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/store_operation/getReceivePaymentHistory" )
    Observable<PosResponse<List<StandByCash>>> getStandByCash(@Query( "appid" ) String appId,
                                                              @Query( "brandid" ) String brandId,
                                                              @Query( "storeid" ) String storeId);

    /**
     * 获取备用金里支付类型列表
     *
     * @param appId
     * @param brandId
     * @return
     */
    @POST( "api/store_operation/getReceivePaymentReasons" )
    Observable<PosResponse<List<StandByCash>>> getStandByCashType(@Query( "appid" ) String appId,
                                                                  @Query( "brandid" ) String brandId);


    /**
     * 新建备用金使用记录
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/store_operation/newReceiveHistoryRecord" )
    Observable<PosResponse> createReceiveHistoryRecord(@Query( "appid" ) String appId,
                                                       @Query( "brandid" ) String brandId,
                                                       @Query( "storeid" ) String storeId,
                                                       @Query( "reasonid" ) int reasonid,
                                                       @Query( "reasonName" ) String reasonName,
                                                       @Query( "outputType" ) int outputType,
                                                       @Query( "amount" ) String amount,
                                                       @Query( "userName" ) String userName,
                                                       @Query( "terminalName" ) String terminalName,
                                                       @Query( "createTime" ) String createTime);

    /**
     * 获取全部打印模板
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/printTemplate/getAllTemplates" )
    Observable<PosResponse<List<PrinterTemplates>>> getAllTemplates(@Query( "appid" ) String appId,
                                                                    @Query( "brandid" ) String brandId,
                                                                    @Query( "storeid" ) String storeId);


    /**
     * 查询全单的折扣信息列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/orderdiscount/getOrderDiscountTypes" )
    Observable<PosResponse<List<Discount>>> getOrderDiscountTypes(@Query( "appid" ) String appId,
                                                                  @Query( "brandid" ) String brandId,
                                                                  @Query( "storeid" ) String storeId);


    /**
     * 重置密码
     *
     * @param username
     * @return
     */
    @POST( "api/terminal/user/forgetPwd" )
    Observable<PosResponse> forgetPwd(@Query( "username" ) String username);

    /**
     * 修改密码
     *
     * @param username
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @POST( "api/terminal/user/changePwd" )
    Observable<PosResponse> changePwd(@Query( "username" ) String username,
                                      @Query( "oldPwd" ) String oldPwd,
                                      @Query( "newPwd" ) String newPwd);


    /**
     * 记录打开钱箱的操作
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param userName
     * @param time
     * @param terminalId
     * @param terminalName
     * @return
     */
    @POST( "api/store_operation/openCashbox" )
    Observable<PosResponse> openCashboxHistory(@Query( "appId" ) String appId,
                                               @Query( "brandId" ) String brandId,
                                               @Query( "storeId" ) String storeId,
                                               @Query( "userName" ) String userName,
                                               @Query( "time" ) Long time,
                                               @Query( "terminalId" ) String terminalId,
                                               @Query( "terminalName" ) String terminalName,
                                               @Query( "workShiftName" ) String workShiftName);
}
