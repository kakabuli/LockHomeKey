package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;

/**
 * Create By lxj  on 2019/3/8
 * Describe
 */
public interface ILanguageSetView extends IBleView {

    void onGetLanguageSuccess(String lang);

    void onGetLanguageFailed(Throwable throwable);

    void onSetLangSuccess(String lang);

    void onSetLangFailed(Throwable throwable);

}
