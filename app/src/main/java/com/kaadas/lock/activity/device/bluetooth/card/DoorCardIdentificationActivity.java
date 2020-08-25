package com.kaadas.lock.activity.device.bluetooth.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.ble.AddCardEndPresenter;
import com.kaadas.lock.mvp.view.IAddCardEndView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/17
 */
public class DoorCardIdentificationActivity extends BaseBleActivity<IAddCardEndView, AddCardEndPresenter<IAddCardEndView>>
        implements View.OnClickListener, IAddCardEndView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    private BleLockInfo bleLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_identification);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.add_door_card);
        if (mPresenter.isAuth(bleLockInfo, true)) {
            mPresenter.getCardNumberFromLock();
        }
    }

    @Override
    protected AddCardEndPresenter<IAddCardEndView> createPresent() {
        return new AddCardEndPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onPwdFull() {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint), getString(R.string.card_full_and_delete_exist_code), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(String toString) {
            }
        });
    }

    @Override
    public void onSetCardFailed(Throwable throwable) {
        /**
         * 0x01	失败
         * 0x85	某个字段错误
         * 0x87	密钥编号已存在(Set)
         * 0x8A	密钥已存在(Set)
         * 0x8B	密钥编号不存在(Clear)，不存在或错误（Check）
         * 0x93	无权限(Check，已锁定)
         * 0x94	超时
         * 0x9A	命令正在执行（TSN重复）
         * 0xC2	校验错误
         * 0xFF	锁接收到命令，但无结果返回
         */
        //默认为设置失败
        String errorCause = getString(R.string.set_door_card_failed);
        if (throwable instanceof BleProtocolFailedException) {
            BleProtocolFailedException protocolFailedException = (BleProtocolFailedException) throwable;
            if (protocolFailedException.getErrorCode() == 0x87) {  //密码编号已存在
                errorCause = getString(R.string.finger_door_card_success);
            }
        } else if (throwable instanceof TimeoutException) {
            errorCause = getString(R.string.set_failed_tineout);
        }
        ToastUtil.getInstance().showLong(errorCause);
        Intent intent = new Intent(this, DoorCardNoConnectOneActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSetCardSuccess(int userNumber) {

    }

    @Override
    public void onUploadPasswordNickSuccess(String nickName, String number, String Password) {
        Intent intent = new Intent(this, AddDoorCardSuccessActivity.class);
        intent.putExtra(KeyConstants.USER_NUM, Integer.parseInt(number));
        startActivity(intent);
        finish();
    }

    @Override
    public void onUploadPasswordNickFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(this,DoorCardManagerActivity.class));
        finish();
    }

    @Override
    public void onUploadPasswordNickFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(this,DoorCardManagerActivity.class));
        finish();
    }

    @Override
    public void syncNumberFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_card_failed_number);
        startActivity(new Intent(this,DoorCardManagerActivity.class));
        finish();
    }
}
