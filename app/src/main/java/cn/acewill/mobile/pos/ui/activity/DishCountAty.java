package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.DishDataController;
import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.config.Store;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogEtCallback;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.user.UserData;
import cn.acewill.mobile.pos.service.DishService;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.ui.adapter.DishCountAdp;
import cn.acewill.mobile.pos.ui.adapter.DishKindsCountAdp;
import cn.acewill.mobile.pos.utils.DialogUtil;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.UserAction;
import cn.acewill.mobile.pos.widget.ProgressDialogF;
import cn.acewill.mobile.pos.widget.TitleView;

import static cn.acewill.mobile.pos.common.DishDataController.dishKindList;

/**
 * Created by DHH on 2016/6/12.
 */
public class DishCountAty extends BaseActivity {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.login_title )
    TitleView loginTitle;
    @BindView( R.id.dishKinds )
    ListView dishKinds;
    @BindView( R.id.dishItems )
    ListView dishItems;
    @BindView( R.id.top_ll )
    LinearLayout topLl;
    @BindView( R.id.btn_dishCount )
    TextView btnDishCount;

    private Store store;
    private UserData mUserData;
    private PosInfo posInfo;

    private ProgressDialogF progressDialog;
    private DishKindsCountAdp dishKidsTopAdp;
    private DishCountAdp dishAdp;
    private List<DishType> dishKind;//菜品分类

    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;
    private Long orderId = -1L;

    private List<Dish> currentDishList = new CopyOnWriteArrayList<>();//这是当前选中的菜品列表

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolsUtils.writeUserOperationRecords("退出设置沽清界面");
        if (currentDishList != null && currentDishList.size() > 0) {
            currentDishList.clear();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_dish_count);
        ButterKnife.bind(this);
        myApplication.addPage(DishCountAty.this);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDishCounts();
    }

    private void initData() {
        store = Store.getInstance(context);
        posInfo = PosInfo.getInstance();
        mUserData = UserData.getInstance(context);
        progressDialog = new ProgressDialogF(this);
        dishKidsTopAdp = new DishKindsCountAdp(context);
        dishAdp = new DishCountAdp(context);

        if (currentDishList != null && currentDishList.size() > 0) {
            currentDishList.clear();
        }

        //适配菜品分类数据
        dishKinds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
                dishAdp.setDataInfo(currentDishList);
                dishItems.setAdapter(dishAdp);
                dishKidsTopAdp.setSelect(currentPosition);
            }
        });

        //从选中菜品列表中直接沽清单个菜品
        dishItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish dish = (Dish) dishAdp.getItem(position);
                if (dish != null) {
                    List<Dish> dishCount = new ArrayList<Dish>();
                    dishCount.add(dish);
                    setDishCount(dishCount, 1);
                }
            }
        });

    }

    public void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    progressDialog.disLoading();
                    if (result != null && result.size() > 0) {
                        dishAdp.setDataInfo(DishDataController.getdishsForKind(currentPosition));
                        dishAdp.setDishCount(result);
                    } else {
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
                    }
                    loadData();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    loadData();
                    Log.i("获取菜品沽清状态失败", e.getMessage());
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err") + "," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            Log.i("获取菜品沽清状态失败", e.getMessage());
            e.printStackTrace();
            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
        }
    }

    private void loadData() {
        if (DishDataController.dishKindList != null && DishDataController.dishKindList.size() > 0) {
            if (DishDataController.menuAllData != null && DishDataController.menuAllData.size() > 0) {
                setKidsMap(dishKindList);
                initDishAdapter();
            } else {
                getAllDishList();
            }

        } else {
            getKindInfo();
        }
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getAllDishList() {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getAllDishList(new ResultCallback<List<Menu>>() {
            @Override
            public void onResult(List<Menu> result) {
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    DishDataController.setDishAllData(result);
                    initDishAdapter();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(e.getMessage());
                Log.i("获取菜品为空", e.getMessage());
            }
        });
    }

    /**
     * 获取菜品分类数据
     */
    private void getKindInfo() {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
            @Override
            public void onResult(List<DishType> result) {
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    dishKind = result;
                    dishKindList = dishKind;
                    setKidsMap(dishKindList);
                    getAllDishList();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                    Log.i("获取菜品分类为空", "");
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null") + "," + e.getMessage());
                Log.i("获取菜品分类为空", e.getMessage());
            }
        });
    }

    private void initDishAdapter() {
        progressDialog.disLoading();
        if (dishKidsTopAdp != null && dishKinds != null) {
            currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
            setKidsMap(dishKindList);
            dishKinds.setAdapter(dishKidsTopAdp);
            dishAdp.setDataInfo(currentDishList);
            dishItems.setAdapter(dishAdp);
            dishKidsTopAdp.setSelect(currentPosition);
        }
    }


    private void setKidsMap(List<DishType> dishKindList) {
        dishKidsTopAdp.setData(dishKindList);
    }


    private List<DishCount> dishCountList = new CopyOnWriteArrayList<>();
    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
            dishAdp.setDishCount(dishCountList);
        }
    }


    private List<Integer> setDishList(List<Dish> dishList) {
        List<Integer> dishIdList = new ArrayList<Integer>();
        if (dishList != null && dishList.size() > 0) {
            for (Dish dish : dishList) {
                dishIdList.add(dish.getDishId());
            }
        }
        return dishIdList;
    }

    private void setDishCount(final List<Dish> dishList, int dishCount) {
        if (dishList != null && dishList.size() > 0) {
            final List<Integer> dishIdList = setDishList(dishList);
            if (dishCount == 0) {
                updataDishCount(dishIdList, dishCount, dishList);
            } else {
                DialogUtil.inputDialog(context, ToolsUtils.returnXMLStr("set_sell_out_copies"), ToolsUtils.returnXMLStr("sell_out_copies"), ToolsUtils.returnXMLStr("set_dish_counts"), 0, false, true, new DialogEtCallback() {
                    @Override
                    public void onConfirm(String sth) {
                        int acount = Integer.valueOf(sth);
                        updataDishCount(dishIdList, acount, dishList);
                    }

                    @Override
                    public void onCancle() {
                    }
                });
            }
        }
    }

    private void updataDishCount(final List<Integer> dishList, final int dishCount, final List<Dish> modifyDishs) {
        try {
            DishService dishService = DishService.getInstance();
            dishService.updataDishCount(dishList, dishCount, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    showToast(ToolsUtils.returnXMLStr("sell_out_is_success"));
                    getDishCounts();
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(ToolsUtils.returnXMLStr("sell_out_is_error")+","+ e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("sell_out_is_error")+"," + e.getMessage());
        }
    }


    @OnClick( {R.id.title_left,R.id.btn_dishCount} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left://返回
                UserAction.log("返回", context);
                finish();
                break;
            case R.id.btn_dishCount:
                ToolsUtils.writeUserOperationRecords("全部沽清按钮");
                if (!ToolsUtils.isList(currentDishList)) {
                    setDishCount(currentDishList, 1);
                }
                break;

        }
    }
}
