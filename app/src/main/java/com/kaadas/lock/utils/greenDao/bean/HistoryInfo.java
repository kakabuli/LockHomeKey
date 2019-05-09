package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * Create By denganzhi  on 2019/4/10
 * Describe
 */
@Entity
public class HistoryInfo {
	@Id(autoincrement = true)
	private  Long id;
	private String device_id;
	private Date createDate;

	private String fileName;  // 下载地址

	public HistoryInfo() {
	}


	@Generated(hash = 908359892)
	public HistoryInfo(Long id, String device_id, Date createDate,
                       String fileName) {
		this.id = id;
		this.device_id = device_id;
		this.createDate = createDate;
		this.fileName = fileName;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "HistoryInfo{" +
				"id=" + id +
				", device_id='" + device_id + '\'' +
				", createDate=" + createDate +
				", fileName='" + fileName + '\'' +
				'}';
	}
}
