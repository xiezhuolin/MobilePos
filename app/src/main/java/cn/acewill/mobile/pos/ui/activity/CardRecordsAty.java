package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.model.order.CardRecord;
import cn.acewill.mobile.pos.presenter.OrderPresenter;
import cn.acewill.mobile.pos.ui.DialogView;
import cn.acewill.mobile.pos.ui.adapter.CardRecordsAdapter;
import cn.acewill.mobile.pos.utils.ToolsUtils;

/**
 * 订单信息列表
 * Created by DHH on 2016/6/12.
 */
public class CardRecordsAty extends BaseActivity implements DialogView, SwipeRefreshLayout.OnRefreshListener, CardRecordsAdapter.RefrushLisener {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.tv_number )
    TextView tvNumber;

    private OrderPresenter orderPresenter;
    private CardRecordsAdapter adapter;
    private List<CardRecord> orderList = new ArrayList<CardRecord>();
    private int lastVisibleItem = 0;
    private int limit = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_card_records);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        myApplication.addPage(CardRecordsAty.this);

        orderPresenter = new OrderPresenter(this);
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new CardRecordsAdapter(context, orderList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);
        if (ToolsUtils.logicIsTable()) {
            tvNumber.setText(ToolsUtils.returnXMLStr("table_number"));
        } else {
            if (StoreInfor.cardNumberMode) {
                tvNumber.setText(ToolsUtils.returnXMLStr("card_number"));
            } else {
                tvNumber.setText("取餐号");
            }
        }

        orderLv.setAdapter(adapter);

        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);
                    orderPresenter.cardRecords();
                }
            }
        });
    }

    private void initData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        orderPresenter.cardRecords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void showDialog() {
        showProgress(ToolsUtils.returnXMLStr("get_cardrecord_order_list"));
    }

    @Override
    public void dissDialog() {
        dissmiss();
    }

    @Override
    public void showError(PosServiceException e) {
        orderSrl.setRefreshing(false);
        showToast(ToolsUtils.returnXMLStr("cardrecord_list_error") + e.getMessage());
    }

    @Override
    public <T> void callBackData(T t) {
        List<CardRecord> orders = (List<CardRecord>) t;
        adapter.setData(orders);
        orderList = ToolsUtils.cloneTo(orders);
        if (orders != null && orders.size() > 0) {
            if (orders.size() < limit) {
                adapter.changeMoreStatus(adapter.NO_MORE);
            } else {
                adapter.changeMoreStatus(adapter.LOAD_MORE);
            }
        } else {
            adapter.changeMoreStatus(adapter.NO_MORE);
        }
        orderSrl.setRefreshing(false);
    }

    @Override
    public void refrush() {
        initData();
    }

    @OnClick( {R.id.title_left} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left:
                finish();
                break;
        }
    }
}
