package com.kaadas.lock.utils.greenDao.bean;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineHangerStateBean;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineLightingBean;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineMotorBean;
import com.kaadas.lock.utils.greenDao.convert.ClothesHangerMachineHangerStateBeanConvert;
import com.kaadas.lock.utils.greenDao.convert.ClothesHangerMachineLightConvert;
import com.kaadas.lock.utils.greenDao.convert.ClothesHangerMachineMotorConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class ClothesHangerMachineAllBean implements Serializable {

    private static final long serialVersionUID = -3083537019826855670L;
    /*
		"hangerList": [{
			"_id": "5ff2aefc2a292e5fbc8ed3fa",
			"wifiSN": "KV51203710172",
			"adminName": "8618589024274",
			"adminUid": "5f5a0002c36a8525eb648432",
			"createTime": 1609740028,
			"hangerNickName": "KV51203710172",
			"isAdmin": 1,
			"uid": "5f5a0002c36a8525eb648432",
			"uname": "8618589024274",
			"updateTime": 1609740028,
			"hangerSN": "1.0.1",
			"hangerVersion": "1.0.1",
			"moduleSN": "1.0.1",
			"moduleVersion": "1.0.1",
			"updateTime": 1609746248,
			"UV": {
				"switch": 0
			},
			"airDry": {
				"switch": 0
			},
			"baking": {
				"switch": 0
			},
			"childLock": 1,
			"light": {
				"switch": 0
			},
			"loudspeaker": 1,
			"motor": {
				"action": 1,
				"status": 0
			},
			"overload": 1,
			"status": 1
		}],
     */

    @Id(autoincrement = true)
    private Long id;

    @SerializedName("_id")
    private String deviceID;
    private String wifiSN;
    private String adminName;
    private String adminUid;
    private long createTime;
    private String hangerNickName;
    private int isAdmin;
    private String uid;
    private String uname;
    private long updateTime;
    private String hangerSN;
    private String hangerVersion;
    private String moduleSN;
    private String moduleVersion;
    private int loudspeaker;
    private int childLock;
    private int overload;
    private int status;

    @Convert(converter = ClothesHangerMachineMotorConvert.class , columnType = String.class)
	private ClothesHangerMachineMotorBean motor;

	@Convert(converter = ClothesHangerMachineLightConvert.class , columnType = String.class)
	private ClothesHangerMachineLightingBean UV;

	@Convert(converter = ClothesHangerMachineLightConvert.class , columnType = String.class)
	private ClothesHangerMachineLightingBean airDry;

	@Convert(converter = ClothesHangerMachineLightConvert.class , columnType = String.class)
	private ClothesHangerMachineLightingBean baking;

	@Convert(converter = ClothesHangerMachineLightConvert.class , columnType = String.class)
	private ClothesHangerMachineLightingBean light;

	@Generated(hash = 436272932)
	public ClothesHangerMachineAllBean(Long id, String deviceID, String wifiSN,
			String adminName, String adminUid, long createTime, String hangerNickName, int isAdmin,
			String uid, String uname, long updateTime, String hangerSN, String hangerVersion,
			String moduleSN, String moduleVersion, int loudspeaker, int childLock, int overload,
			int status, ClothesHangerMachineMotorBean motor, ClothesHangerMachineLightingBean UV,
			ClothesHangerMachineLightingBean airDry, ClothesHangerMachineLightingBean baking,
			ClothesHangerMachineLightingBean light) {
		this.id = id;
		this.deviceID = deviceID;
		this.wifiSN = wifiSN;
		this.adminName = adminName;
		this.adminUid = adminUid;
		this.createTime = createTime;
		this.hangerNickName = hangerNickName;
		this.isAdmin = isAdmin;
		this.uid = uid;
		this.uname = uname;
		this.updateTime = updateTime;
		this.hangerSN = hangerSN;
		this.hangerVersion = hangerVersion;
		this.moduleSN = moduleSN;
		this.moduleVersion = moduleVersion;
		this.loudspeaker = loudspeaker;
		this.childLock = childLock;
		this.overload = overload;
		this.status = status;
		this.motor = motor;
		this.UV = UV;
		this.airDry = airDry;
		this.baking = baking;
		this.light = light;
	}

	@Generated(hash = 19920523)
	public ClothesHangerMachineAllBean() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceID() {
		return this.deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getWifiSN() {
		return this.wifiSN;
	}

	public void setWifiSN(String wifiSN) {
		this.wifiSN = wifiSN;
	}

	public String getAdminName() {
		return this.adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminUid() {
		return this.adminUid;
	}

	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}

	public long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getHangerNickName() {
		return this.hangerNickName;
	}

	public void setHangerNickName(String hangerNickName) {
		this.hangerNickName = hangerNickName;
	}

	public int getIsAdmin() {
		return this.isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return this.uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getHangerSN() {
		return this.hangerSN;
	}

	public void setHangerSN(String hangerSN) {
		this.hangerSN = hangerSN;
	}

	public String getHangerVersion() {
		return this.hangerVersion;
	}

	public void setHangerVersion(String hangerVersion) {
		this.hangerVersion = hangerVersion;
	}

	public String getModuleSN() {
		return this.moduleSN;
	}

	public void setModuleSN(String moduleSN) {
		this.moduleSN = moduleSN;
	}

	public String getModuleVersion() {
		return this.moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public int getLoudspeaker() {
		return this.loudspeaker;
	}

	public void setLoudspeaker(int loudspeaker) {
		this.loudspeaker = loudspeaker;
	}

	public int getChildLock() {
		return this.childLock;
	}

	public void setChildLock(int childLock) {
		this.childLock = childLock;
	}

	public int getOverload() {
		return this.overload;
	}

	public void setOverload(int overload) {
		this.overload = overload;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ClothesHangerMachineMotorBean getMotor() {
		return this.motor;
	}

	public void setMotor(ClothesHangerMachineMotorBean motor) {
		this.motor = motor;
	}

	public ClothesHangerMachineLightingBean getUV() {
		return this.UV;
	}

	public void setUV(ClothesHangerMachineLightingBean UV) {
		this.UV = UV;
	}

	public ClothesHangerMachineLightingBean getAirDry() {
		return this.airDry;
	}

	public void setAirDry(ClothesHangerMachineLightingBean airDry) {
		this.airDry = airDry;
	}

	public ClothesHangerMachineLightingBean getBaking() {
		return this.baking;
	}

	public void setBaking(ClothesHangerMachineLightingBean baking) {
		this.baking = baking;
	}

	public ClothesHangerMachineLightingBean getLight() {
		return this.light;
	}

	public void setLight(ClothesHangerMachineLightingBean light) {
		this.light = light;
	}


}
