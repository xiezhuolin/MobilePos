package cn.acewill.mobile.pos.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import cn.acewill.mobile.pos.base.activity.BaseActivity;

/**
 * Created by DHH on 2018/7/19.
 */

public class SplashAty extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		bindLogService();
		startActivity(new Intent(SplashAty.this, LoginAty.class));
		finish();
	}

//	//绑定日志Service
//	private void bindLogService() {
//		Intent intent = new Intent(SplashAty.this, LogService.class);
//		startService(intent);
//	}
}
