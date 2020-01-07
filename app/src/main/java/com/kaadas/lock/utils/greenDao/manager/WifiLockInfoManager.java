package com.kaadas.lock.utils.greenDao.manager;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.greenDao.db.WifiLockInfoDao;


public class WifiLockInfoManager {

    private WifiLockInfoDao dao;

    public WifiLockInfoManager() {
        dao = MyApplication.getInstance().getDaoWriteSession().getWifiLockInfoDao();
    }




    /**
     * 根据编号删除密码
     * @param wifiSN
     */
    public void deleteBySn(String wifiSN) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getWifiLockInfoDao();
        }
        dao.queryBuilder().where(
                WifiLockInfoDao.Properties.WifiSN.eq(wifiSN)
        ).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 先删除  再添加
     * @param bean
     */
    public void insertOrReplace( WifiLockInfo bean) {
        if (bean == null){
            return;
        }
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getWifiLockInfoDao();
        }
        deleteBySn(bean.getWifiSN());
        dao.insert(bean);
    }


}
