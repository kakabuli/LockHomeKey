package com.kaadas.lock.mvp.presenter;

import android.util.Log;

import com.kaadas.lock.publiclibrary.http.UpgradeService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Create By denganzhi  on 2019/6/10
 * Describe
 */

public class UpgradePresenter {
    Retrofit retrofit=null;

    UpgradeService upgradeService;

    public interface IUpgradePresenter {

        void  ShowUpgradePresenterSuccess(String jsonPresenterResult);
        void  ShowUpgradePresenterFail();

    }





    public UpgradePresenter() {

        retrofit = new Retrofit.Builder().baseUrl("http://s.kaadas.com:8989/cfg/SoftMgr/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
//        retrofit = new Retrofit.Builder().baseUrl("http://192.168.168.157:8080/AndroidTest/")
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
        upgradeService = retrofit.create(UpgradeService.class);
    }


    public void getUpgreadJson( IUpgradePresenter upgradePresenter){
        Call<String> callLogin=upgradeService.getUpdateJson();
        callLogin.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result= response.body();
                if(upgradePresenter!=null){
                    upgradePresenter.ShowUpgradePresenterSuccess(result);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("denganzhi1",t.toString());
                if(upgradePresenter!=null){
                    upgradePresenter.ShowUpgradePresenterFail();
                }
            }
        });
    }





}
