package cn.acewill.mobile.pos.service.retrofit;

import java.util.List;

import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshCreateDeal;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 微生活
 * Created by aqw on 2016/10/19.
 */
public interface RetrofitWshService {

    /**
     * 获取会员信息, 有返回则说明是会员
     * @param appId
     * @param brandId
     * @param storeId
     * @param account
     * @return
     */
    @GET("public/members/getMemberInfo")
    Observable<PosResponse<List<Account>>> getMemberInfo(@Query( "appId" ) String appId,
                                                         @Query( "brandId" ) String brandId,
                                                         @Query( "storeId" ) String storeId,
                                                         @Query( "account" ) String account);


    /**
     * 创建交易
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST("public/members/createDeal")
    Observable<PosResponse<WshDealPreview>> createDeal(@Query( "appId" ) String appId,
                                                       @Query( "brandId" ) String brandId,
                                                       @Query( "storeId" ) String storeId,
                                                       @Body WshCreateDeal.Request request);

    /**
     * 提交交易
     * @param appId
     * @param brandId
     * @param storeId
     * @param bizId
     * @param verifySms
     * @return
     */
    @POST("public/members/commitDeal")
    Observable<PosResponse> commitDeal(@Query( "appId" ) String appId,
                                       @Query( "brandId" ) String brandId,
                                       @Query( "storeId" ) String storeId,
                                       @Query( "bizId" ) String bizId,
                                       @Query( "verifySms" ) String verifySms
    );



}
