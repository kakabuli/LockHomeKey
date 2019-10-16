package com.kaadas.lock.publiclibrary.ota.face;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.FaceOtaPresenter;
import com.kaadas.lock.mvp.view.IFaceOtaView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ota.DownFileUtils;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;

import java.io.File;

public class RequestOtaAndLinkWifiActivity extends BaseBleActivity<IFaceOtaView, FaceOtaPresenter<IFaceOtaView>> implements IFaceOtaView {
    private String binDownUrl;
    private String fileName;
    private String filePath;
    private TextView otaStatus;
    private SeekBar otaProgress;
    private SocketOtaUtil socketOtaUtil;
    private BleLockInfo bleLockInfo;
    private String version;
    private int number;
    private int otaType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ota_and_link_wifi);
        bleLockInfo = mPresenter.getBleLockInfo();
        Intent intent = getIntent();
        fileName = intent.getStringExtra(OtaConstants.fileName);
        binDownUrl = intent.getStringExtra(OtaConstants.bindUrl);
        version = intent.getStringExtra(OtaConstants.bindUrl);
        number = intent.getIntExtra(OtaConstants.bindUrl, 1);
        otaType = intent.getIntExtra(OtaConstants.bindUrl, 0);

    }

    @Override
    protected FaceOtaPresenter<IFaceOtaView> createPresent() {
        return new FaceOtaPresenter<>();
    }


}
