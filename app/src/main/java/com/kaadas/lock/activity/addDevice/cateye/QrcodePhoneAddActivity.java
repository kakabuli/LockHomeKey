package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddCateyeHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddCatEyePresenter;
import com.kaadas.lock.mvp.presenter.qrcodePhoneAddPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.utils.KeyConstants;
import com.king.zxing.util.CodeUtils;



import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class  QrcodePhoneAddActivity extends BaseActivity<IAddCatEyeView, qrcodePhoneAddPresenter<IAddCatEyeView>> implements IAddCatEyeView {

    @BindView(R.id.qrcode_img)
    ImageView qrcode_img;
    @BindView(R.id.help)
    ImageView help;

    private String pwd;
    private String ssid;
    private String gwId;
    String qrcontext=null;
    private String gatewayId; //猫眼添加成功之后
    private String deviceId;  //猫眼添加成功之后的网关id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_phone_add);

        ButterKnife.bind(this);

        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);



        qrcontext= "ssid:"+"\""+ssid+"\""+" "+"password:"+"\""+pwd+"\"";
        Log.e("denganzhi1"," gwid:"+gwId+" ssid:"+ssid+" pwd:"+pwd);
        Log.e("denganzhi1","code:"+qrcontext);
        createQRCode(qrcontext);

//        timer= new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                System.out.println("-------延迟5000毫秒，每1000毫秒执行一次--------");
//            }
//        }, 1000*120);
        mPresenter.startJoin(null, null, gwId, ssid, pwd);
    }

    @OnClick({R.id.back, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent helpIntent=new Intent(this, DeviceAddCateyeHelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }

    /**
     * 生成二维码
     * @param content
     */
    private void createQRCode(String content){
        //生成二维码最好放子线程生成防止阻塞UI，这里只是演示
     //   Bitmap logo = BitmapFactory.decodeResource(getResources(),R.drawable.logo);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）

        Bitmap bitmap =  CodeUtils.createQRCode(content,width-180,null);
        //显示二维码
        qrcode_img.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected qrcodePhoneAddPresenter<IAddCatEyeView> createPresent() {
        return new qrcodePhoneAddPresenter();
    }

    @Override
    public void joinTimeout() {
      //  pairCatEyeResult(false);
    }

    @Override
    public void cateEyeJoinSuccess(DeviceOnLineBean deviceOnLineBean) {
        gatewayId=deviceOnLineBean.getGwId();
        deviceId=deviceOnLineBean.getDeviceId();
        pairCatEyeResult(true);
    }

    @Override
    public void catEysJoinFailed(Throwable throwable) {
        pairCatEyeResult(false);
    }

    //猫眼配置结果
    private void pairCatEyeResult(Boolean flag) {

        Log.e("denganzhi1","QrcodePhoneAddAcrivity....flag:"+flag+ " gwid:"+gatewayId + " device:"+deviceId);

        if (flag) {
            if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                Intent successIntent = new Intent(this, AddDeviceCatEyeSuccessActivity.class);
                successIntent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                successIntent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                startActivity(successIntent);
                finish();
            }
        } else {
            Intent failIntent = new Intent(this, AddDeviceCatEyeFailActivity.class);
            startActivity(failIntent);
            finish();
        }
    }
}
