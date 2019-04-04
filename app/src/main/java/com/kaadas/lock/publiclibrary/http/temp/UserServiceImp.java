package com.kaadas.lock.publiclibrary.http.temp;



import com.kaadas.lock.publiclibrary.http.temp.postbean.ChangeNicknameBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.ChangeOriginalPasswordBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.EmailLogin;
import com.kaadas.lock.publiclibrary.http.temp.postbean.GetRandomByEmail;
import com.kaadas.lock.publiclibrary.http.temp.postbean.GetRandomByPhone;
import com.kaadas.lock.publiclibrary.http.temp.postbean.GetUserNicknameBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.ModifyUserNicknameBean;
import com.kaadas.lock.publiclibrary.http.temp.postbean.PhoneNumberLogin;
import com.kaadas.lock.publiclibrary.http.temp.postbean.RegisterByEmail;
import com.kaadas.lock.publiclibrary.http.temp.postbean.RegisterByPhone;
import com.kaadas.lock.publiclibrary.http.temp.postbean.ResetPassword;
import com.kaadas.lock.publiclibrary.http.temp.postbean.UserFeedBackBean;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserServiceImp {


    public static void loginByPhone(String phone, String pwd, Observer observer) {
        PhoneNumberLogin phoneNumberLogin = new PhoneNumberLogin(phone, pwd);
        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .LoginByPhone(new HttpUtils<PhoneNumberLogin>().getBody(phoneNumberLogin))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    public static void loginByEmail(String email, String pwd, Observer observer) {
        EmailLogin phoneNumberLogin = new EmailLogin(email, pwd);
        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .LoginByEmail(new HttpUtils<EmailLogin>().getBody(phoneNumberLogin))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }


    public static void registerByPhone(String phone, String pwd, String token, Observer observer) {

        RegisterByPhone registerByPhone = new RegisterByPhone(phone, pwd, token);

        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .registerByPhone(new HttpUtils<RegisterByPhone>().getBody(registerByPhone))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }


    public static void registerByEmail(String email, String pwd, String token, Observer observer) {

        RegisterByEmail registerByEmail = new RegisterByEmail(email, pwd, token);

        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .registerByEmail(new HttpUtils<RegisterByEmail>().getBody(registerByEmail))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    public static void getRandomByEmail(String email, Observer observer) {
        GetRandomByEmail sendRandomByEmail = new GetRandomByEmail(email);
        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByEmail(new HttpUtils<GetRandomByEmail>().getBody(sendRandomByEmail))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    public static void getRandomByPhone(String phone, String code, Observer observer) {
        GetRandomByPhone getRandomByPhone = new GetRandomByPhone(phone, code);
        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByPhone(new HttpUtils<GetRandomByPhone>().getBody(getRandomByPhone))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }


    /**
     * @param user_name
     * @param pwd
     * @param token
     * @param type      账号类型  1手机  2邮箱
     * @param observer
     */
    public static void resetPassword(String user_name, String pwd, String token, int type, Observer observer) {

        ResetPassword resetPassword = new ResetPassword(user_name, pwd, type, token);

        RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .resetPassword(new HttpUtils<ResetPassword>().getBody(resetPassword))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }


    public static Observable getRandomByEmailTest(String email) {

        GetRandomByEmail sendRandomByEmail = new GetRandomByEmail(email);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByEmail(new HttpUtils<GetRandomByEmail>().getBody(sendRandomByEmail))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    public static Observable getRandomByPhoneTest(String phone, String code) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GetRandomByPhone getRandomByPhone = new GetRandomByPhone(phone, code);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByPhone(new HttpUtils<GetRandomByPhone>().getBody(getRandomByPhone))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 修改用户昵称
     */
    public static void modifyUserNickname(String uid, String nickname, Observer observer) {
        ModifyUserNicknameBean modifyUserNicknameBean = new ModifyUserNicknameBean(uid, nickname);
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .modifyUserNickname(new HttpUtils<ModifyUserNicknameBean>().getBody(modifyUserNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    /**
     * 修改原始密码
     */
    public static void changeOriginalPassword(String uid, String newpwd, String oldpwd, Observer observer) {
        ChangeOriginalPasswordBean changeOriginalPasswordBean = new ChangeOriginalPasswordBean(uid, newpwd, oldpwd);
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .changeOriginalPassword(new HttpUtils<ChangeOriginalPasswordBean>().getBody(changeOriginalPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    /**
     * 获取用户昵称
     */
    public static void getUserNickname(String uid, Observer observer) {
        GetUserNicknameBean getUserNicknameBean = new GetUserNicknameBean(uid);
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .getUserNickname(new HttpUtils<GetUserNicknameBean>().getBody(getUserNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    /**
     * 修改昵称
     */
    public static void changeNickname(String uid, String nickname, Observer observer) {
        ChangeNicknameBean changeNicknameBean = new ChangeNicknameBean(uid, nickname);
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .changeNickname(new HttpUtils<ChangeNicknameBean>().getBody(changeNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    /**
     * 用户反馈
     */
    public static void userFeedBack(String uid, String suggest, Observer observer) {
        UserFeedBackBean userFeedBackBean = new UserFeedBackBean(uid, suggest);
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .userFeedBack(new HttpUtils<UserFeedBackBean>().getBody(userFeedBackBean))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }

    /**
     * 上传头像
     */

    public static void uploadPic(String uid, String path, Observer observer) {
        File file = new File(path);
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), uid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        RetrofitServiceManager.getInstance().create(IUserService.class).getPicturesBean(uidBody, filePart)
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);
    }


    /**
     * 下载头像
     */
    public static void downloadUserHead(String uid, ResourceObserver observer) {
        RetrofitServiceManager.getInstance().create(IUserService.class)
                .downloadUserHead(uid)
                .subscribeOn(Schedulers.io())
//                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(observer);

    }


    /**
     * 另一种接口
     *
     * @param phone
     * @param pwd
     */
    public static Observable loginByPhone(String phone, String pwd) {
        PhoneNumberLogin phoneNumberLogin = new PhoneNumberLogin(phone, pwd);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .LoginByPhone(new HttpUtils<PhoneNumberLogin>().getBody(phoneNumberLogin))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    public static Observable loginByEmail(String email, String pwd) {
        EmailLogin phoneNumberLogin = new EmailLogin(email, pwd);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .LoginByEmail(new HttpUtils<EmailLogin>().getBody(phoneNumberLogin))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    public static Observable registerByPhone(String phone, String pwd, String token) {

        RegisterByPhone registerByPhone = new RegisterByPhone(phone, pwd, token);

        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .registerByPhone(new HttpUtils<RegisterByPhone>().getBody(registerByPhone))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    public static Observable registerByEmail(String email, String pwd, String token) {

        RegisterByEmail registerByEmail = new RegisterByEmail(email, pwd, token);

        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .registerByEmail(new HttpUtils<RegisterByEmail>().getBody(registerByEmail))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    public static Observable getRandomByEmail(String email) {
        GetRandomByEmail sendRandomByEmail = new GetRandomByEmail(email);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByEmail(new HttpUtils<GetRandomByEmail>().getBody(sendRandomByEmail))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    public static Observable getRandomByPhone(String phone, String code) {
        GetRandomByPhone getRandomByPhone = new GetRandomByPhone(phone, code);
        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .getRandomByPhone(new HttpUtils<GetRandomByPhone>().getBody(getRandomByPhone))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * @param user_name
     * @param pwd
     * @param token
     * @param type      账号类型  1手机  2邮箱
     */
    public static Observable resetPassword(String user_name, String pwd, String token, int type) {

        ResetPassword resetPassword = new ResetPassword(user_name, pwd, type, token);

        return RetrofitServiceManager.getNoTokenInstance().create(IUserService.class)
                .resetPassword(new HttpUtils<ResetPassword>().getBody(resetPassword))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }


    /**
     * 修改用户昵称
     */
    public static Observable modifyUserNickname(String uid, String nickname) {
        ModifyUserNicknameBean modifyUserNicknameBean = new ModifyUserNicknameBean(uid, nickname);
        return RetrofitServiceManager.getInstance().create(IUserService.class)
                .modifyUserNickname(new HttpUtils<ModifyUserNicknameBean>().getBody(modifyUserNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 修改原始密码
     */
    public static Observable changeOriginalPassword(String uid, String newpwd, String oldpwd) {
        ChangeOriginalPasswordBean changeOriginalPasswordBean = new ChangeOriginalPasswordBean(uid, newpwd, oldpwd);
        return RetrofitServiceManager.getInstance().create(IUserService.class)
                .changeOriginalPassword(new HttpUtils<ChangeOriginalPasswordBean>().getBody(changeOriginalPasswordBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 获取用户昵称
     */
    public static Observable getUserNickname(String uid) {
        GetUserNicknameBean getUserNicknameBean = new GetUserNicknameBean(uid);
        return RetrofitServiceManager.getInstance().create(IUserService.class)
                .getUserNickname(new HttpUtils<GetUserNicknameBean>().getBody(getUserNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 修改昵称
     */
    public static Observable changeNickname(String uid, String nickname) {
        ChangeNicknameBean changeNicknameBean = new ChangeNicknameBean(uid, nickname);
        return RetrofitServiceManager.getInstance().create(IUserService.class)
                .changeNickname(new HttpUtils<ChangeNicknameBean>().getBody(changeNicknameBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 用户反馈
     */
    public static Observable userFeedBack(String uid, String suggest) {
        UserFeedBackBean userFeedBackBean = new UserFeedBackBean(uid, suggest);
        return RetrofitServiceManager.getInstance().create(IUserService.class)
                .userFeedBack(new HttpUtils<UserFeedBackBean>().getBody(userFeedBackBean))
                .compose(RxjavaHelper.observeOnMainThread())
                ;
    }

    /**
     * 上传头像
     */

/*    public static Observable uploadPic(String uid, String path) {
        File file = new File(path);
        RequestBody uidBody = RequestBody.create(MediaType.parse("multipart/form-data"), uid);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        return RetrofitServiceManager.getInstance().create(IUserService.class).getPicturesBean(uidBody, filePart)
                .subscribeOn(Schedulers.io())
                .compose(RxjavaHelper.observeOnMainThread())
                 ;
    }*/


}
