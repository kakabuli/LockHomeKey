package com.kaadas.lock.publiclibrary.http.temp;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Create By lxj  on 2019/1/8
 * Describe
 */
@Deprecated
public interface IUserService {


    /**
     * 通过手机号登陆
     * @return
     */
    @POST(HttpConstants.PHONE_LOGIN)
    Observable<BaseResponse<String>> LoginByPhone(@Body RequestBody info);

    /**
     * 通过邮箱登陆
     * @return
     */
    @POST(HttpConstants.EMAIL_LOGIN)
    Observable<BaseResponse<String>> LoginByEmail(@Body RequestBody info);


    /**
     * 通过手机号注册
     *
     * @return
     */
    @POST(HttpConstants.PHONE_REGISTER)
    Observable<BaseResponse<String>> registerByPhone(@Body RequestBody info);

    /**
     * 通过邮箱注册
     *
     * @return
     */
    @POST(HttpConstants.EMAIL_REGISTER)
    Observable<BaseResponse<String>> registerByEmail(@Body RequestBody info);

    /**
     *发送邮箱验证码
     * @return
     */
    @POST(HttpConstants.GET_EMAIN_YZM)
    Observable<BaseResponse<String>> getRandomByEmail(@Body RequestBody info);



    /**
     *发送手机验证码
     * @return
     */
    @POST(HttpConstants.SEND_INTERNATIONAL_MSG)
    Observable<BaseResponse<String>> getRandomByPhone(@Body RequestBody info);




    /**
     *重置密码
     * @return
     */
    @POST(HttpConstants.EMAIL_FORGET_PWD)
    Observable<BaseResponse<String>> resetPassword(@Body RequestBody info);
    /**
     *重登陆
     * @return
     */
    @POST(HttpConstants.RE_LOGIN)
    Observable<BaseResponse<String>> agaginLoginIn(@Body RequestBody info);
    /**
     *修改用户昵称
     * @return
     */
    @POST(HttpConstants.CHANGE_NICK_NAME)
            Observable<BaseResponse<String>> modifyUserNickname(@Body RequestBody info);
    /**
     *修改原始密码
     * @return
     */
    @POST(HttpConstants.CHANGE_PWD)
            Observable<BaseResponse<String>> changeOriginalPassword(@Body RequestBody info);
    /**
     *获取用户昵称
     * @return
     */
    @POST(HttpConstants.GET_NICKNAME)
            Observable<BaseResponse<String>> getUserNickname(@Body RequestBody info);
    /**
     *修改昵称
     * @return
     */
    @POST(HttpConstants.CHANGE_NICKNAME)
            Observable<BaseResponse<String>> changeNickname(@Body RequestBody info);
    /**
     *用户反馈
     * @return
     */
    @POST(HttpConstants.USER_FEEDBACK)
            Observable<BaseResponse<String>> userFeedBack(@Body RequestBody info);
    /**
     *上传头像
     * @return
     */
    @Multipart
    @POST(HttpConstants.UP_LOAD_USERHEAD)
    Observable<BaseResponse<String>> getPicturesBean(@Part("uid") RequestBody uid, @Part MultipartBody.Part file);
    /**
     *下载头像
     * @return
     */
    @GET(HttpConstants.DOWN_LOAD_USERHEAD+"{uid}")
    @Streaming
    Observable<ResponseBody> downloadUserHead(@Path("uid") String uid);


}
