package cn.acewill.mobile.pos.service.retrofit;

import java.util.List;

import cn.acewill.mobile.pos.model.Definition;
import cn.acewill.mobile.pos.model.OrderItemReportData;
import cn.acewill.mobile.pos.model.Receipt;
import cn.acewill.mobile.pos.model.StoreBusinessInformation;
import cn.acewill.mobile.pos.model.StoreConfiguration;
import cn.acewill.mobile.pos.model.TerminalInfo;
import cn.acewill.mobile.pos.model.WorkShift;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishTime;
import cn.acewill.mobile.pos.model.dish.Unit;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.model.report.BodyRequest;
import cn.acewill.mobile.pos.model.report.DishReport;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.service.retrofit.response.DishResponse;
import cn.acewill.mobile.pos.service.retrofit.response.LKLResponse;
import cn.acewill.mobile.pos.service.retrofit.response.OtherFileResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PaymentTypesResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.service.retrofit.response.ScreenResponse;
import cn.acewill.mobile.pos.service.retrofit.response.ValidationResponse;
import cn.acewill.mobile.pos.service.retrofit.response.WeiFuTongResponse;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by DHH on 2016/6/16.
 */
public interface RetrofitTerminalService {
    /**
     * 获取菜品分类列表
     */
    @POST("api/terminal/dishKind")
    Observable<DishResponse> getKindDataInfo(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    /**
     * 获取菜品里列表
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST("api/terminal/dishmenu/sku")
    Observable<DishResponse> getDishList(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);


    @POST("api/terminal/getAllDishmenu")
    Observable<DishResponse> getAllDishmenu(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);



    /**
     * 添加菜品
     * @param dish
     * @return
     */
    @POST("api/dish")
    Observable<PosResponse> addDish(@Body Dish dish);

    @GET("api/dish/units")
    Observable<PosResponse<List<Unit>>> getDishUnit(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    @GET("api/dish/times")
    Observable<PosResponse<List<DishTime>>> getDishTime(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    /**
     * 获取菜品数量
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET("api/terminal/dishCounts")
    Observable<PosResponse<List<DishCount>>> getDishCounts(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    /**
     * 获取该门店已经启用的支付方式
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST("api/terminal/paytypes")
    Observable<PaymentTypesResponse> getPaymentTypeList(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    /**
     * 检查菜品是否足够出售
     * @param appId
     * @param brandId
     * @param storeId
     * @dishIDStr 一个json数组的字符串， 比如 "[{\"dishid\":1,\"count\":2}]"
     * @return
     */
    @POST("api/terminal/checkCounts")
    Observable<DishResponse> checkDishCount(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId, @Query( "dishIDStr" ) String dishIDStr);

    /**
     *菜品估清
     * @param appId
     * @param brandId
     * @param storeId
     * @param dishidList
     * @param dishCount
     * @return
     */
    @POST("api/terminal/updateCount")
    Observable<PosResponse> updataDishCount(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId, @Query( "dishidList" ) List<Integer> dishidList, @Query( "count" ) int dishCount);

    /**
     * 查询全单的折扣信息列表
     * @param appId
     * @param brandId
     * @param storeId
     * @dishIDStr 一个json数组的字符串， 比如 "[{\"dishid\":1,\"count\":2}]"
     * @return
     */
    @POST("api/orderdiscount/getOrderDiscountTypes")
    Observable<PosResponse<List<MarketingActivity>>> getMarketingActivityList(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId);

    /**
     * 开始一个班次
     * @param appId
     * @param storeId
     * @return
     */
    @POST("/api/store_operation/work_shifts")
    Observable<PosResponse<WorkShift>> startWorkShift(@Query( "appId" ) String appId, @Query( "storeId" ) String storeId,
                                                      @Body WorkShift workShift);

    /**
     * 结束一个班次
     * @param appId
     * @param storeId
     * @return
     */
    @PUT("/api/store_operation/work_shifts")
    Observable<PosResponse> endWorkShift(@Query( "appId" ) String appId, @Query( "storeId" ) String storeId,
                                         @Query( "id" ) Long id, @Body WorkShift workShift);


    /**
     * 检测当前是否有未交的班次
     * @param appId
     * @param storeId
     * @return
     */
    @GET("/api/store_operation/work_shifts")
    Observable<PosResponse<WorkShift>> getOpenWorkShift(@Query( "appId" ) String appId, @Query( "storeId" ) String storeId,
                                                        @Query( "userName" ) String userName, @Query( "terminalId" ) String terminalId);

    /**
     * 获取班次定义的接口
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET( "api/store_operation/work_shift_definition" )
    Observable<PosResponse<List<Definition>>> workShiftDefinition(@Query( "appId" ) String appId,
                                                                  @Query( "brandId" ) String brandId,
                                                                  @Query( "storeId" ) String storeId);
    /**
     * 在某个门店添加打印机
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST("/api/store_operation/printers")
    Observable<PosResponse<Printer>> addPrinter(@Query( "appId" ) String appId,
                                                @Query( "brandId" ) String brandId,
                                                @Query( "storeId" ) String storeId,
                                                @Body Printer printer);

    /**
     * 在某个门店添加打印机
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET("/api/store_operation/printers")
    Observable<PosResponse<List<Printer>>> listPrinters(@Query( "appId" ) String appId,
                                                        @Query( "brandId" ) String brandId,
                                                        @Query( "storeId" ) String storeId);

    /**
     * 删除某个门店的打印机
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @DELETE("/api/store_operation/printers")
    Observable<PosResponse<Integer>> deletePrinter(@Query( "appId" ) String appId,
                                                   @Query( "brandId" ) String brandId,
                                                   @Query( "storeId" ) String storeId,
                                                   @Query( "id" ) String id);

    /**
     * 更新打印机
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @PUT("/api/store_operation/printers")
    Observable<PosResponse<Printer>> updatePrinter(@Query( "appId" ) String appId,
                                                   @Query( "brandId" ) String brandId,
                                                   @Query( "storeId" ) String storeId,
                                                   @Query( "id" ) Integer id,
                                                   @Body Printer printer);

    /**
     * 列出这个门店的小票类型
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET("/api/store_operation/receipts")
    Observable<PosResponse<List<Receipt>>> listReceipts(@Query( "appId" ) String appId,
                                                        @Query( "brandId" ) String brandId,
                                                        @Query( "storeId" ) String storeId);

    /**
     * 列出这个门店的小票类型
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET("/api/store_operation")
    Observable<PosResponse<StoreBusinessInformation>> getStoreBusinessInformation(@Query( "appId" ) String appId,
                                                                                  @Query( "brandId" ) String brandId,
                                                                                  @Query( "storeId" ) String storeId);

    /**
     * 通过机器的macAddress获取终端信息
     * @param terminalmac
     * @return
     */
    @GET( "/api/terminal/info" )
    Observable<PosResponse<TerminalInfo>> getTerminalInfo(@Query( "terminalMac" ) String terminalmac);

    /**
     * 绑定终端到该用户所属的门店
     * @param terminalMac
     * @param terminalType 终端类型；0：pos；1：移动pos；2：点餐机；
     * @return
     */
    @POST( "/api/terminal/bind" )
    Observable<PosResponse<TerminalInfo>> bindStore(@Query( "terminalMac" ) String terminalMac,
                                                    @Query( "terminalType" ) String terminalType);

    /**
     * 取消绑定终端所属的门店
     * @param userName
     * @param password
     * @param terminalMac
     * @return
     */
    @POST( "/api/terminal/unbind" )
    Observable<PosResponse> unbindStore(@Query( "userName" ) String userName,
                                        @Query( "password" ) String password,
                                        @Query( "terminalMac" ) String terminalMac);


    /**
     * 获取文件（第二屏展示）
     * @param appid
     * @param brandid
     * @param storeid
     * @return
     */
    @POST("api/terminal/getOtherfiles")
    Observable<OtherFileResponse> getOtherFile(@Query( "appid" ) String appid,
                                               @Query( "brandid" ) String brandid,
                                               @Query( "storeid" ) String storeid);

    /**
     * 获取当前班次和当天报表数据
     * @param appId
     * @param brandId
     * @param storeId
     * @param workShfitId
     * @return
     */
    @POST("api/store_operation/orderItemOnWork")
    Observable<PosResponse<List<OrderItemReportData>>> getReportData(@Query( "appId" ) String appId,
                                                                     @Query( "brandId" ) String brandId,
                                                                     @Query( "storeId" ) String storeId,
                                                                     @Query( "workShfitId" ) String workShfitId);

    /**
     * 获取菜品销售报表
     * @param bodyRequest
     * @return
     */
    @POST("api/store_operation/dish_sales")
    Observable<PosResponse<List<DishReport>>> getReportDish(@Query( "sort" ) String sort, @Query( "direction" ) String direction, @Body BodyRequest bodyRequest);

    /**
     * 退菜/单时判断输入用户是否具有权限
     * @param username
     * @param pwd
     * @param appId
     * @param brandId
     * @param storeId
     * @param authroityid 权限id
     * @return
     */
    @POST("api/terminal/checkUserAuthority")
    Observable<PosResponse> checkUserAuthority(@Query( "username" ) String username,
                                               @Query( "pwd" ) String pwd,
                                               @Query( "appid" ) String appId,
                                               @Query( "brandid" ) String brandId,
                                               @Query( "storeid" ) String storeId,
                                               @Query( "authroityid" ) Integer authroityid);


    /**
     * 获取拉卡拉支付信息,并提交支付请求
     * @param appId
     * @param storeId
     * @param amount
     * @param subject
     * @param body
     * @param order_no
     * @param channel
     * @param client_ip
     * @param user_id
     * @return
     */
    @POST("wx/creatPaymaxCharge")
    Observable<PosResponse<LKLResponse>> creatPaymaxCharge(@Query( "appid" ) String appId,
                                                           @Query( "storeid" ) String storeId,
                                                           @Query( "amount" ) String amount,
                                                           @Query( "subject" ) String subject,
                                                           @Query( "body" ) String body,
                                                           @Query( "order_no" ) String order_no,
                                                           @Query( "channel" ) String channel,
                                                           @Query( "client_ip" ) String client_ip,
                                                           @Query( "user_id" ) String user_id
    );


    /**
     *  查询paymax支付结果
     * @param appId
     * @param storeId
     * @param chargeid  Charge对象ID
     * @return
     */
    @POST("wx/retrievePaymax")
    Observable<PosResponse<LKLResponse>> retrievePaymax(@Query( "appid" ) String appId,
                                                        @Query( "storeid" ) String storeId,
                                                        @Query( "chargeid" ) String chargeid);


    /**
     * 查询门店设置信息
     * @param appId
     * @param brandid
     * @param storeid
     * @return
     */
    @POST("api/store_operation/getStoreConfiguration")
    Observable<PosResponse<StoreConfiguration>> getStoreConfiguration(@Query( "appid" ) String appId,
                                                                      @Query( "brandid" ) String brandid,
                                                                      @Query( "storeid" ) String storeid);

    /**
     * 调起威富通支付
     * @param appid
     * @param storeid
     * @param auth_code
     * @param mch_create_ip
     * @param body
     * @param total_fee
     * @param out_trade_no
     * @return
     */
    @POST("api/swiftpass/gateway")
    Observable<PosResponse<WeiFuTongResponse>> gateway(@Query( "appId" ) String appid,
                                                       @Query( "storeId" ) String storeid,
                                                       @Query( "auth_code" ) String auth_code,
                                                       @Query( "mch_create_ip" ) String mch_create_ip,
                                                       @Query( "body" ) String body,
                                                       @Query( "total_fee" ) Integer total_fee,
                                                       @Query( "out_trade_no" ) String out_trade_no);

    /**
     * 查询威富通支付情况
     * @param appid
     * @param storeid
     * @param out_trade_no
     * @param transaction_id
     * @return
     */
    @POST("api/swiftpass/query")
    Observable<PosResponse<WeiFuTongResponse>> queryWeiFuTong(@Query( "appId" ) String appid,
                                                              @Query( "storeId" ) String storeid,
                                                              @Query( "out_trade_no" ) String out_trade_no,
                                                              @Query( "transaction_id" ) String transaction_id);


    /**
     * 获取交接班记录
     * @param appId
     * @param brandId
     * @param storeId
     * @param startTime
     * @param endTime
     * @return
     */
    @GET("api/store_operation/work_shifts_store")
    Observable<PosResponse<List<WorkShift>>> getWorkShiftHistory(@Query( "appId" ) String appId,
                                                                 @Query( "brandId" ) String brandId,
                                                                 @Query( "storeId" ) String storeId,
                                                                 @Query( "startTime" ) String startTime,
                                                                 @Query( "endTime" ) String endTime);

    /**
     * 获取副屏配置
     * @param appid
     * @param brandid
     * @param storeid
     * @return
     */
    @POST("api/terminal/getScreenConfiguration")
    Observable<PosResponse<ScreenResponse>> getScreenConfiguration(@Query( "appid" ) String appid,
                                                                   @Query( "brandid" ) String brandid,
                                                                   @Query( "storeid" ) String storeid);

    /**
     *
     * @param appid
     * @param brandid
     * @param storeid
     * @param couponCode 券码
     * @return
     */
    @POST("public/waimai/meituan/shop/validationSetout")
    Observable<PosResponse<ValidationResponse>> validationSetout(@Query( "appId" ) String appid,
                                                                 @Query( "brandId" ) String brandid,
                                                                 @Query( "storeId" ) String storeid,
                                                                 @Query( "couponCode" ) String couponCode);

    /**
     * 执行验券
     * @param appid
     * @param brandid
     * @param storeid
     * @param eName  收银员名称
     * @param orderId 订单号
     * @param couponCodes 团购券码 String 多个券码中间英文“,”分开
     * @return
     */
    @POST("public/waimai/meituan/shop/executeCode")
    Observable<PosResponse<ValidationResponse>> executeCode(@Query( "appId" ) String appid,
                                                            @Query( "brandId" ) String brandid,
                                                            @Query( "storeId" ) String storeid,
                                                            @Query( "eName" ) String eName,
                                                            @Query( "orderId" ) Long orderId,
                                                            @Query( "couponCodes" ) String couponCodes);


}
