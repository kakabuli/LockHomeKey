package com.kaadas.lock.mvp.mvpbase;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;


import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create By lxj  on 2019/1/7
 * Describe Activity 基类
 */
public abstract class BaseActivity<T extends IBaseView, V
        extends BasePresenter<T>> extends AppCompatActivity implements IBaseView {
    protected V mPresenter;
    private LoadingDialog loadingDialog;
    private Handler bHandler = new Handler();
    private Unbinder unbinder;

    private boolean isRegistered = false;
    private NetWorkChangReceiver netWorkChangReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresent();
        mPresenter.attachView((T) this);
        MyApplication.getInstance().addActivity(this);

        //注册网络状态监听广播
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);

    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        //销毁时，删除回调，防止内存泄漏
        bHandler.removeCallbacksAndMessages(null);
        MyApplication.getInstance().removeActivity(this);
        //解绑
        if (isRegistered) {
            unregisterReceiver(netWorkChangReceiver);
        }
        unbinder.unbind();
        super.onDestroy();
    }


    /**
     * 子类实现具体的构建过程
     *
     * @return
     */
    protected abstract V createPresent();



    @Override
    public void showLoading(String Content) {

        loadingDialog = LoadingDialog.getInstance(this);
        if (!isFinishing()) {
            loadingDialog.show(Content);
        }
    }

    @Override
    public void hiddenLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 点击输入框之外的地方  hidden软键盘
     *
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
    /**
     * 多种隐藏软件盘方法的其中一种
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
