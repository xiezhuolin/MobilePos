package cn.acewill.mobile.pos.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.fragment.BaseFragment;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.interfices.DialogOkCallBack;
import cn.acewill.mobile.pos.model.OrderItemReportData;
import cn.acewill.mobile.pos.model.report.DishReport;
import cn.acewill.mobile.pos.service.PosInfo;
import cn.acewill.mobile.pos.service.ResultCallback;
import cn.acewill.mobile.pos.service.StoreBusinessService;
import cn.acewill.mobile.pos.ui.adapter.PayReportAdp;
import cn.acewill.mobile.pos.ui.adapter.ReportDishAdapter;
import cn.acewill.mobile.pos.utils.ScreenUtil;
import cn.acewill.mobile.pos.widget.ProgressDialogF;

/**
 * 报表
 * Created by aqw on 2017/1/6.
 */
public class ReportFragment extends BaseFragment {

    @BindView( R.id.order_title)
    TextView orderTitle;
    @BindView( R.id.reprot_ll)
    LinearLayout reprotLl;
    @BindView( R.id.more_set)
    LinearLayout moreSet;
    @BindView( R.id.order_num)
    TextView orderNum;
    @BindView( R.id.order_money)
    TextView orderMoney;
    @BindView( R.id.refundo_num)
    TextView refundoNum;
    @BindView( R.id.refundo_money)
    TextView refundoMoney;
    @BindView( R.id.refundd_num)
    TextView refunddNum;
    @BindView( R.id.refundd_money)
    TextView refunddMoney;
    @BindView( R.id.order_report)
    LinearLayout orderReport;
    @BindView( R.id.pay_list)
    ListView payList;
    @BindView( R.id.pay_report)
    LinearLayout payReport;
    @BindView( R.id.dish_list)
    ListView dishList;
    @BindView( R.id.dish_report)
    LinearLayout dishReport;
    @BindView( R.id.refrush)
    LinearLayout refrush;
    @BindView( R.id.title_top)
    RelativeLayout titleTop;
    @BindView( R.id.refrush_iv)
    ImageView refrushIv;

    private int report_posision = 0;//0:订单报表，1：支付统计，2：菜品统计
    private StoreBusinessService storeBusinessService;
    private PopupWindow window;
    private PayReportAdp payReportAdp;
    private ReportDishAdapter reportDishAdapter;
    private ProgressDialogF progressDialogF;
    private DialogOkCallBack callBack;
    private Animation loadingAnim;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        try {
            storeBusinessService = StoreBusinessService.getInstance();
            progressDialogF = new ProgressDialogF(getActivity());
            setStatus();
            initPopupWindow();
            payReportAdp = new PayReportAdp(getContext());
            reportDishAdapter = new ReportDishAdapter(getContext());
            payList.setAdapter(payReportAdp);
            dishList.setAdapter(reportDishAdapter);

            loadingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开始旋转动画
    private void startAnim(){
        if(loadingAnim!=null){
            refrushIv.startAnimation(loadingAnim);
        }
    }
    //停止旋转动画
    private void stopeAnim(){
        refrushIv.clearAnimation();
    }

    //设置回调
    public void setCallBack(DialogOkCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    //初始化顶部弹框
    private void initPopupWindow() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_report, null);
        TextView order_report = (TextView) popupView.findViewById(R.id.order_report);
        TextView pay_report = (TextView) popupView.findViewById(R.id.pay_report);
        TextView dish_report = (TextView) popupView.findViewById(R.id.dish_report);


        //订单统计
        order_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                report_posision = 0;
                setStatus();
                getReport();
            }
        });

        //支付统计
        pay_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                report_posision = 1;
                setStatus();
                getReport();
            }
        });

        //菜品统计
        dish_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                report_posision = 2;
                setStatus();
                getDishReport();
            }
        });

        int width_p = ScreenUtil.getScreenSize(getContext())[0] / 3;
        window = new PopupWindow(popupView);
        window.setWidth(width_p);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.empty_icon));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
    }

    @OnClick({R.id.reprot_ll, R.id.more_set, R.id.refrush})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reprot_ll://切换报表
                int swidth = ScreenUtil.getScreenSize(getContext())[0];
                int xoff = swidth / 2 - swidth / 6;
                window.showAsDropDown(titleTop, xoff, 3);
                break;
            case R.id.more_set://更多
                callBack.onOk();
                break;
            case R.id.refrush://刷新
                refresh();
                break;
        }
    }

    private void setStatus() {
        switch (report_posision) {
            case 0://订单报表
                orderReport.setVisibility(View.VISIBLE);
                payReport.setVisibility(View.GONE);
                dishReport.setVisibility(View.GONE);
                orderTitle.setText("订单汇总");
                break;
            case 1://支付统计
                orderReport.setVisibility(View.GONE);
                payReport.setVisibility(View.VISIBLE);
                dishReport.setVisibility(View.GONE);
                orderTitle.setText("支付统计");
                break;
            case 2://菜品统计
                orderReport.setVisibility(View.GONE);
                payReport.setVisibility(View.GONE);
                dishReport.setVisibility(View.VISIBLE);
                orderTitle.setText("菜品销售");
                break;
        }
    }

    //刷新接口数据
    public void refresh() {
        if (report_posision == 0 || report_posision == 1) {//调用订单与支付统计接口
            getReport();
        } else {//调用菜品统计接口
            getDishReport();
        }
    }

    /**
     * 获取当日班次和当天的报表数据
     */
    public void getReport() {
        try {
            startAnim();
            String workShiftId = PosInfo.getInstance().getWorkShiftId() + "";
            if (TextUtils.isEmpty(workShiftId)) {
                stopeAnim();
                return;
            }
            storeBusinessService.getReportData(workShiftId, new ResultCallback<List<OrderItemReportData>>() {
                @Override
                public void onResult(List<OrderItemReportData> result) {
                    stopeAnim();
                    if (result != null && result.size() > 0) {

                        for (OrderItemReportData item : result) {

                            if ("当天".equals(item.getNote())) {

                                orderNum.setText(item.getOrderTotal() + "");
                                orderMoney.setText("￥" + item.getSalesTotal());
                                refundoNum.setText(item.getExitOrderTotal() + "");
                                refundoMoney.setText("￥" + item.getExitOrderSalesTotal());
                                refunddNum.setText(item.getExitItemTotal() + "");
                                refunddMoney.setText("￥" + item.getExitItemSalesTotal());

                                List<OrderItemReportData.ItemSalesData> itemSalesDatas = item.getItemSalesDatas();
                                if (itemSalesDatas != null && itemSalesDatas.size() > 0) {

                                    int count = 0;
                                    BigDecimal money = new BigDecimal(0);
                                    for (OrderItemReportData.ItemSalesData itemSalesData : itemSalesDatas) {
                                        count += itemSalesData.itemCounts;
                                        money = money.add(itemSalesData.total);
                                    }
                                    OrderItemReportData.ItemSalesData itemData = new OrderItemReportData.ItemSalesData();
                                    itemData.name = "合计";
                                    itemData.itemCounts = count;
                                    itemData.total = money;
                                    itemSalesDatas.add(itemData);

                                    payReportAdp.setData(itemSalesDatas);
                                }

                            }

                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.e("getReport", e.getMessage());
                    stopeAnim();
                    showToast(e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取菜品销售报表
     */
    private void getDishReport() {
        startAnim();
        storeBusinessService.getDishReport(new ResultCallback<List<DishReport>>() {
            @Override
            public void onResult(List<DishReport> result) {
                stopeAnim();
                if (result != null && result.size() > 0) {
                    reportDishAdapter.setData(result);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                stopeAnim();
                e.printStackTrace();
            }
        });
    }
}
