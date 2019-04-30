package com.kaadas.lock.bean;

public class MyDate {
	
	private int day;
	private String week;
	private boolean isChecked=false;

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public MyDate(int day, String week) {
		super();
		this.day = day;
		this.week = week;
	}
	public MyDate() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "MyDate [day=" + day + ", week=" + week + "]";
	}

	public MyDate(int day, String week, boolean isChecked) {
		this.day = day;
		this.week = week;
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}
}
