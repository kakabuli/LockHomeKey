package com.kaadas.lock.bean;

import com.kaadas.lock.publiclibrary.http.UpgradeService;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Create By denganzhi  on 2019/6/10
 * Describe
 */

public class UpgradePresenter {
    Retrofit retrofit=null;

    UpgradeService upgradeService;

    public UpgradePresenter() {

        retrofit = new Retrofit.Builder().baseUrl("http://s.kaadas.com:8989/cfg/SoftMgr/app.json")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
//        retrofit = new Retrofit.Builder().baseUrl("http://192.168.168.157:8080/AndroidTest/")
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
        upgradeService = retrofit.create(UpgradeService.class);
    }




}
