package com.kaadas.lock.mvp.mvpbase;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public interface IBaseView {
    void showLoading(String content);
    void showLoadingNoCancel(String content);

    void hiddenLoading();
}
