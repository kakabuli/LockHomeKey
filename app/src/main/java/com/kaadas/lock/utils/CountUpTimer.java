package com.kaadas.lock.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * 正计时的类
 */

public abstract class CountUpTimer {

	/**
	 * Millis since epoch when alarm should stop.
	 */

	/**
	 * The interval in millis that the user receives callbacks
	 */
	private final long mCountdownInterval;

	private long startTime;

	/**
	 * boolean representing if the timer was cancelled
	 */

	/**
	 * is called.
	 *
	 * @param countDownInterval The interval along the way to receive
	 *                          {@link #onTick(long)} callbacks.
	 */
	public CountUpTimer(long countDownInterval) {
		mCountdownInterval = countDownInterval;
	}


	/**
	 * Start the countdown.
	 */
	public synchronized final CountUpTimer start() {
		startTime = SystemClock.elapsedRealtime() ;
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
		return this;
	}

	/**
	 * 关闭倒计时
	 */
	public synchronized final void stop() {
		mHandler.removeMessages(MSG);
	}
	/**
	 * Callback fired on regular interval.
	 *
	 * @param millisUntilFinished The amount of time until finished.
	 */
	public abstract void onTick(long millisUntilFinished);

	/**
	 * Callback fired when the time is up.
	 */


	private static final int MSG = 1;


	// handles counting down
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			synchronized (CountUpTimer.this) {
					final long millisLeft =   SystemClock.elapsedRealtime()-startTime;
					onTick(millisLeft);
					sendMessageDelayed(obtainMessage(MSG), mCountdownInterval);
			}
		}
	};


}
