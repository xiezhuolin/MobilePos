/*
 * Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.acewill.posdish.zxing.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.acewill.posdish.zxing.camera.CameraManager;
import com.acewill.posdish.zxing.decode.DecodeThread;
import com.google.zxing.Result;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.ui.activity.ScanActivity;


/**
 * This class handles all the messaging which comprises the state machine for capture. <br>
 * CaptureActivityHandler类是�?个针对扫描任务的Handler，可接收的message有启动扫描（restart_preview）�?�扫描成功（decode_succeeded）�?�扫描失败（decode_failed）等等�??
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class CaptureActivityHandler extends Handler {

	private final ScanActivity activity;
	/**
	 * 真正负责扫描任务的核心线�?
	 */
	private final DecodeThread decodeThread;
	private final CameraManager cameraManager;
	private State state;

	/**
	 * 当前扫描的状�?
	 */
	private enum State {
		/**
		 * 预览
		 */
		PREVIEW,
		/**
		 * 扫描成功
		 */
		SUCCESS,
		/**
		 * 结束扫描
		 */
		DONE
	}

	public CaptureActivityHandler(ScanActivity activity, CameraManager cameraManager, int decodeMode) {
		this.activity = activity;
		// 1. 启动扫描线程
		decodeThread = new DecodeThread(activity, decodeMode);
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		// 2. �?启相机预览界�?
		cameraManager.startPreview();
		// 3. 将preview回调函数与decodeHandler绑定、调用viewfinderView
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			case R.id.restart_preview: // 准备进行下一次扫�?
				restartPreviewAndDecode();
				break;
			case R.id.decode_succeeded:
				state = State.SUCCESS;
				Bundle bundle = message.getData();

				activity.handleDecode((Result) message.obj, bundle);
				break;
			case R.id.decode_failed:
				// We're decoding as fast as possible, so when one decode fails,
				// start another.
				state = State.PREVIEW;
				cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
				break;
			case R.id.return_scan_result:
				activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
				activity.finish();
				break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	/**
	 * 完成�?次扫描后，只�?要再调用此方法即�?
	 */
	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
		}
	}
}
