package com.kaadas.lock.utils;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by David
 * 时间工具类
 * */
public class TimeUtils {
	
	private int time = 60;
	private Timer timer;
	private TextView tvTime;
	private TextView tvWidget;


	public TimeUtils(TextView tvTime,TextView tvWidget) {
		super();
		this.tvTime = tvTime;
		this.tvWidget = tvWidget;
	}
	
	
	public void RunTimer() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				time--;
				Message msg = handler.obtainMessage();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(task, 100, 1000);
	}
	public void cancelTimer(){
		timer.cancel();
		tvTime.setText(MyApplication.getInstance().getString(R.string.get_verification));
		tvTime.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.c1F96F7));
//		tvTime.setVisibility(View.GONE);
	}
	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if (time > 0) {
						//重新获取（60）
						String verificationContent = String.format(MyApplication.getInstance().getString(R.string.get_verification_content), time+"");
						tvTime.setText(verificationContent);
						tvTime.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.c999999));
						tvWidget.setEnabled(false);
//						tvTime.setVisibility(View.VISIBLE);
					} else {
						timer.cancel();
						tvTime.setText(MyApplication.getInstance().getString(R.string.get_verification));
						tvTime.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.c1F96F7));
						tvWidget.setEnabled(true);
//						tvTime.setVisibility(View.GONE);
					}
					break;
				default:
					break;
			}
		}
		
		
		;
	};
	
	/**
	 * 当地时间 ---> UTC时间
	 * @return
	 */
	public static String local2UTC(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
		String gmtTime = sdf.format(new Date());
		return gmtTime;
	}
	
	/**
	 * UTC时间 ---> 当地时间
	 * @param utcTime   UTC时间
	 * @return
	 */
	public static String utc2Local(String utcTime) {
		SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//UTC时间格式
		utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gpsUTCDate = null;
		try {
			gpsUTCDate = utcFormater.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat localFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//当地时间格式
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(gpsUTCDate.getTime());
		return localTime;
	}


}
