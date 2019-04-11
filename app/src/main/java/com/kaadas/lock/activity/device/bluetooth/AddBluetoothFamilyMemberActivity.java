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
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/2/24
 */
public class AddBluetoothFamilyMemberActivity extends AppCompatActivity implements View.OnClickListener {
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

                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.getInstance().showShort(R.string.phone_number_con_not_empty);
                    return;
                }
                if (myPhone != null) {
                    if (myPhone.equals(phone)) {
                        ToastUtil.getInstance().showShort(R.string.no_add_my);
                        return;
                    }
                }

                if (!PhoneUtil.isMobileNO(phone)) {
                    ToastUtil.getInstance().showShort(R.string.phone_not_right);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.AUTHORIZATION_TELEPHONE, "86" + phone);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
