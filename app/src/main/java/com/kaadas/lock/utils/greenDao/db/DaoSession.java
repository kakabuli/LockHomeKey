package com.kaadas.lock.utils.greenDao.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.kaadas.lock.utils.greenDao.bean.BleLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.CateEyeInfoBase;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.bean.CatEyeServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.DBOpenLockRecord;
import com.kaadas.lock.utils.greenDao.bean.GatewayBaseInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockRecord;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.HistoryInfo;
import com.kaadas.lock.utils.greenDao.bean.PirDefault;

import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.CateEyeInfoBaseDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeEventDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DBOpenLockRecordDao;
import com.kaadas.lock.utils.greenDao.db.GatewayBaseInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockAlarmEventDaoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockRecordDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.HistoryInfoDao;
import com.kaadas.lock.utils.greenDao.db.PirDefaultDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bleLockServiceInfoDaoConfig;
    private final DaoConfig cateEyeInfoBaseDaoConfig;
    private final DaoConfig catEyeEventDaoConfig;
    private final DaoConfig catEyeServiceInfoDaoConfig;
    private final DaoConfig dBOpenLockRecordDaoConfig;
    private final DaoConfig gatewayBaseInfoDaoConfig;
    private final DaoConfig gatewayLockAlarmEventDaoDaoConfig;
    private final DaoConfig gatewayLockPwdDaoConfig;
    private final DaoConfig gatewayLockRecordDaoConfig;
    private final DaoConfig gatewayLockServiceInfoDaoConfig;
    private final DaoConfig gatewayServiceInfoDaoConfig;
    private final DaoConfig historyInfoDaoConfig;
    private final DaoConfig pirDefaultDaoConfig;

    private final BleLockServiceInfoDao bleLockServiceInfoDao;
    private final CateEyeInfoBaseDao cateEyeInfoBaseDao;
    private final CatEyeEventDao catEyeEventDao;
    private final CatEyeServiceInfoDao catEyeServiceInfoDao;
    private final DBOpenLockRecordDao dBOpenLockRecordDao;
    private final GatewayBaseInfoDao gatewayBaseInfoDao;
    private final GatewayLockAlarmEventDaoDao gatewayLockAlarmEventDaoDao;
    private final GatewayLockPwdDao gatewayLockPwdDao;
    private final GatewayLockRecordDao gatewayLockRecordDao;
    private final GatewayLockServiceInfoDao gatewayLockServiceInfoDao;
    private final GatewayServiceInfoDao gatewayServiceInfoDao;
    private final HistoryInfoDao historyInfoDao;
    private final PirDefaultDao pirDefaultDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bleLockServiceInfoDaoConfig = daoConfigMap.get(BleLockServiceInfoDao.class).clone();
        bleLockServiceInfoDaoConfig.initIdentityScope(type);

        cateEyeInfoBaseDaoConfig = daoConfigMap.get(CateEyeInfoBaseDao.class).clone();
        cateEyeInfoBaseDaoConfig.initIdentityScope(type);

        catEyeEventDaoConfig = daoConfigMap.get(CatEyeEventDao.class).clone();
        catEyeEventDaoConfig.initIdentityScope(type);

        catEyeServiceInfoDaoConfig = daoConfigMap.get(CatEyeServiceInfoDao.class).clone();
        catEyeServiceInfoDaoConfig.initIdentityScope(type);

        dBOpenLockRecordDaoConfig = daoConfigMap.get(DBOpenLockRecordDao.class).clone();
        dBOpenLockRecordDaoConfig.initIdentityScope(type);

        gatewayBaseInfoDaoConfig = daoConfigMap.get(GatewayBaseInfoDao.class).clone();
        gatewayBaseInfoDaoConfig.initIdentityScope(type);

        gatewayLockAlarmEventDaoDaoConfig = daoConfigMap.get(GatewayLockAlarmEventDaoDao.class).clone();
        gatewayLockAlarmEventDaoDaoConfig.initIdentityScope(type);

        gatewayLockPwdDaoConfig = daoConfigMap.get(GatewayLockPwdDao.class).clone();
        gatewayLockPwdDaoConfig.initIdentityScope(type);

        gatewayLockRecordDaoConfig = daoConfigMap.get(GatewayLockRecordDao.class).clone();
        gatewayLockRecordDaoConfig.initIdentityScope(type);

        gatewayLockServiceInfoDaoConfig = daoConfigMap.get(GatewayLockServiceInfoDao.class).clone();
        gatewayLockServiceInfoDaoConfig.initIdentityScope(type);

        gatewayServiceInfoDaoConfig = daoConfigMap.get(GatewayServiceInfoDao.class).clone();
        gatewayServiceInfoDaoConfig.initIdentityScope(type);

        historyInfoDaoConfig = daoConfigMap.get(HistoryInfoDao.class).clone();
        historyInfoDaoConfig.initIdentityScope(type);

        pirDefaultDaoConfig = daoConfigMap.get(PirDefaultDao.class).clone();
        pirDefaultDaoConfig.initIdentityScope(type);

        bleLockServiceInfoDao = new BleLockServiceInfoDao(bleLockServiceInfoDaoConfig, this);
        cateEyeInfoBaseDao = new CateEyeInfoBaseDao(cateEyeInfoBaseDaoConfig, this);
        catEyeEventDao = new CatEyeEventDao(catEyeEventDaoConfig, this);
        catEyeServiceInfoDao = new CatEyeServiceInfoDao(catEyeServiceInfoDaoConfig, this);
        dBOpenLockRecordDao = new DBOpenLockRecordDao(dBOpenLockRecordDaoConfig, this);
        gatewayBaseInfoDao = new GatewayBaseInfoDao(gatewayBaseInfoDaoConfig, this);
        gatewayLockAlarmEventDaoDao = new GatewayLockAlarmEventDaoDao(gatewayLockAlarmEventDaoDaoConfig, this);
        gatewayLockPwdDao = new GatewayLockPwdDao(gatewayLockPwdDaoConfig, this);
        gatewayLockRecordDao = new GatewayLockRecordDao(gatewayLockRecordDaoConfig, this);
        gatewayLockServiceInfoDao = new GatewayLockServiceInfoDao(gatewayLockServiceInfoDaoConfig, this);
        gatewayServiceInfoDao = new GatewayServiceInfoDao(gatewayServiceInfoDaoConfig, this);
        historyInfoDao = new HistoryInfoDao(historyInfoDaoConfig, this);
        pirDefaultDao = new PirDefaultDao(pirDefaultDaoConfig, this);

        registerDao(BleLockServiceInfo.class, bleLockServiceInfoDao);
        registerDao(CateEyeInfoBase.class, cateEyeInfoBaseDao);
        registerDao(CatEyeEvent.class, catEyeEventDao);
        registerDao(CatEyeServiceInfo.class, catEyeServiceInfoDao);
        registerDao(DBOpenLockRecord.class, dBOpenLockRecordDao);
        registerDao(GatewayBaseInfo.class, gatewayBaseInfoDao);
        registerDao(GatewayLockAlarmEventDao.class, gatewayLockAlarmEventDaoDao);
        registerDao(GatewayLockPwd.class, gatewayLockPwdDao);
        registerDao(GatewayLockRecord.class, gatewayLockRecordDao);
        registerDao(GatewayLockServiceInfo.class, gatewayLockServiceInfoDao);
        registerDao(GatewayServiceInfo.class, gatewayServiceInfoDao);
        registerDao(HistoryInfo.class, historyInfoDao);
        registerDao(PirDefault.class, pirDefaultDao);
    }
    
    public void clear() {
        bleLockServiceInfoDaoConfig.clearIdentityScope();
        cateEyeInfoBaseDaoConfig.clearIdentityScope();
        catEyeEventDaoConfig.clearIdentityScope();
        catEyeServiceInfoDaoConfig.clearIdentityScope();
        dBOpenLockRecordDaoConfig.clearIdentityScope();
        gatewayBaseInfoDaoConfig.clearIdentityScope();
        gatewayLockAlarmEventDaoDaoConfig.clearIdentityScope();
        gatewayLockPwdDaoConfig.clearIdentityScope();
        gatewayLockRecordDaoConfig.clearIdentityScope();
        gatewayLockServiceInfoDaoConfig.clearIdentityScope();
        gatewayServiceInfoDaoConfig.clearIdentityScope();
        historyInfoDaoConfig.clearIdentityScope();
        pirDefaultDaoConfig.clearIdentityScope();
    }

    public BleLockServiceInfoDao getBleLockServiceInfoDao() {
        return bleLockServiceInfoDao;
    }

    public CateEyeInfoBaseDao getCateEyeInfoBaseDao() {
        return cateEyeInfoBaseDao;
    }

    public CatEyeEventDao getCatEyeEventDao() {
        return catEyeEventDao;
    }

    public CatEyeServiceInfoDao getCatEyeServiceInfoDao() {
        return catEyeServiceInfoDao;
    }

    public DBOpenLockRecordDao getDBOpenLockRecordDao() {
        return dBOpenLockRecordDao;
    }

    public GatewayBaseInfoDao getGatewayBaseInfoDao() {
        return gatewayBaseInfoDao;
    }

    public GatewayLockAlarmEventDaoDao getGatewayLockAlarmEventDaoDao() {
        return gatewayLockAlarmEventDaoDao;
    }

    public GatewayLockPwdDao getGatewayLockPwdDao() {
        return gatewayLockPwdDao;
    }

    public GatewayLockRecordDao getGatewayLockRecordDao() {
        return gatewayLockRecordDao;
    }

    public GatewayLockServiceInfoDao getGatewayLockServiceInfoDao() {
        return gatewayLockServiceInfoDao;
    }

    public GatewayServiceInfoDao getGatewayServiceInfoDao() {
        return gatewayServiceInfoDao;
    }

    public HistoryInfoDao getHistoryInfoDao() {
        return historyInfoDao;
    }

    public PirDefaultDao getPirDefaultDao() {
        return pirDefaultDao;
    }

}
