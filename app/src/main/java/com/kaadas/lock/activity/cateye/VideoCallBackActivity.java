package com.kaadas.lock.activity.cateye;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.BluetoothOpenLockRecordFragment;
import com.kaadas.lock.fragment.BluetoothWarnInformationFragment;
import com.kaadas.lock.fragment.RecordingFragment;
import com.kaadas.lock.fragment.SnapshotFragment;
import com.kaadas.lock.fragment.SnapshotFragment1;

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

    private FragmentManager manager;
    private FragmentTransaction transaction;
    RecordingFragment recordingFragment;
    SnapshotFragment1 snapshotFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_back);
        ButterKnife.bind(this);
        tvContent.setText(getString(R.string.video_callback_info));
        videoRecording.setOnClickListener(this);
        snapshotInformation.setOnClickListener(this);
        initFragment();

    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        recordingFragment = new RecordingFragment();
        transaction.add(R.id.content, recordingFragment);
        transaction.commit();

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
