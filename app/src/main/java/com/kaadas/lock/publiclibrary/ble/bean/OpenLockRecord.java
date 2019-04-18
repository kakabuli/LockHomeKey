package com.kaadas.lock.publiclibrary.ble.bean;

public class OpenLockRecord {
	@Override
	public String toString() {
		return "OpenLockRecordBle{" +
				"user_num='" + user_num + '\'' +
				", open_type='" + open_type + '\'' +
				", open_time='" + open_time + '\'' +
				", index=" + index +
				'}';
	}

	//用户编号
	String user_num;
	String open_type;//开门类型 指纹密码卡片
	String open_time;//开门时间

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
    //提供索引字段,根据索引排序
	private int index;

	public OpenLockRecord(String user_num, String open_type, String open_time, int index) {
		this.user_num = user_num;
		this.open_type = open_type;
		this.open_time = open_time;
		this.index = index;
	}

	public String getUser_num() {
		return user_num;
	}

	public void setUser_num(String user_num) {
		this.user_num = user_num;
	}

	public String getOpen_type() {
		return open_type;
	}

	public void setOpen_type(String open_type) {
		this.open_type = open_type;
	}

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
}
