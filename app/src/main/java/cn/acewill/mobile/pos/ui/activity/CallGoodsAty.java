package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.service.PosInfo;

/**
 * Created by DHH on 2017/3/21.
 */

public class CallGoodsAty extends BaseActivity {
    @BindView( R.id.title_left )
    LinearLayout titleLeft;
    @BindView( R.id.right_left_ll )
    LinearLayout rightLeftll;
    @BindView( R.id.table_wv )
    WebView tableWv;
    @BindView( R.id.myProgressBar )
    ProgressBar myProgressBar;

    private PosInfo posInfo;
    private String serverUrl;
    private String callGoodsUrl;
    private String store_url;//appid,brandid,storeid的组合参数
    private String callGoods_manager;//桌台管理的html路径
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_call_goods);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        posInfo = PosInfo.getInstance();
        serverUrl = posInfo.getServerUrl();
        callGoodsUrl = "CallOrderManage.html";
        store_url = "?appid=" + posInfo.getAppId() + "&brandid=" + posInfo.getBrandId() + "&storeid=" + posInfo.getStoreId()+"&username="+posInfo.getUsername();
        callGoods_manager = serverUrl+callGoodsUrl+store_url;

        tableWv.getSettings().setJavaScriptEnabled(true);
        tableWv.getSettings().setLoadWithOverviewMode(true);
        tableWv.getSettings().setDomStorageEnabled(true);
        tableWv.getSettings().setBuiltInZoomControls(true);
        tableWv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        tableWv.getSettings().setSupportZoom(true);

        tableWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                //                Log.e("onPageCommitVisible", url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });

        tableWv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == myProgressBar.getVisibility()) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                    myProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        if (!TextUtils.isEmpty(callGoods_manager)) {
            loadCallGoodsUrl();
        }


        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 加载桌台界面Url
     */
    public void loadCallGoodsUrl() {
        if (!TextUtils.isEmpty(callGoods_manager) && tableWv != null) {
            tableWv.loadUrl(callGoods_manager);
        }
    }
}
