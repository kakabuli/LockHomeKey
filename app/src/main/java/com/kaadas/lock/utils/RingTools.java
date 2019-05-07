package com.kaadas.lock.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphonePreferences;

import org.linphone.mediastream.video.capture.hwconf.Hacks;

import java.io.FileInputStream;
import java.io.IOException;

import static android.media.AudioManager.MODE_RINGTONE;
import static android.media.AudioManager.STREAM_RING;

/**
 * Create By denganzhi  on 2019/3/5
 * Describe
 */

public class RingTools {

	private AudioManager mAudioManager;

	private  Context context;
	private Vibrator mVibrator;
	private MediaPlayer mRingerPlayer;
	private boolean isRinging;

	public RingTools(Context context) {
		this.context = context;
	}

	public synchronized  void startRinging() {

		mAudioManager = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE));

		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);


//		if (!LinphonePreferences.instance().isDeviceRingtoneEnabled()) {
//			// Enable speaker audio route, linphone library will do the ringing itself automatically
//			routeAudioToSpeaker();
//			return;
//		}
//
//		if (mR.getBoolean(R.bool.allow_ringing_while_early_media)) {
//			routeAudioToSpeaker(); // Need to be able to ear the ringtone during the early media
//		}

		//if (Hacks.needGalaxySAudioHack())
		mAudioManager.setMode(MODE_RINGTONE);

		try {
			if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) && mVibrator != null) {
				long[] patern = {0, 1000, 1000};
				mVibrator.vibrate(patern, 1);
			}
			if (mRingerPlayer == null) {
				//requestAudioFocus(STREAM_RING);
				mRingerPlayer = new MediaPlayer();
				mRingerPlayer.setAudioStreamType(STREAM_RING);

				String ringtone = LinphonePreferences.instance().getRingtone(Settings.System.DEFAULT_RINGTONE_URI.toString());
				try {
					if (ringtone.startsWith("content://")) {
						mRingerPlayer.setDataSource(context, Uri.parse(ringtone));
					} else {
						FileInputStream fis = new FileInputStream(ringtone);
						mRingerPlayer.setDataSource(fis.getFD());
						fis.close();
					}
				} catch (IOException e) {
					Log.e("linphoneManager", e.toString() + "Cannot set ringtone");
				}

				mRingerPlayer.prepare();
				mRingerPlayer.setLooping(true);
				mRingerPlayer.start();
			} else {
				Log.w("linphoneManager", "already ringing");
			}
		} catch (Exception e) {
			Log.e("linphoneManager", e.toString() + "cannot handle incoming call");
		}
		isRinging = true;
	}

//	private void requestAudioFocus(int stream) {
//		if (!mAudioFocused) {
//			int res = mAudioManager.requestAudioFocus(null, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
//			Log.d("linphoneManager", "Audio focus requested: " + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "Granted" : "Denied"));
//			if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mAudioFocused = true;
//		}
//	}

	public synchronized void stopRinging() {
		if (mRingerPlayer != null) {
			mRingerPlayer.stop();
			mRingerPlayer.release();
			mRingerPlayer = null;
		}
		if (mVibrator != null) {
			mVibrator.cancel();
		}

		if (Hacks.needGalaxySAudioHack())
			mAudioManager.setMode(AudioManager.MODE_NORMAL);

		isRinging = false;
		// You may need to call galaxys audio hack after this method
//		if (!BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
//			if (mServiceContext.getResources().getBoolean(R.bool.isTablet)) {
//				Log.d("Stopped ringing, routing back to speaker");
//				routeAudioToSpeaker();
//			} else {
//				Log.d("Stopped ringing, routing back to earpiece");
//				routeAudioToReceiver();
//			}
//		}
	}
}
