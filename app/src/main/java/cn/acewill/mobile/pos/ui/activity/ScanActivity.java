package cn.acewill.mobile.pos.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.acewill.posdish.zxing.camera.CameraManager;
import com.acewill.posdish.zxing.decode.DecodeThread;
import com.acewill.posdish.zxing.util.BeepManager;
import com.acewill.posdish.zxing.util.CaptureActivityHandler;
import com.acewill.posdish.zxing.util.InactivityTimer;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Field;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;


/**
 * 创建日期：2015年4月27日<br>
 * 描述：扫一扫页面 This activity opens the camera and does the actual scanning on a background thread. It draws a viewfinder to help the user place the barcode correctly, shows feedback as the image processing is happening, and then overlays the results when a scan is successful.
 *
 * 此Activity所做的事： 1.开启camera，在后台独立线程中完成扫描任务； 2.绘制了一个扫描区（viewfinder）来帮助用户将条码置于其中以准确扫描；
 *
 * @author HJK
 */
public class ScanActivity extends BaseActivity implements SurfaceHolder.Callback {
	private static final String TAG = ScanActivity.class.getSimpleName();
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private LinearLayout mBackText;
	/**
	 * 是否有预览
	 */
	private boolean isHasSurface = false;
	/**
	 * 活动监控器。如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;
	/**
	 * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
	 */
	private BeepManager beepManager;
	private TranslateAnimation translateAnimation;
	private SurfaceView surfaceView = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private View mErrView;
	private ImageView scanLineHorizontal;
	private Rect mCropRect = null;

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_scan);
		// 用于一键退出的activity管理器
		initView();
		regListener();
	}

	// 初始化界面
	private void initView() {
		mBackText = (LinearLayout) this.findViewById(R.id.title_left);
		surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLineHorizontal = (ImageView) findViewById(R.id.capture_scan_line_horizontal);
		mErrView = findViewById(R.id.errView);
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		startAnimation(scanLineHorizontal);
	}

	private void regListener() {
		mBackText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	// 扫描动画
	private void startAnimation(View view) {
		if (view == null)
			return;
		translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
		translateAnimation.setDuration(2500);
		translateAnimation.setRepeatCount(-1);
		translateAnimation.setRepeatMode(Animation.RESTART);
		view.startAnimation(translateAnimation);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.

		// 相机初始化的动作需要开启相机并测量屏幕大小，这些操作
		// 不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的 话，
		// 会导致扫描窗口的尺寸计算有误的bug
		cameraManager = new CameraManager(getApplication());
		handler = null;
		// 摄像头预览功能必须借助SurfaceView，因此也需要在一开始对其进行初始化
		// 如果需要了解SurfaceView的原理
		// 参考:http://blog.csdn.net/luoshengyang/article/details/8661317
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// 防止sdk8的设备初始化预览异常
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}
		// 恢复活动监控器
		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		// 关闭摄像头
		cameraManager.closeDriver();
		if (!isHasSurface) {
			surfaceView.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	// 获取到二维码信息
		public void handleDecode(Result rawResult, Bundle bundle) {
			// 重新计时
			inactivityTimer.onActivity();
			beepManager.playBeepSoundAndVibrate();
			String msg = rawResult.getText();
			Log.e("333333", "msg"+msg);
			Intent intent = new Intent();
			intent.putExtra("data", msg);
			setResult(-110, intent);
			finish();
		}

		private void initCamera(SurfaceHolder surfaceHolder) {
			if (surfaceHolder == null) {
				throw new IllegalStateException("No SurfaceHolder provided");
			}
			if (cameraManager.isOpen()) {
				Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
				return;
			}
			try {
				cameraManager.openDriver(surfaceHolder);
				// Creating the handler starts the preview, which can also throw a
				// RuntimeException.
				if (handler == null) {
					handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
				}

				initCrop();
			} catch (IOException ioe) {
				Log.w(TAG, ioe);
				displayFrameworkBugMessageAndExit();
			} catch (RuntimeException e) {
				// Barcode Scanner has seen crashes in the wild of this variety:
				// java.?lang.?RuntimeException: Fail to connect to camera service
				Log.w(TAG, "Unexpected error initializing camera", e);
				displayFrameworkBugMessageAndExit();
			}
		}

		private void displayFrameworkBugMessageAndExit() {
			showToast("四方调度平台已被禁止权限：调用摄像头。请在手机管家-权限管理重新授权。");
			mErrView.setVisibility(View.VISIBLE);
			if (translateAnimation != null)
				translateAnimation.cancel();
			// camera error
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("摄像头打开失败");
			builder.setMessage("请在手机的“设置>隐私和安全>权限管理>应用程序>四方调度平台>调用摄像头”，设置为“允许”后再试试");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			// builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			//
			// @Override
			// public void onCancel(DialogInterface dialog) {
			// finish();
			// }
			// });
			builder.show();
		}

		public void restartPreviewAfterDelay(long delayMS) {
			if (handler != null) {
				handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
			}
		}

		/**
		 * 获取截取的矩形区域
		 *
		 * @return
		 */
		public Rect getCropRect() {
			return mCropRect;
		}

		/**
		 * 初始化截取的矩形区域
		 */
		private void initCrop() {
			int cameraWidth = cameraManager.getCameraResolution().y;
			int cameraHeight = cameraManager.getCameraResolution().x;

			/** 获取布局中扫描框的位置信息 */
			int[] location = new int[2];
			scanCropView.getLocationInWindow(location);

			int cropLeft = location[0];
			int cropTop = location[1] - getStatusBarHeight();

			int cropWidth = scanCropView.getWidth();
			int cropHeight = scanCropView.getHeight();

			/** 获取布局容器的宽高 */
			int containerWidth = scanContainer.getWidth();
			int containerHeight = scanContainer.getHeight();

			/** 计算最终截取的矩形的左上角顶点x坐标 */
			int x = cropLeft * cameraWidth / containerWidth;
			/** 计算最终截取的矩形的左上角顶点y坐标 */
			int y = cropTop * cameraHeight / containerHeight;

			/** 计算最终截取的矩形的宽度 */
			int width = cropWidth * cameraWidth / containerWidth;
			/** 计算最终截取的矩形的高度 */
			int height = cropHeight * cameraHeight / containerHeight;

			/** 生成最终的截取的矩形 */
			mCropRect = new Rect(x, y, width + x, height + y);
		}

		private int getStatusBarHeight() {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object obj = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = Integer.parseInt(field.get(obj).toString());
				return getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
}
