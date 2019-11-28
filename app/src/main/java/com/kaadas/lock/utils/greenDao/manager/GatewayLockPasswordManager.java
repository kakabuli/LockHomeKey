package com.kaadas.lock.utils.greenDao.manager;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;
import com.kaadas.lock.utils.greenDao.db.GatewayPasswordPlanBeanDao;

import java.util.List;

public class GatewayLockPasswordManager {

    private GatewayPasswordPlanBeanDao dao;

    public GatewayLockPasswordManager() {
        dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
    }

    /**
     * 先删除再添加
     * @param deviceId
     * @param uid
     * @param gatewayId
     * @param passwordPlanBeans
     */
    public void insertAfterDelete(String deviceId, String uid, String gatewayId, List<GatewayPasswordPlanBean> passwordPlanBeans) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        deleteAll(deviceId, uid, gatewayId);
        dao.insertInTx(passwordPlanBeans);
    }


    /**
     * 删除所有的密码
     * @param deviceId
     * @param uid
     * @param gatewayId
     */
    public void deleteAll(String deviceId, String uid, String gatewayId) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        dao.queryBuilder().where(
                GatewayPasswordPlanBeanDao.Properties.DeviceId.eq(deviceId),
                GatewayPasswordPlanBeanDao.Properties.GatewayId.eq(gatewayId),
                GatewayPasswordPlanBeanDao.Properties.Uid.eq(uid)
        ).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据编号删除密码
     * @param deviceId
     * @param uid
     * @param gatewayId
     * @param number
     */
    public void deleteByNumber(String deviceId, String uid, String gatewayId, int number) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        dao.queryBuilder().where(
                GatewayPasswordPlanBeanDao.Properties.DeviceId.eq(deviceId),
                GatewayPasswordPlanBeanDao.Properties.GatewayId.eq(gatewayId),
                GatewayPasswordPlanBeanDao.Properties.Uid.eq(uid),
                GatewayPasswordPlanBeanDao.Properties.PasswordNumber.eq(number)
        ).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 先删除  再添加
     * @param deviceId
     * @param uid
     * @param gatewayId
     * @param bean
     */
    public void insertOrReplace(String deviceId, String uid, String gatewayId, GatewayPasswordPlanBean bean) {
        if (bean == null){
            return;
        }
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        int number = bean.getPasswordNumber();
        deleteByNumber(deviceId, uid, gatewayId, number);
        dao.insert(bean);
    }


    /**
     * 查询所有数据
     * @param deviceId
     * @param uid
     * @param gatewayId
     * @return
     */
    public List<GatewayPasswordPlanBean> queryAll(String deviceId, String uid, String gatewayId) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        return dao.queryBuilder().where(GatewayPasswordPlanBeanDao.Properties.DeviceId.eq(deviceId),
                GatewayPasswordPlanBeanDao.Properties.GatewayId.eq(gatewayId),
                GatewayPasswordPlanBeanDao.Properties.Uid.eq(uid)).list();
    }


    /**
     *
     */
    public void insertWhenNoExist(String deviceId, String uid, String gatewayId, GatewayPasswordPlanBean gatewayPasswordPlanBean) {
        if (dao == null) {
            dao = MyApplication.getInstance().getDaoWriteSession().getGatewayPasswordPlanBeanDao();
        }
        int number = gatewayPasswordPlanBean.getPasswordNumber();
        GatewayPasswordPlanBean GatewayPasswordPlanBean = dao.queryBuilder().where(
                GatewayPasswordPlanBeanDao.Properties.DeviceId.eq(deviceId),
                GatewayPasswordPlanBeanDao.Properties.GatewayId.eq(gatewayId),
                GatewayPasswordPlanBeanDao.Properties.Uid.eq(uid),
                GatewayPasswordPlanBeanDao.Properties.PasswordNumber.eq(number)
        ).unique();
        if (GatewayPasswordPlanBean==null){
            dao.insert(gatewayPasswordPlanBean);
       }
    }


}
