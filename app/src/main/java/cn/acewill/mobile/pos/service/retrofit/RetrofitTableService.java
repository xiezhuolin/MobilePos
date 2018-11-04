package cn.acewill.mobile.pos.service.retrofit;

import java.util.List;

import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.order.OrderSingleReason;
import cn.acewill.mobile.pos.model.table.Area;
import cn.acewill.mobile.pos.model.table.Section;
import cn.acewill.mobile.pos.model.table.Sections;
import cn.acewill.mobile.pos.model.table.Table;
import cn.acewill.mobile.pos.model.table.TableData;
import cn.acewill.mobile.pos.model.table.TableOrder;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by DHH on 2016/6/16.
 */
public interface RetrofitTableService {

    /**
     * 获取所有桌台信息
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET( "management/tables" )
    Observable<PosResponse<List<Table>>> getTablesAllStatus(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId);

    @GET( "management/tables" )
    Observable<PosResponse<List<Table>>> getTablesAreaStatus(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "sectionId" ) long sectionId);

    /**
     * 返回店铺区域列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET( "management/tables/sections" )
    Observable<PosResponse<List<Area>>> getTablesArea(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId);

    /**
     * @param id 需要清理的桌台id
     * @return
     */
    @POST( "management/tables/status/empty" )
    Observable<PosResponse<List<Table>>> cleanTable(@Query( "id" ) long id);

    /**
     * @param id 需要设置脏台的桌台id
     * @return
     */
    @POST( "management/tables/status/dirty" )
    Observable<PosResponse<List<Table>>> dirtyTable(@Query( "id" ) long id);

    /**
     * 开台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "management/tables/open" )
    Observable<PosResponse> openTable(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "tableId" ) long tableId, @Query( "numberOfCustomer" ) Long numberOfCustomer, @Query( "operatedBy" ) String operatedBy);

    /**
     * 加台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param oldTableId
     * @param newTableId
     * @param numberOfCustomer
     * @param operatedBy
     * @return
     */
    @POST( "management/tables/addtable" )
    Observable<PosResponse<List<Order>>> addTable(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "oldTableId" ) long oldTableId, @Query( "newTableId" ) long newTableId, @Query( "numberOfCustomer" ) long numberOfCustomer, @Query( "operatedBy" ) String operatedBy);

    /**
     * 获取某个桌台上的订单
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param tableId
     * @return
     */
    @GET( "management/tables/orders" )
    Observable<PosResponse<List<Order>>> ordersTable(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "tableId" ) long tableId);

    /**
     * 根据区域返回桌台数据
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param sectionId 如果是-1  则查询整个门店的数据
     * @return
     */
    @POST( "management/tables/orderData" )
    Observable<PosResponse<List<TableOrder>>> tablesOrderData(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "sectionId" ) long sectionId);

    /**
     * 获取门店的区域列表
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET( "management/tables/sections" )
    Observable<PosResponse<List<Section>>> getTableSections(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId);


    /**
     * 加菜
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/orders/append" )
    Observable<PosResponse<Order>> appendDish(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) long orderId, @Body List<OrderItem> itemList);


    /**
     * 退菜
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param itemList
     * @return
     */
    @POST( "api/orders/remove" )
    Observable<PosResponse<Order>> removeDish(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) long orderId, @Body List<OrderItem> itemList, @Query( "reasonId" ) int reasonId);

    /**
     * 根据区域id获取桌台详情
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param sectionId
     * @return
     */
    @POST( "management/tables/orderData" )
    Observable<PosResponse<List<TableData>>> getTableInfor(@Query( "appId" ) String appId,
                                                           @Query( "brandId" ) String brandId,
                                                           @Query( "storeId" ) String storeId,
                                                           @Query( "sectionId" ) long sectionId);

    /**
     * 转台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param oldTableId
     * @param newTableId
     * @return
     */
    @POST( "management/tables/turn" )
    Observable<PosResponse> turnTable(@Query( "appId" ) String appId,
                                      @Query( "brandId" ) String brandId,
                                      @Query( "storeId" ) String storeId,
                                      @Query( "oldTableId" ) long oldTableId,
                                      @Query( "newTableId" ) long newTableId);

    /**
     * 加台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param oldTableId
     * @param newTableId
     * @param numberOfCustomer
     * @param operatedBy
     * @return
     */
    @POST( "management/tables/addtable" )
    Observable<PosResponse> addTable(@Query( "appId" ) String appId,
                                     @Query( "brandId" ) String brandId,
                                     @Query( "storeId" ) String storeId,
                                     @Query( "oldTableId" ) long oldTableId,
                                     @Query( "newTableId" ) long newTableId,
                                     @Query( "numberOfCustomer" ) String numberOfCustomer,
                                     @Query( "operatedBy" ) String operatedBy);

    /**
     * 并台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param firstTableId
     * @param secondTableId
     * @return
     */
    @POST( "management/tables/jointable" )
    Observable<PosResponse> joinTable(@Query( "appId" ) String appId,
                                      @Query( "brandId" ) String brandId,
                                      @Query( "storeId" ) String storeId,
                                      @Query( "firstTableId" ) long firstTableId,
                                      @Query( "secondTableId" ) long secondTableId);

    /**
     * 清台
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "management/tables/status/empty" )
    Observable<PosResponse> clearTable(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "tableId" ) long tableId);

    /**
     * 进入桌台界面需要绑定
     * @param appId
     * @param brandId
     * @param storeId
     * @param terminalName
     * @param tableId
     * @return
     */
    @POST( "/management/tables/enterTable" )
    Observable<PosResponse> enterTable(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "terminalName" ) String terminalName,
                                       @Query( "tableId" ) long tableId);

    /**
     * 退出桌台界面需要解绑
     * @param appId
     * @param brandId
     * @param storeId
     * @param terminalName
     * @param tableId
     * @return
     */
    @POST( "/management/tables/exitTable " )
    Observable<PosResponse> exitTable(@Query( "appId" ) String appId,
                                      @Query( "brandId" ) String brandId,
                                      @Query( "storeId" ) String storeId,
                                      @Query( "terminalName" ) String terminalName,
                                      @Query( "tableId" ) long tableId);


    /**
     * 获取区域列表信息
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @GET( "management/tables/sections" )
    Observable<PosResponse<List<Sections>>> getSections(@Query( "appId" ) String appId,
                                                        @Query( "brandId" ) String brandId,
                                                        @Query( "storeId" ) String storeId);


    /**
     * 获取退单原因列表
     * @return
     */
    @GET("api/orders/reason")
    Observable<PosResponse<List<OrderSingleReason>>> getReason(@Query( "appid" ) String appId);


    /**
     * 获取反结原因
     * @return
     */
    @GET("api/orders/reCheckoutReason")
    Observable<PosResponse<List<OrderSingleReason>>> reCheckoutReason(@Query( "appid" ) String appId);
}
