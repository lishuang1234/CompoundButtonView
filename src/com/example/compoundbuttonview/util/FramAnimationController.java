package com.example.compoundbuttonview.util;

import android.R.integer;
import android.os.Handler;
import android.os.Message;

public class FramAnimationController {
	private final static int MSG_ANIMATE = 100;
	public  final static int ANIMATINO_FRAME_DURATION = 1000 / 60;
	private final static Handler mHandler = new AnimationHandler();

	public static void requestAnimationFrame(Runnable runnable) {
		Message message = new Message();
		message.what = MSG_ANIMATE;
		message.obj = runnable;
		mHandler.sendMessageDelayed(message, ANIMATINO_FRAME_DURATION);
	}

	public static void requestFrameDelay(Runnable runnable, long delay) {
		Message message = new Message();
		message.what = MSG_ANIMATE;
		message.obj = runnable;
		mHandler.sendMessageDelayed(message, delay);

	}

	private static class AnimationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_ANIMATE:
				((Runnable) msg.obj).run();
				break;
			default:
				break;
			}
		}

	}
}
