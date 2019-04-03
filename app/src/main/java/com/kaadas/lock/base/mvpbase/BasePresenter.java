package com.kaadas.lock.base.mvpbase;

import android.os.Handler;
import android.os.Looper;



import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/1/7
 * Describe MVP  中的BasePresenter
 */
public class BasePresenter<T extends IBaseView> {
    public Handler handler = new Handler(Looper.getMainLooper());
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * 弱引用
     * T Activity 中UI操作接口
     */
    protected WeakReference<T> mViewRef;
    protected boolean isAttach = false;

    public void attachView(T view) {
        mViewRef = new WeakReference<T>(view);
        isAttach = true;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        isAttach = false;
    }

    public void toDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


}
