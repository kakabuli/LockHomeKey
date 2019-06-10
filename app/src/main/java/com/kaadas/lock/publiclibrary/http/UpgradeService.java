package com.kaadas.lock.publiclibrary.http;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Create By denganzhi  on 2019/6/10
 * Describe
 */

public interface UpgradeService {


    @GET("app.json")
    Call<String> getUpdateJson();
}
