package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DBOpenLockRecord {
	@Override
	public String toString() {
		return "OpenLockRecordBle{" +
				"user_num='" + user_num + '\'' +
				", open_type='" + open_type + '\'' +
				", open_time='" + open_time + '\'' +
				", index=" + index +
				'}';
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_num() {
		return this.user_num;
	}
	public void setUser_num(String user_num) {
		this.user_num = user_num;
	}
	public String getOpen_type() {
		return this.open_type;
	}
	public void setOpen_type(String open_type) {
		this.open_type = open_type;
	}
	public String getOpen_time() {
		return this.open_time;
	}
	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
	public int getIndex() {
		return this.index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Id(autoincrement = true)
	private Long id;
	//用户编号
	String user_num;
	String open_type;//开门类型 指纹密码卡片
	String open_time;//开门时间
	//提供索引字段,根据索引排序
	private int index;
	@Generated(hash = 317002155)
	public DBOpenLockRecord(Long id, String user_num, String open_type,
			String open_time, int index) {
		this.id = id;
		this.user_num = user_num;
		this.open_type = open_type;
		this.open_time = open_time;
		this.index = index;
	}
	@Generated(hash = 1852048577)
	public DBOpenLockRecord() {
	}


}
