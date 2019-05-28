package com.kaadas.lock.publiclibrary.ota;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.ota.p6.P6OtaUpgradeActivity;
import com.kaadas.lock.publiclibrary.ota.ti.TiOtaUpgradeActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OTADialogActivity extends BaseActivity<IOtaView, OtaPresenter<IOtaView>> implements IOtaView {
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

        sn = (String) SPUtils.get(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), "");
        version = (String) SPUtils.get(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), "");
        LogUtils.e("获取版本号的KEy  " + sn);
        LogUtils.e("获取版本号是  " + version);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sn) || TextUtils.isEmpty(version)) {  //如果本地保存的SN或者version为空
                    ToastUtil.getInstance().showLong(R.string.get_update_info_failed);
                    finish();
                    return;
                }
                //获取版本信息
                mPresenter.checkOtaInfo(sn,version);

                MyApplication.getInstance().getBleService().release();


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
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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
    public void onFailedServer(OTAResult result) {
        ToastUtil.getInstance().showLong(R.string.get_server_update_info_failed);
        finish();
    }

    @Override
    public void onGetOtaInfoSuccess(OTAResult.UpdateFileInfo updateFileInfo) {
        MyApplication.getInstance().getBleService().release();
        Intent intent = new Intent();
        intent.putExtra(OtaConstants.fileName, "XiaoKai_" + updateFileInfo.getFileVersion() + ".bin");
        intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock());
        intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
        intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
        if (bleLockInfo.getBleType()==1){
            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5() + ".bin");
            intent.setClass(OTADialogActivity.this, TiOtaUpgradeActivity.class);
        }else {
            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5()+ ".cyacd2");
            intent.setClass(OTADialogActivity.this, P6OtaUpgradeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.net_exception_tryagain);
        finish();
    }















}
