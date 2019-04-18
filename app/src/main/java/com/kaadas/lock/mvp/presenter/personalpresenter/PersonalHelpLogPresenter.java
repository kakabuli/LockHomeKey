package com.kaadas.lock.mvp.presenter.personalpresenter;



import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetHelpLogResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.personalview.IPersonalHelpLogView;

import io.reactivex.disposables.Disposable;

public class PersonalHelpLogPresenter<T> extends BasePresenter<IPersonalHelpLogView> {

    //获取帮助日志
    public void  getHelpLog(String uid,int page){

        XiaokaiNewServiceImp.getHelpLog(uid,page).subscribe(new BaseObserver<GetHelpLogResult>() {
            @Override
            public void onSuccess(GetHelpLogResult getHelpLogResult) {
                if ("200".equals(getHelpLogResult.getCode())){
                    if (mViewRef.get()!=null){
                        mViewRef.get().getHelpLogSuccess(getHelpLogResult);
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getHelpLogFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get()!=null){
                    mViewRef.get().getHelpLogError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });


    }

}
