package com.yun.software.kaadas.UI.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/24
 * @describe
 */

public class BaseMainFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = BaseMainFragmentAdapter.class.getSimpleName();
    private int  pagetotal;
    private FragmentTransaction mCurTransaction;
    private FragmentManager mFragmentManager;
    private List<Fragment> fragments;

    private boolean isDestroy = false;

    public BaseMainFragmentAdapter(FragmentManager fm, int pagetotal, List<Fragment> fragments) {
        super(fm);
        mFragmentManager = fm;
        this.fragments=fragments;
        this.pagetotal = pagetotal;
    }

    public BaseMainFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentManager = fm;
        this.fragments=fragments;
        this.pagetotal = Integer.MAX_VALUE;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    public void setDestroy(boolean destroy) {
        isDestroy = destroy;
    }

    @Override
    public Fragment getItem(int position) {
        return buildFragmentItem(position);
    }

    @Override
    public int getCount() {
        return pagetotal;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Fragment buildFragmentItem(int position) {
        if(position<fragments.size()){
            return fragments.get(position);
        }else{
            return new Fragment();
        }

    }

    @SuppressLint("CommitTransaction")
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (isDestroy) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.remove((Fragment) object);
        } else {
            super.destroyItem(container, position, object);
        }

    }
    //刷新fragment
    public void setFragments(List<Fragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft = null;
            mFragmentManager.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        if (isDestroy) {
            if (mCurTransaction != null) {
                mCurTransaction.commitNowAllowingStateLoss();
                mCurTransaction = null;
            }
        }
    }
}
