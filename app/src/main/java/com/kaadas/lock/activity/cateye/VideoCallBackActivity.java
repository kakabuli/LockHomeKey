package com.kaadas.lock.activity.cateye;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.BluetoothOpenLockRecordFragment;
import com.kaadas.lock.fragment.BluetoothWarnInformationFragment;
import com.kaadas.lock.fragment.RecordingFragment;
import com.kaadas.lock.fragment.SnapshotFragment;
import com.kaadas.lock.fragment.SnapshotFragment1;
import com.kaadas.lock.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoCallBackActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.tv_content)
    TextView tvContent;

    @BindView(R.id.video_recording)
    TextView videoRecording;
    @BindView(R.id.snapshot_information)
    TextView snapshotInformation;

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    RecordingFragment recordingFragment;
    SnapshotFragment1 snapshotFragment;
    public static boolean isRunning = false;

    String gatewayId="GW01182510163";
    String deviceId="CH01191510002";
    Bundle args = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_back);
        ButterKnife.bind(this);
        tvContent.setText(getString(R.string.video_callback_info));
        videoRecording.setOnClickListener(this);
        snapshotInformation.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(gatewayId)){
            args.putString(Constants.DEVICE_ID, deviceId);
            args.putString(Constants.GATEWAY_ID,gatewayId);
        }
        initFragment();
        isRunning = true;




    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        recordingFragment = new RecordingFragment();
        recordingFragment.setArguments(args);
        transaction.add(R.id.content, recordingFragment);
        transaction.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.video_recording:
                //开锁记录
                videoRecording.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                videoRecording.setTextColor(getResources().getColor(R.color.white));
                snapshotInformation.setBackgroundResource(0);
                snapshotInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if (recordingFragment != null) {
                    fragmentTransaction.show(recordingFragment);
                } else {
                    recordingFragment = new RecordingFragment();
                    recordingFragment.setArguments(args);
                    fragmentTransaction.add(R.id.content, recordingFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.snapshot_information:
                //警告信息
                videoRecording.setBackgroundResource(0);
                videoRecording.setTextColor(getResources().getColor(R.color.c1F96F7));
                snapshotInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                snapshotInformation.setTextColor(getResources().getColor(R.color.white));
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if (snapshotFragment != null) {
                    fragmentTransaction.show(snapshotFragment);
                } else {
                    snapshotFragment = new SnapshotFragment1();
                    snapshotFragment.setArguments(args);
                    fragmentTransaction.add(R.id.content, snapshotFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (recordingFragment != null) {
            ft.hide(recordingFragment);
        }
        if (snapshotFragment != null) {
            ft.hide(snapshotFragment);
        }


    }
}
