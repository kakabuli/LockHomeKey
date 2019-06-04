package com.kaadas.lock.activity.device.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class AddBluetoothFamilyMemberActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.et_telephone)
    EditText etTelephone;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.add_user));
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_confirm:
                String phone = etTelephone.getText().toString().trim();
                String myPhone = (String) SPUtils.get(SPUtils.PHONEN, "");

                if (myPhone != null) {
                    if (myPhone.equals(phone)) {
                        ToastUtil.getInstance().showShort(R.string.no_add_my);
                        return;
                    }
                }
                if (NetUtil.isNetworkAvailable()) {
                    if (TextUtils.isEmpty(phone)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                        AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                        return;
                    }


                    if (StringUtil.isNumeric(phone)) {
                        if (!PhoneUtil.isMobileNO(phone)) {
                            // 账户密码错误 请输入正确验证码 调用这个方法传入对应的内容就可以
                            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                            return;
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra(KeyConstants.AUTHORIZATION_TELEPHONE, "86" + phone);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        if (!DetectionEmailPhone.isEmail(phone)) {
//                    ToastUtil.getInstance().showShort(R.string.email_not_right);
                            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                            return;
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra(KeyConstants.AUTHORIZATION_TELEPHONE, phone);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                } else {
                    ToastUtil.getInstance().showShort(R.string.noNet);
                }

                break;
        }
    }
}
