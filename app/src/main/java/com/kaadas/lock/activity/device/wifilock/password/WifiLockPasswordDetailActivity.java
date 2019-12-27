package com.kaadas.lock.activity.device.wifilock.password;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
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
    private String[] weekdays;
    private int pwdType; //	密钥类型：1密码 2指纹密码 3卡片密码
    private String wifiSn;
    private WiFiLockCardAndFingerShowBean wiFiLockCardAndFingerShowBean;
    private ForeverPassword foreverPassword;
    private String nickName;
    private int num;

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
            nickName = foreverPassword.getNickName();
            createTime = foreverPassword.getCreateTime();
            num = Integer.parseInt(foreverPassword.getNum());
            initData();
        } else if (pwdType == 2 || pwdType == 3) {
            if (pwdType == 2) {
                ivCardFingerIcon.setImageResource(R.mipmap.fingerprint_icon);
                headTitle.setText(R.string.fingerprint_detail);
            } else {
                headTitle.setText(R.string.door_card_detail);
                ivCardFingerIcon.setImageResource(R.mipmap.door_card_icon);
            }
            wiFiLockCardAndFingerShowBean = (WiFiLockCardAndFingerShowBean) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
            llPassword.setVisibility(View.GONE);
            llCardFinger.setVisibility(View.VISIBLE);
            nickName = wiFiLockCardAndFingerShowBean.getNickName();
            createTime = wiFiLockCardAndFingerShowBean.getCreateTime();
            int num = wiFiLockCardAndFingerShowBean.getNum();
            tvCardFingerNumber.setText(num < 9 ? "0" + num : "" + num);
            this.num = wiFiLockCardAndFingerShowBean.getNum();
        }

        if (createTime == 0) {
            createTime = System.currentTimeMillis() / 1000;
        }
        weekdays = new String[]{getString(R.string.week_day),
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wedensday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday)};
        tvTime.setText(DateUtils.secondToDate(createTime));
        tvName.setText(nickName != null ? nickName : "");
    }

    private void initData() {
        String weeks = "";
        if (foreverPassword.getType() == 1) { //永久密码
            tvNumber.setText(getString(R.string.password_yong_jiu_valid));
        } else {
            // 2时间段 3周期 4 24小时 5 一次性密码
            if (foreverPassword.getType() == 2) {  //时效密码
//                密码有效时效  2018/12/12  10：22~2018/12/24 10:22
                String startTime = DateUtils.formatDetailTime(foreverPassword.getStartTime());
                String endTime = DateUtils.formatDetailTime(foreverPassword.getEndTime());
                String content = getString(R.string.password_valid_shi_xiao) + "  " + startTime + "~" + endTime;
                tvNumber.setText(content);
            } else if (foreverPassword.getType() == 4) { //24小时
                tvNumber.setText(getString(R.string.password_one_day_valid));
            } else if (foreverPassword.getType() == 3) {  //周期密码
                for (int i = 0; i < foreverPassword.getItems().size(); i++) {
                    if ("1".equals(foreverPassword.getItems().get(i))) {
                        weeks += " " + weekdays[i];
                    }
                }
                String strHint = String.format(getString(R.string.week_hint), weeks,
                        DateUtils.long2HourMin(foreverPassword.getStartTime()), DateUtils.long2HourMin(foreverPassword.getEndTime()));
                tvNumber.setText(strHint);
            } else if (foreverPassword.getType() == 5) {
                tvNumber.setText(R.string.temporary_password_used_once);
            }
            if ("09".equals(foreverPassword.getNum())) {
                tvNumber.setText(R.string.stress_password);
            }
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
                        mPresenter.updateNickName(wifiSn, pwdType, num, name);
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
