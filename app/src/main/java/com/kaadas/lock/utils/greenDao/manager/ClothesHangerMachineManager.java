package com.kaadas.lock.utils.greenDao.manager;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.greenDao.bean.ClothesHangerMachineAllBean;
import com.kaadas.lock.utils.greenDao.db.ClothesHangerMachineAllBeanDao;

public class ClothesHangerMachineManager {

    private ClothesHangerMachineAllBeanDao dao;

    public ClothesHangerMachineManager(){
        dao = MyApplication.getInstance().getDaoWriteSession().getClothesHangerMachineAllBeanDao();
    }

    /**
     * 根据SN删除
     */
    public void deleteBySn(String wifiSn){
        if(dao == null){
            dao = MyApplication.getInstance().getDaoWriteSession().getClothesHangerMachineAllBeanDao();
        }
        dao.queryBuilder().where(
                ClothesHangerMachineAllBeanDao.Properties.WifiSN.eq(wifiSn))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 更新表
     * 先删除，后添加
     */
    public void insertOrReplace(ClothesHangerMachineAllBean bean){
        if (bean == null){
            return;
        }
        if(dao == null){
            dao = MyApplication.getInstance().getDaoWriteSession().getClothesHangerMachineAllBeanDao();
        }
        deleteBySn(bean.getWifiSN());
        dao.insert(bean);
    }
}
