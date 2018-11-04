package cn.acewill.mobile.pos.ui.activity;

import android.os.Bundle;

import butterknife.ButterKnife;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;

/**
 * Created by DHH on 2016/6/12.
 */
public class WelcomeAty extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_base);
        ButterKnife.bind(this);
        myApplication.addPage(WelcomeAty.this);
    }

}
