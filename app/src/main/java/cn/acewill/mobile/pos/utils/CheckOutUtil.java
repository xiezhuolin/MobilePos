package cn.acewill.mobile.pos.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DishCheckCallback;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.service.DishService;
import cn.acewill.mobile.pos.service.OrderService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.WshService;
import cn.acewill.mobile.pos.widget.ProgressDialogF;

/**
 * Created by DHH on 2017/12/13.
 */

public class CheckOutUtil {
    private Context context;
    private Payment payment;
    private PosInfo posInfo;
    private Store store;
    private Cart cart;
    private OrderService orderService;
    private DishService dishService;
    private WshService wshService;
    private ProgressDialogF progressDialog;
    public CheckOutUtil(Context context, Payment payment) {
        this.context = context;
        this.payment = ToolsUtils.cloneTo(payment);
        posInfo = PosInfo.getInstance();
        store = Store.getInstance(context);
        cart = Cart.getInstance();
        progressDialog = new ProgressDialogF(context);
        try {
            orderService = OrderService.getInstance();
            dishService = DishService.getInstance();
            wshService = WshService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测菜品沽清状况
     *
     * @param dishs
     * @param callback callback.haveStock(); 有库存   callback.noStock();无库存
     */
    public void getDishStock(List<Dish> dishs, final DishCheckCallback callback) {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        List<DishCount> dishCountList = new ArrayList<>();
        int size = dishs.size();
        for (int i = 0; i < size; i++) {
            Dish dish = dishs.get(i);

            DishCount count = new DishCount();
            count.setDishid(dish.getDishId());
            count.setCount(dish.quantity);
            dishCountList.add(count);

            if (dish.isPackage()) {
                for (Dish.Package aPackage : dish.subItemList) {
                    DishCount dc = new DishCount();
                    dc.setDishid(aPackage.getDishId());
                    dc.setCount(aPackage.quantity * dish.quantity);
                    dishCountList.add(dc);
                }
            }

        }
        dishService.checkDishCount(dishCountList, new ResultCallback<List<DishCount>>() {
            @Override
            public void onResult(List<DishCount> result) {
                progressDialog.disLoading();
                if (result == null || result.size() <= 0) {
                    callback.haveStock();
                } else {
                    callback.noStock(result);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                MyApplication.getInstance().ShowToast(e.getMessage());
                progressDialog.disLoading();
            }
        });
    }
}
