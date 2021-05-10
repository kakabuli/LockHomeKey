package com.kaadas.lock.publiclibrary.ota.ble;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleCheckInfoActivity;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OTADialogActivity extends BaseBleCheckInfoActivity<IOtaView, OtaPresenter<IOtaView>> implements IOtaView {
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private BleLockInfo bleLockInfo;
    private String sn;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otadialog);

        ButterKnife.bind(this);
        bleLockInfo = (BleLockInfo) getIntent().getSerializableExtra(KeyConstants.BLE_DEVICE_INFO);
        String content = String.format(getString(R.string.oad_content), bleLockInfo.getServerLockInfo().getLockNickName());
        tvContent.setText(content);

        sn = bleLockInfo.getServerLockInfo().getDeviceSN();
        version = bleLockInfo.getServerLockInfo().getSoftwareVersion();

        LogUtils.e("获取版本号的KEy  server  " + sn);
        LogUtils.e("获取版本号是 server " + version);

        if (TextUtils.isEmpty(sn) || TextUtils.isEmpty(version)){
            sn = (String) SPUtils.get(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), ""); //Key
            version = (String) SPUtils.get(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), ""); //Key

            LogUtils.e("获取版本号的KEy  local  " + sn);
            LogUtils.e("获取版本号是 local " + version);
        }

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sn) || TextUtils.isEmpty(version)) {  //如果本地保存的SN或者version为空
                    ToastUtils.showLong(R.string.get_update_info_failed);
                    finish();
                    return;
                }
                //获取版本信息

                mPresenter.checkOTAInfo(sn,version,1);
//                mPresenter.checkOtaInfo("K901192210013","1.01.007");
                LogUtils.e("OTA  升级  断开连接");
                MyApplication.getInstance().getBleService().release();  // OTA  升级  断开连接"
            }
        });


        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setScaleX((float) 0.96);
        win.getDecorView().setScaleY((float) 0.96);

        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = (int) (width*0.9);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
        //设置点击外部空白处可以关闭Activity
        this.setFinishOnTouchOutside(true);
    }

    @Override
    protected OtaPresenter<IOtaView> createPresent() {
        return new OtaPresenter<>();
    }

    @Override
    public void onEnterOta() {
        super.onEnterOta();
        finish();
    }
}
