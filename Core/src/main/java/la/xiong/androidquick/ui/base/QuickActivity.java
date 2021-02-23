/**
 * @Description: Activity的一个基类, 提供丰富的功能, 旨在开发一款新APP应用的时候直接继承使用
 * @Detail:
 * 1.   默认提供六种转场动画, 如需自定义, 请在子类调用父类的onCreate后添加overridePendingTransition
 * 2.   封装页面跳转传参
 * 3.   EventBus事件总线
 * 4.   管理所有启动的activity
 * 5.   设备屏幕信息
 * 6.   监听网络状态变化
 * 7.   支持butterknife 8+, databinding
 * 8.   页面跳转: readyGo, readyGoThenKill, readyGoForResult
 * 9.   提供页面状态展示: loading, network error, error, empty
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */

package la.xiong.androidquick.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import la.xiong.androidquick.R;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import la.xiong.androidquick.ui.manager.HttpSubRequest;
import la.xiong.androidquick.ui.manager.QuickAppManager;
import la.xiong.androidquick.ui.permission.EasyPermissions;
import la.xiong.androidquick.ui.receiver.NetStateReceiver;
import la.xiong.androidquick.ui.viewstatus.VaryViewHelperController;
import spa.lyh.cn.statusbarlightmode.ImmersionConfiguration;
import spa.lyh.cn.statusbarlightmode.ImmersionMode;

/**
 * Created by yanliang
 * on 2019/3/14
 */
public abstract class QuickActivity extends AppCompatActivity implements EasyPermissions.PermissionWithDialogCallbacks{

    /**
     * log tag
     */
    protected static String TAG = "QuickActivity";

    /**
     * context
     */
    protected Context mContext = null;

    /**
     * screen information
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * loading view controller
     */
    private VaryViewHelperController mVaryViewHelperController = null;

    /**
     * overridePendingTransition mode
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    /**
     * butterknife 8+ support
     */
    private Unbinder mUnbinder;
    /**
     * system bar color tint
     */
    public ImmersionMode immersionMode;
    private boolean flag;

    /**
     * default toolbar
     */
    protected TextView tvTitle;
    protected TextView tvRight;
    protected ImageView imgRight;
    protected FrameLayout toolbarLayout;
    private LinearLayout contentView;
    public LinearLayout linback;
    protected HttpSubRequest httpSubRequest;
    private View defalutLoadView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isApplySystemBarTint()) {
            immersionMode = ImmersionMode.getInstance();
            flag = immersionMode.execImmersionMode(this);
        }
        mContext = this;
        httpSubRequest=new HttpSubRequest();
        // activity manager
        QuickAppManager.getInstance().addActivity(this);
        // animation
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }

        EventBus.getDefault().register(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        int layoutId = getContentViewLayoutID();
        if (layoutId != 0) {
            try {
                setContentView(layoutId);
            } catch (NoClassDefFoundError e) {
                setContentView(layoutId);
            }
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        // extras
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        initViewsAndEvents();
        //NetStateReceiver.registerNetworkStateReceiver(mContext);
    }

    @Override
    public void setContentView(int layoutResID) {
        // 添加toolbar布局
        if (isLoadDefaultTitleBar()) {
            initToolBarView();
            initContentView();
            contentView.addView(toolbarLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            View view = getContentView(layoutResID, contentView);
            if (view == null) {
                LayoutInflater.from(this).inflate(layoutResID, contentView, true);

                super.setContentView(contentView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                super.setContentView(view, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            super.setContentView(layoutResID);
        }
        mUnbinder = ButterKnife.bind(this);
        if(isLoadDefaultTitleBar()){
            defalutLoadView=  contentView.getChildAt(1);
        }else{
            View rootView = findViewById(android.R.id.content);
            defalutLoadView=rootView;

        }

//        mVaryViewHelperController = new VaryViewHelperController(defalutLoadView);
        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
    }

    protected abstract boolean isLoadDefaultTitleBar();

    protected View getContentView(int layoutResID, LinearLayout contentView) {
        return null;
    }

    protected void initContentView() {
        contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);
    }

    private void initToolBarView() {
        toolbarLayout = new FrameLayout(this);
        LayoutInflater.from(this).inflate(R.layout.layout_toolbar, toolbarLayout,
                true);
        tvTitle = (TextView) toolbarLayout.findViewById(R.id.tv_title);
        tvRight = (TextView) toolbarLayout.findViewById(R.id.tv_right);
        linback=(LinearLayout)toolbarLayout.findViewById(R.id.lin_back);
        imgRight = toolbarLayout.findViewById(R.id.img_right);
        linback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("测试","点击返回");
                finish();
                onFinish();
            }
        });
    }

//    protected void leftClick(View view){
//        linback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view.getVisibility() == View.VISIBLE)
//                    finish();
//            }
//        });
//    }

    @Override
    public void finish() {
        super.finish();
        QuickAppManager.getInstance().removeActivity(this);
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
    }

    public void onFinish(){

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if(httpSubRequest!=null){
            httpSubRequest.clear();
        }
        NetStateReceiver.unRegisterNetworkStateReceiver(mContext);
        EventBus.getDefault().unregister(this);
    }

    /**
     * get bundle data
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * when event coming
     *
     * @param eventCenter
     */
    protected abstract void onEventComing(EventCenter eventCenter);

    /**
     * get loading target view
     */
    protected abstract View getLoadingTargetView();

    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents();

    /**
     * toggle overridePendingTransition
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();

    /**
     * get the overridePendingTransition mode
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();

    /**
     * apply systembar color tint
     */
    protected abstract boolean isApplySystemBarTint();

    /**
     * 会被子类覆盖去封装 跳转到不同的activity 或者fragment的页面
     *
     * @param clazz
     * @return
     */
    protected Intent getGoIntent(Class<?> clazz) {
        return new Intent(this, clazz);
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = getGoIntent(clazz);
        startActivity(intent);

    }

    /**
     * startActivity with  flag
     * @param clazz
     * @param flag
     */
    protected void readyGo(Class<?> clazz, int flag) {
        Intent intent = getGoIntent(clazz);
        intent.setFlags(flag);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = getGoIntent(clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);

    }

    /**
     * startActivity with bundle and flag
     * @param clazz
     * @param bundle
     * @param flag
     */
    protected void readyGo(Class<?> clazz, Bundle bundle, int flag) {
        Intent intent = getGoIntent(clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        intent.setFlags(flag);
        startActivity(intent);
    }


    /**
     * startActivity then finish
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = getGoIntent(clazz);
        startActivity(intent);
        finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = getGoIntent(clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = getGoIntent(clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * toggle show loading
     *
     * @param toggle
     */
    protected void toggleShowLoading(boolean toggle, String msg) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading(msg,mContext);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show error
     *
     * @param toggle
     */
    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showError(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }
    protected void toggleShowEmptyImage(boolean toggle, @DrawableRes int id, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            return;
        }

        if (toggle) {
            mVaryViewHelperController.showEmptyImage(id, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show network error
     *
     * @param toggle
     */
    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showNetworkError(onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    protected void toggleRestore() {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.restore();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventCenter eventCenter) {
        if (null != eventCenter) {
            onEventComing(eventCenter);
        }
    }

    protected void changeStatusBarColor(int ResId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ImmersionConfiguration tConfig = new ImmersionConfiguration.Builder(this)
                    .setColor(ResId)
                    .build();
            immersionMode.setTemporaryConfig(tConfig);
            immersionMode.execImmersionMode(this);
        }
    }

    /**
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * Android6.0权限控制
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     */
    private Map<Integer, PermissionCallback> mPermissonCallbacks = null;
    private Map<Integer, String[]> mPermissions = null;

    public interface PermissionCallback {
        /**
         * has all permission
         *
         * @param allPerms all permissions
         */
        void hasPermission(List<String> allPerms);

        /**
         * denied some permission
         *
         * @param deniedPerms          denied permission
         * @param grantedPerms         granted permission
         * @param hasPermanentlyDenied has permission denied permanently
         */
        void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied);

        /**
         * @param dialogType dialogType
         * @param callback   callback from easypermissions
         */
        void showDialog(int dialogType, EasyPermissions.DialogCallback callback);
    }

    /**
     * request permission
     *
     * @param dialogType  dialogType
     * @param requestCode requestCode
     * @param perms       permissions
     * @param callback    callback
     */
    public void performCodeWithPermission(int dialogType,
                                          final int requestCode, @NonNull String[] perms, @NonNull PermissionCallback callback) {
        if (EasyPermissions.hasPermissions(this, perms)) {
            callback.hasPermission(Arrays.asList(perms));
        } else {
            if (mPermissonCallbacks == null) {
                mPermissonCallbacks = new HashMap<>();
            }
            mPermissonCallbacks.put(requestCode, callback);

            if (mPermissions == null) {
                mPermissions = new HashMap<>();
            }
            mPermissions.put(requestCode, perms);

            EasyPermissions.requestPermissions(this, dialogType, requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if (mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        // 100% granted permissions
        if (mPermissions.get(requestCode).length == perms.size()) {
            mPermissonCallbacks.get(requestCode).hasPermission(Arrays.asList(mPermissions.get(requestCode)));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        if (mPermissions == null || !mPermissions.containsKey(requestCode)) {
            return;
        }

        //granted permission
        List<String> grantedPerms = new ArrayList<>();
        for (String perm : mPermissions.get(requestCode)) {
            if (!perms.contains(perm)) {
                grantedPerms.add(perm);
            }
        }

        //check has permission denied permanently
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, true);
        } else {
            mPermissonCallbacks.get(requestCode).noPermission(perms, grantedPerms, false);
        }
    }

    @Override
    public void onDialog(int requestCode, int dialogType, EasyPermissions.DialogCallback callback) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks.containsKey(requestCode)) {
            return;
        }
        mPermissonCallbacks.get(requestCode).showDialog(dialogType, callback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.e("测试","返回按钮");
        onFinish();
    }
}
