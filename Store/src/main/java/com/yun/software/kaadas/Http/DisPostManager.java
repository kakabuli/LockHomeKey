package com.yun.software.kaadas.Http;

import java.util.LinkedList;

import io.reactivex.disposables.Disposable;

/**
 * Created by yanliang
 * on 2019/4/3
 */
public class DisPostManager {

    private LinkedList<Disposable> mDisposables = null;
    private DisPostManager() {
        mDisposables = new LinkedList<>();
    }

    private static class SingletonInstance {
        private static final DisPostManager INSTANCE = new DisPostManager();
    }

    public void cancleAllDispose() {
        if (mDisposables.size() == 0)
            return;
        for (Disposable disposable : mDisposables) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }

        }
    }
    public void addDisoPost(Disposable disposable){
        mDisposables.add(disposable);
    }
    public static DisPostManager getInstance() {

        return SingletonInstance.INSTANCE;
    }
}
