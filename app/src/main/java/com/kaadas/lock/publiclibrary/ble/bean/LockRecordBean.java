package com.kaadas.lock.publiclibrary.ble.bean;

public class LockRecordBean {
	@Override
	public String toString() {
		return "LockRecordBean{" +
				"device_name='" + device_name + '\'' +
				", device_nickname='" + device_nickname + '\'' +
				", open_user='" + open_user + '\'' +
				", open_nickname='" + open_nickname + '\'' +
				", _id='" + _id + '\'' +
				", lockName='" + lockName + '\'' +
				", lockNickName='" + lockNickName + '\'' +
				", uname='" + uname + '\'' +
				", open_time='" + open_time + '\'' +
				", open_type='" + open_type + '\'' +
				", open_purview='" + open_purview + '\'' +
				", user_num='" + user_num + '\'' +
				'}';
	}

	private String device_name;  //设备名称
	private String device_nickname;  //设备昵称
	private String open_user;   //开锁用户?
	private String open_nickname;  //开锁用户的昵称
	private String _id;
	private String lockName;   //锁名称
	private String lockNickName;  //锁昵称
	private String uname;   //
	private String open_time;   //开锁时间
	private String open_type;    //开锁类型
	private String open_purview;  //
	private String user_num;   //用户编号

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public String getLockNickName() {
		return lockNickName;
	}

	public void setLockNickName(String lockNickName) {
		this.lockNickName = lockNickName;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getOpen_purview() {
		return open_purview;
	}

	public void setOpen_purview(String open_purview) {
		this.open_purview = open_purview;
	}

	public String getUser_num() {
		return user_num;
	}

	public void setUser_num(String user_num) {
		this.user_num = user_num;
	}

	public void setOpen_type(String open_type) {
		this.open_type = open_type;
	}



	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDevice_name() {
		return this.device_name;
	}

	public void setDevice_nickname(String device_nickname) {
		this.device_nickname = device_nickname;
	}

	public String getDevice_nickname() {
		return this.device_nickname;
	}

	public void setOpen_user(String open_user) {
		this.open_user = open_user;
	}

	public String getOpen_user() {
		return this.open_user;
	}

	public void setOpen_nickname(String open_nickname) {
		this.open_nickname = open_nickname;
	}

	public String getOpen_nickname() {
		return this.open_nickname;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getOpen_time() {
		return this.open_time;
	}

	public String getOpen_type() {
		return open_type;
	}

}
