package cn.acewill.mobile.pos.service.retrofit;

import java.util.List;

import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.OrderSingleReason;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 交易(结账；退款等)
 * Created by aqw on 2016/8/18.
 */
public interface RetrofitTradeService {

    /**
     * 结账
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @param paymentList
     * @return
     */
    @POST("api/orders/checkout")
    Observable<PosResponse> checkOut(@Query( "appId" ) String appId,
                                     @Query( "brandId" ) String brandId,
                                     @Query( "storeId" ) String storeId,
                                     @Query( "orderId" ) String orderId, @Body List<PaymentList> paymentList);

    /**
     * 反结账
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @param paymentList
     * @return
     */
    @POST("api/orders/reCheckout")
    Observable<PosResponse> reCheckout(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "orderId" ) String orderId,
                                       @Query( "reasonId" ) Integer reasonId,
                                       @Query( "userName" ) String userName,
                                       @Query( "terminalName" ) String terminalName,
                                       @Query( "refund" ) Boolean refund,
                                       @Body List<PaymentList> paymentList);

    /**
     * 关闭订单
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST("api/orders/close")
    Observable<PosResponse> closeOrder(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "reasonId" ) int reasonId,
                                       @Query( "orderId" ) String orderId,
                                       @Query( "returnUserName" ) String returnUserName,
                                       @Query( "authUserName" ) String authUserName);



    /**
     * 退单(先支付后下单使用)
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST("api/orders/refund")
    Observable<PosResponse> refund(@Query( "appId" ) String appId,
                                   @Query( "brandId" ) String brandId,
                                   @Query( "storeId" ) String storeId,
                                   @Query( "orderId" ) String orderId,
                                   @Query( "reasonId" ) int reasonId,
                                   @Query( "userName" ) String userName,
                                   @Query( "returnUserName" ) String returnUserName,
                                   @Query( "authUserName" ) String authUserName);

    /**
     * 获取退单原因列表
     * @return
     */
    @GET("api/orders/reason")
    Observable<PosResponse<List<OrderSingleReason>>> getReason();

    /**
     * 退菜(先下单后支付使用)
     * @param appId
     * @param brandId
     * @param storeId
     * @param itemList
     * @return
     */
    @POST("api/orders/remove")
    Observable<PosResponse<Order>> removeDish(@Query( "appId" ) String appId,
                                              @Query( "brandId" ) String brandId,
                                              @Query( "storeId" ) String storeId,
                                              @Query( "orderId" ) String orderId,
                                              @Query( "reasonId" ) int reasonId,
                                              @Body List<OrderItem> itemList,
                                              @Query( "returnUserName" ) String returnUserName,
                                              @Query( "authUserName" ) String authUserName
    );

    /**
     * 退菜(先支付后下单使用)
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST("api/orders/refundDish")
    Observable<PosResponse> refundDish(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "orderId" ) String orderId,
                                       @Query( "reasonId" ) int reasonId,
                                       @Query( "userName" ) String userName,
                                       @Body List<OrderItem> itemList,
                                       @Query( "returnUserName" ) String returnUserName,
                                       @Query( "authUserName" ) String authUserName
    );


    /**
     * 发票冲红
     * @param appId
     * @param brandId
     * @param storeId
     * @param revokeReson
     * @return
     */
    @POST("public/invoice/revokeInvoice")
    Observable<PosResponse> revokeInvoice(@Query( "appId" ) String appId,
                                          @Query( "brandId" ) String brandId,
                                          @Query( "storeId" ) String storeId,
                                          @Query( "orderId" ) String orderId,
                                          @Query( "revokeReson" ) String revokeReson);

}
