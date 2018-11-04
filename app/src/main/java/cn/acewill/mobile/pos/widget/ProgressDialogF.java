/*
 * 鍒涘缓鏃ユ湡锛?2012-12-15
 */
package cn.acewill.mobile.pos.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import cn.acewill.mobile.pos.R;


/**
 * 版权所有 中软国际有限公司。 保留所有权利。<br>
 * 项目名：飞信 - Android客户端<br>
 * 描述：
 * 
 * @version 1.0
 * @since JDK1.5
 */
public class ProgressDialogF extends Dialog {
	
	private Activity aty;
	private final TextView mMessage;

	public ProgressDialogF(Context context) {
		super(context, R.style.Theme_Dialog);
		aty = (Activity)context;
		// View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
		setContentView(R.layout.dialog_progress);
		mMessage = (TextView) findViewById(R.id.textview_message);
		setCanceledOnTouchOutside(false);
		// setWindowWidth(0.9);
		setWindowWidth(-1);
	}

	public ProgressDialogF(Context context, double percent) {
		super(context,R.style.Theme_Dialog);
		// View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
		setContentView(R.layout.dialog_progress);
		mMessage = (TextView) findViewById(R.id.textview_message);
		// setWindowWidth(0.9);
		setWindowWidth(percent);
	}
	/**
	 * 
	 */
	public void setWindowWidth(double percent) {
//		Window window = this.getWindow();
////		// LayoutParams params = new LayoutParams();
////		// params.x = 0;//璁剧疆x鍧愭爣
////		// params.y = 0;//璁剧疆y鍧愭爣
////		// window.setAttributes(params);
//		window.setGravity(Gravity.CENTER);
//		Display play = window.getWindowManager().getDefaultDisplay();
//		DisplayMetrics metrics = new DisplayMetrics();
//		play.getMetrics(metrics);
//		window.setLayout(percent == -1 ? LayoutParams.WRAP_CONTENT : (int) (metrics.widthPixels * percent), percent == -1 ? LayoutParams.WRAP_CONTENT : (int) (metrics.widthPixels * percent));
//		
	}

	public void setMessage(CharSequence message) {
		mMessage.setText(message);
	}

	public void setMessage(int resId) {
		mMessage.setText(resId);
	}

	public void setIndeterminate(boolean is) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#show()
	 */
	@Override
	public void show() {
		if(this != null && !this.isShowing() )
		{
			super.show();
		}
	}

	public void showLoading(String msg) {

		try{
			if (this != null &&this.isShowing())
				return;
			if (!TextUtils.isEmpty(msg))
				this.setMessage(msg);
			this.show();
		}catch (Exception e){
			Log.i("ProgressDialog",e.getMessage());
		}

	}

	public void disLoading() {
		if (aty != null && !aty.isFinishing() && this != null && this.isShowing())
			this.cancel();
	}
}
