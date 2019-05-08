package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/4/19
 * Describe
 */

public class PirEventBus implements Serializable {

	 private  String deviceId;

	public PirEventBus(String deviceId) {
		this.deviceId = deviceId;
	}

	public PirEventBus() {
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "PirEventBus{" +
			"deviceId='" + deviceId + '\'' +
			'}';
	}
}
