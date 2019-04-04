package com.kaadas.lock.base.mvpbase;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaadas.lock.utils.LoadingDialog;


/**
 * Create By lxj  on 2019/2/15
 * Describe
 */
public abstract class BaseFragment<T extends IBaseView, V
        extends BasePresenter<T>> extends Fragment implements IBaseView {

    protected V mPresenter;
    private LoadingDialog loadingDialog;
    private Handler bHandler = new Handler();



    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresent();
        mPresenter.attachView((T) this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /**
     * 切换到了当前fragment
     */
    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * 切换到了当前fragment  且显示出来
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 子类实现具体的构建过程
     *
     * @return
     */
    protected abstract V createPresent();

    @Override
    public void showLoading(String Content) {
        loadingDialog = LoadingDialog.getInstance(getContext());
        if (!getActivity().isFinishing()) {
            loadingDialog.show(Content);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }


    @Override
    public void hiddenLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }





}
