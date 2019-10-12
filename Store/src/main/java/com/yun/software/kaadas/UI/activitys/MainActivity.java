package com.yun.software.kaadas.UI.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaeger.library.StatusBarUtil;
import com.yun.software.kaadas.Comment.Constans;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.TabEntity;
import com.yun.software.kaadas.UI.fragment.DeviceFragment;
import com.yun.software.kaadas.UI.fragment.HomeFragment;
import com.yun.software.kaadas.UI.fragment.MineFragment;
import com.yun.software.kaadas.UI.fragment.ShopFragment;
import com.yun.software.kaadas.Utils.FragmentUtils;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class MainActivity extends BaseActivity {

    private Context context = MainActivity.this;
    @BindView(R2.id.Container)
    FrameLayout layout_content;

    @BindView(R2.id.CommonTab)
    CommonTabLayout CommonTab;

    private ShopFragment shopFragment;
    private DeviceFragment deviceFragment;
    private HomeFragment homeFragment;
    private MineFragment mineFragment;


    private Fragment mCurrentFragment;
    private int selectIndex=0;
    private String[] mTitles = {"首页", "设备", "商城","我"};
    private int[] mIconUnselectIds = {
            R.drawable.icon_home_nor, R.drawable.icon_find_nor, R.drawable.icon_k_nor, R.drawable.icon_mine_nor};
    private int[] mIconSelectIds = {
            R.drawable.icon_home_sel, R.drawable.icon_find_sel, R.drawable.icon_k_sel, R.drawable.icon_mine_sel};
    private FragmentManager fragmentManager;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    public static boolean isForeground = false;




    @Override
    protected boolean isApplySystemBarTint() {
        return false;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }


    //保证闪退后,fragment不重叠
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }



    private void setCostomMsg(String msg){
        ToastUtil.showShort(msg);
    }

    @Override
    protected void initViewsAndEvents() {

        StatusBarUtil.setLightMode(this);

        initDate();



    }







    private void initDate(){
        //弹出输入况
        // DialogManager.getInstance().showCommentDialog(getSupportFragmentManager(),this);
        fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        CommonTab.setTabData(mTabEntities);
        //点击监听
        CommonTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        CommonTab.setCurrentTab(selectIndex);
        SwitchTo(selectIndex);
    }





    /**
     * 切换
     */
    private void SwitchTo(int position) {
        switch (position) {

            //首页
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }

                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, homeFragment, layout_content.getId(), position, false);
                break;
            //设备
            case 1:
                if (deviceFragment == null) {
                    deviceFragment = new DeviceFragment();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, deviceFragment, layout_content.getId(), position, false);
                break;
            //商城
            case 2:


                if (shopFragment == null) {
                    shopFragment = new ShopFragment();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, shopFragment, layout_content.getId(), position, false);
                break;

                //我
            case 3:

                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mineFragment, layout_content.getId(), position, false);
                break;
            default:
                break;
        }

        selectIndex = position;

    }

    private long clickBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (System.currentTimeMillis() - clickBackTime <= 3000) {
//                LoginOutUtils.newIntance().finish();
                moveTaskToBack(true);
                finish();
                System.exit(0);

            } else {
                ToastUtil.showShort("再按一次退出");
                clickBackTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @SuppressLint("NewApi")
    private boolean hasPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0x11:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){//同意授权

                }else {
                    ToastUtil.showShort("权限被拒绝了");
                }
                break;
        }
    }





    @Override
    protected void onEventComing(EventCenter eventCenter) {
//        super.onEventComing(eventCenter);
//        if (eventCenter.getEventCode() == Constans.RELOGIN){
//            readyGo(WxLoginActivity.class);
//            UserUtils.setToken("");
//        }else if (eventCenter.getEventCode() == Constans.MESSAGE_GOTO_HOME){
//            selectIndex = 0;
//            isSwitch = true;
//        }

    }

    private boolean isSwitch = false;

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();

        if (isSwitch){
            CommonTab.setCurrentTab(selectIndex);
            SwitchTo(selectIndex);
            isSwitch = false;
        }




    }



}
