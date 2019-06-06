package com.kaadas.lock.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.kaadas.lock.utils.ftp.GeTui;

/**
 * Create By denganzhi  on 2019/6/5
 * Describe
 */

public class RecordTools {

    public static boolean validateMicAvailability(){
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 22050);
        try{
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED ){
                available = false;

            }
            Log.e(GeTui.VideoLog,"recorder state:"+recorder.getRecordingState()+"");
            recorder.startRecording();
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING){
                recorder.stop();
                available = false;

            }
            recorder.stop();
        } finally{
            recorder.release();
            recorder = null;
        }

        return available;
    }
}
