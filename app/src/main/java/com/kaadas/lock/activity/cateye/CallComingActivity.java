package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneAutoAccept;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.RingTools;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;

import org.linphone.core.LinphoneCall;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.GET;

public class CallComingActivity extends BaseAddToApplicationActivity implements View.OnClickListener {

    @BindView(R.id.call_coming_refuse_ll)
    LinearLayout call_coming_refuse_ll;


    @BindView(R.id.call_coming_title)
    TextView callComingTitle;
    @BindView(R.id.call_coming_img)
    ImageView callComingImg;
    @BindView(R.id.call_coming_center_view)
    View callComingCenterView;
    @BindView(R.id.tv_cat_device_power)
    ImageView tvCatDevicePower;
    @BindView(R.id.iv_accept_call)
    ImageView ivAcceptCall;
    private RingTools ringTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_coming);
        ButterKnife.bind(this);
        call_coming_refuse_ll.setOnClickListener(this);
        ivAcceptCall.setOnClickListener(this);
        SPUtils.remove(Constants.ALREADY_TOAST);
        MyLog.getInstance().save("CallComingActivity==>onCreate......:");
        Log.e(GeTui.VideoLog,"CallComingActivity==>onCreate......:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 当状态栏设置为透明的时候,View渲染到状态栏
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //   getWindow().setStatusBarColor(getResources().getColor(R.color.red));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ringTools = new RingTools(this);
        ringTools.startRinging();
         listenerCallStatus();


        //设置窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setScaleX((float) 1);
        win.getDecorView().setScaleY((float) 1);

        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
        //设置点击外部空白处可以关闭Activity
        this.setFinishOnTouchOutside(true);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.call_coming_refuse_ll:
                LinphoneHelper.addAutoAcceptCallBack(null);
                ringTools.stopRinging();
                intent = new Intent();
                intent.putExtra(KeyConstants.IS_ACCEPT_CALL, false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_accept_call:
                LinphoneHelper.addAutoAcceptCallBack(null);
                ringTools.stopRinging();
                intent = new Intent();
                intent.putExtra(KeyConstants.IS_ACCEPT_CALL, true);
                setResult(RESULT_OK, intent);
                MyLog.getInstance().save("CallComingActivity..接听");
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(Tag,"CallComing.........Ondestroy");
        ringTools.stopRinging();
        super.onDestroy();
    }


    private static String Tag = "来电界面";

    private void listenerCallStatus() {
        LinphoneHelper.addAutoAcceptCallBack(new PhoneAutoAccept() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                Log.e(Tag, "猫眼  incomingCall.........");

            }

            @Override
            public void callConnected() {
                Log.e(Tag, "猫眼  callConnected.........");

            }

            @Override
            public void callReleased() {
                Log.e(Tag, "猫眼  callReleased.........");
            }

            @Override
            public void callFinish() {
                Log.e(Tag, "猫眼 callFinish.........");
                if(ringTools!=null){
                    ringTools.stopRinging();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SPUtils.put(Constants.ALREADY_TOAST,true);
                        Toast.makeText(CallComingActivity.this,getString(R.string.call_coming_connection_fail),Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.IS_ACCEPT_CALL, false);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void Streaming() {
                Log.e(Tag, "猫眼 Streaming.........");
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

}
