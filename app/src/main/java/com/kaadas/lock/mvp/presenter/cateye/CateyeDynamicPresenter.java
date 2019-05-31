package com.kaadas.lock.mvp.presenter.cateye;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICateyeDynamicView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeEventDao;

import java.util.ArrayList;
import java.util.List;

public class CateyeDynamicPresenter<T> extends BasePresenter<ICateyeDynamicView> {
    private List<CatEyeEvent> catEyeInfo=new ArrayList<>();
    //获取猫眼动态信息

    public void getCatEyeDynamicInfo(int page,int pageNum,String gatewayId,String deviceId){
        //获取数据库的门锁报警信息
            LogUtils.e("访问数据库的门锁信息");
        List<CatEyeEvent> catEyeEventsList=MyApplication.getInstance().getDaoWriteSession().queryBuilder(CatEyeEvent.class).orderDesc(CatEyeEventDao.Properties.EventTime).offset(page * pageNum).limit(pageNum).list();
             /*= MyApplication.getInstance().getDaoWriteSession().queryBuilder(CatEyeEvent.class);*/
            if(catEyeEventsList!=null&&catEyeEventsList.size()>0){
                for (CatEyeEvent catEyeEvent:catEyeEventsList) {
                    if (gatewayId.equals(catEyeEvent.getGatewayId())&&deviceId.equals(catEyeEvent.getDeviceId())){
                        catEyeInfo.add(catEyeEvent);
                    }
                }
                if (mViewRef!=null&&mViewRef.get()!=null){
                    mViewRef.get().getCateyeDynamicSuccess(catEyeInfo);
                    catEyeInfo.clear();
                }
            }else{
                if (mViewRef!=null&&mViewRef.get()!=null){
                    mViewRef.get().getCateyeDynamicFail();
                    if (catEyeInfo!=null) {
                        catEyeInfo.clear();
                    }
                }
            }
        }



}
