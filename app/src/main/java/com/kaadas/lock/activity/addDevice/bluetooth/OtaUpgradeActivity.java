package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.widget.CircleProgress;
import com.kaadas.lock.widget.MutiProgress;
import com.kaadas.lock.widget.OtaMutiProgress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtaUpgradeActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.circle_progress_bar2)
    CircleProgress mCircleProgress2;
    @BindView(R.id.mutiprogree_ota)
    OtaMutiProgress mutiprogree;
    @BindView(R.id.start_upgrade)
    Button start_upgrade;
    int j=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_upgrade);
        ButterKnife.bind(this);
        tv_content.setText(getResources().getString(R.string.ota_lock_upgrade));
        iv_back.setOnClickListener(this);
        mCircleProgress2.setOnClickListener(this);
        start_upgrade.setOnClickListener(this);
        mutiprogree.setCurrNodeNO(0, false);
    }
    int cicleProgress=0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.iv_back:
                finish();
                break;
            case R.id.circle_progress_bar2:
                cicleProgress=cicleProgress+10;
                if(cicleProgress>100){
                    cicleProgress=100;
                }
                mCircleProgress2.setValue(cicleProgress);
                break;
            case  R.id.start_upgrade:
             if(j==1){
                 j=0;
                 AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
                     @Override
                     public void left() {

                     }

                     @Override
                     public void right() {

                     }
                 });
             }else{
                 j=1;
                 AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.ota_fail), getString(R.string.ota_fail_reply),
                         getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                             @Override
                             public void left() {

                             }

                             @Override
                             public void right() {

                             }
                         });
             }


                break;
        }
    }
}
