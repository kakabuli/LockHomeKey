package com.kaadas.lock.activity.device.cateye.more;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.cateye.CateyeMoreDeviceInformationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class CateyeMoreActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_bell)
    TextView tvBell;
    @BindView(R.id.rl_bell)
    RelativeLayout rlBell;
    @BindView(R.id.tv_ringnumber)
    TextView tvRingnumber;
    @BindView(R.id.rl_ring_number)
    RelativeLayout rlRingNumber;
    @BindView(R.id.iv_smart_monitor)
    ImageView ivSmartMonitor;
    @BindView(R.id.rl_smart_monitor)
    RelativeLayout rlSmartMonitor;
    @BindView(R.id.tv_resolution)
    TextView tvResolution;//视频分辨率
    @BindView(R.id.rl_resolution)
    RelativeLayout rlResolution;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    boolean smartMonitorStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_more);
        ButterKnife.bind(this);
        initClick();
        initData();
    }

    private void initData() {
        //todo 获取到设备名字时,key都加上设备名字
        smartMonitorStatus = (boolean) SPUtils.get(KeyConstants.SMART_MONITOR_STATUS, true);
        if (smartMonitorStatus) {
            ivSmartMonitor.setImageResource(R.mipmap.iv_open);
        } else {
            ivSmartMonitor.setImageResource(R.mipmap.iv_close);
        }
    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.settting));
        rlDeviceName.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlSmartMonitor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_device_name:
                //设备名字
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_device_name));
                //获取到设备名称设置
                editText.setText("");
                editText.setSelection("".length());
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        //todo 判断名称是否修改
                /*        if (deviceNickname!=null){
                            if (deviceNickname.equals(name)){
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }*/
                        tvDeviceName.setText(name);
                        alertDialog.dismiss();
                    }
                });
                break;


            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }
                });
                break;
            case R.id.rl_device_information:
                intent = new Intent(this, CateyeMoreDeviceInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_smart_monitor:
                if (smartMonitorStatus) {
                    //打开状态 现在关闭
                    ivSmartMonitor.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.SMART_MONITOR_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivSmartMonitor.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.SMART_MONITOR_STATUS, true);
                }
                smartMonitorStatus = !smartMonitorStatus;
                break;
        }
    }
}
