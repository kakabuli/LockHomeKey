package com.kaadas.lock.activity.device.wifilock.password;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.WiFiLockCardAndFingerShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockNickNamePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockNickNameView;
import com.kaadas.lock.publiclibrary.bean.FacePassword;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockPasswordDetailActivity extends BaseActivity<IWifiLockNickNameView, WifiLockNickNamePresenter<IWifiLockNickNameView>>
        implements View.OnClickListener, IWifiLockNickNameView {

    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_editor)
    ImageView ivEditor;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.ll_password)
    LinearLayout llPassword;
    @BindView(R.id.iv_card_finger_icon)
    ImageView ivCardFingerIcon;
    @BindView(R.id.tv_card_finger_number)
    TextView tvCardFingerNumber;
    @BindView(R.id.ll_card_finger)
    LinearLayout llCardFinger;
    private long createTime;
    private int pwdType; //	密钥类型：1密码 2指纹密码 3卡片密码 4面容识别
    private String wifiSn;
    private WiFiLockCardAndFingerShowBean wiFiLockCardAndFingerShowBean;
    private ForeverPassword foreverPassword;
    private FacePassword facePassword;
    private String nickName;
    private int num;
    private String adminNickName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_password_detail);
        ButterKnife.bind(this);
        back.setOnClickListener(this);
        ivEditor.setOnClickListener(this);

        pwdType = getIntent().getIntExtra(KeyConstants.PASSWORD_TYPE, 1);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

        if (pwdType == 1) {
            llPassword.setVisibility(View.VISIBLE);
            llCardFinger.setVisibility(View.GONE);
            headTitle.setText(R.string.password_detail);
            foreverPassword = (ForeverPassword) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
            if(foreverPassword.getNickName() != null)
                nickName = foreverPassword.getNickName();
            createTime = foreverPassword.getCreateTime();
            num = Integer.parseInt(foreverPassword.getNum());
            initData();
        } else if (pwdType == 2 || pwdType == 3) {
            if (pwdType == 2) {
                ivCardFingerIcon.setImageResource(R.mipmap.fingerprint_icon);
                headTitle.setText(R.string.fingerprint_detail);
            }
            else if (pwdType == 3) {
                headTitle.setText(R.string.door_card_detail);
                ivCardFingerIcon.setImageResource(R.mipmap.door_card_icon);
            }

            wiFiLockCardAndFingerShowBean = (WiFiLockCardAndFingerShowBean) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
            llPassword.setVisibility(View.GONE);
            llCardFinger.setVisibility(View.VISIBLE);
            nickName = wiFiLockCardAndFingerShowBean.getNickName();
            createTime = wiFiLockCardAndFingerShowBean.getCreateTime();
            int num = wiFiLockCardAndFingerShowBean.getNum();
            String sNum = (num < 9 ? "0" + num : "" + num);
            tvCardFingerNumber.setText(sNum + " " + (TextUtils.isEmpty(nickName) ? num : nickName));
            this.num = wiFiLockCardAndFingerShowBean.getNum();
        }else if(pwdType == 4){
            headTitle.setText(R.string.face_password);
            ivCardFingerIcon.setImageResource(R.mipmap.face_password);
            llPassword.setVisibility(View.GONE);
            llCardFinger.setVisibility(View.VISIBLE);

            facePassword = (FacePassword) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
            nickName = facePassword.getNickName();
            createTime = facePassword.getCreateTime();
            num = Integer.parseInt(facePassword.getNum());
            String sNum = (num < 9 ? "0" + num : "" + num);
            tvCardFingerNumber.setText(sNum + " " + (TextUtils.isEmpty(nickName) ? num : nickName));

        }

        if (createTime == 0) {
            createTime = System.currentTimeMillis() / 1000;
        }

        tvTime.setText(DateUtils.secondToDate(createTime));
        tvName.setText(nickName != null ? nickName : "");


        adminNickName = (String) SPUtils.get(SPUtils.USERNAME, "");
        if (TextUtils.isEmpty(adminNickName)) {
            adminNickName = (String) SPUtils.get(SPUtils.PHONEN, "");
        }
    }

    private void initData() {
        tvNumber.setText(getString(R.string.password_yong_jiu_valid));
        switch (foreverPassword.getType()) {
            case 0:
                tvNumber.setText(getString(R.string.password_yong_jiu_valid));
                break;
            case 1:
                tvNumber.setText("策略密码");
                break;
            case 2:
                tvNumber.setText("胁迫密码");
                break;
            case 3:
                tvNumber.setText("管理员密码");
                break;
            case 4:
                tvNumber.setText("无权限密码");
                break;
            case 254:
                tvNumber.setText("密码一次性有效");
                break;
            case 255:
                tvNumber.setText("无效值密码");
                break;

        }

    }

    @Override
    protected WifiLockNickNamePresenter<IWifiLockNickNameView> createPresent() {
        return new WifiLockNickNamePresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_editor:
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                if (nickName != null) {
                    editText.setText(nickName);
                    editText.setSelection(nickName.length());
                }
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                if (pwdType == 1) {
                    tvTitle.setText(getString(R.string.please_input_password_name));
                } else if (pwdType == 2) {
                    tvTitle.setText(getString(R.string.input_fingerprint_name));
                } else if (pwdType == 3) {
                    tvTitle.setText(getString(R.string.input_door_card_name));
                }else if (pwdType == 4) {
                    tvTitle.setText(getString(R.string.input_door_face_name));
                }

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        if (StringUtil.judgeNicknameWhetherSame(nickName, name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_not_modify);
                            alertDialog.dismiss();
                            return;
                        }
                        alertDialog.dismiss();
                        mPresenter.updateNickName(wifiSn, pwdType, num, name, adminNickName);
                        showLoading(getString(R.string.is_modifing));
                    }
                });
                break;
        }
    }


    @Override
    public void onUpdateNickSuccess() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.modify_success);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onUpdateNickFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.modify_failed);
    }

    @Override
    public void onUpdateNickFailedServer(BaseResult result) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.modify_failed);
    }
}
