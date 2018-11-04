package cn.acewill.mobile.pos.presenter;


import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.order.Order;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.retrofit.MemberResponse;
import cn.acewill.mobile.pos.ui.DialogView;

/**
 * 会员订单管理
 * Created by aqw on 2016/6/17.
 */
public class MemberOrderPresenter {
    private DialogView dialogView;
    private OrderService orderService;
    private MyApplication myApplication;

    private Store store;
    private PosInfo posInfo;

    private String appId;//商户ID
    private String brandId;//品牌ID
    private String storeId;//门店ID

    public MemberOrderPresenter(DialogView dialogView){
        this.dialogView = dialogView;
        try {
            orderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }

        myApplication = MyApplication.getInstance();
        store = Store.getInstance(myApplication);
        posInfo = PosInfo.getInstance();
        appId = store.getStoreAppId();
        brandId = store.getBrandId();
        storeId = store.getStoreId();
    }

    //根据订单号获取订单详情
    public void getOrderById(String orderId){
        dialogView.showDialog();
        orderService.getOrderInfoById(orderId, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                dialogView.dissDialog();
                if(result != null)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    //获取某个门店当日的所有订单
    public void getAllOrders(int page,int limit){
        dialogView.showDialog();
        orderService.getMemberOrderList(appId, brandId, storeId,page,limit, new ResultCallback<MemberResponse>() {
            @Override
            public void onResult(MemberResponse result) {
                dialogView.dissDialog();
                if(result != null)
                {
                    if(result.getContent() != null)
                    {
                        dialogView.callBackData(result);
                    }
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

}
