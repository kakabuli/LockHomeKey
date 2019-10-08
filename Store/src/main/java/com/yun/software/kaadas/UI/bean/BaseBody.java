package com.yun.software.kaadas.UI.bean;

import java.util.List;

public class BaseBody<T> {

	public List<T> list;
	public int total;

	public List<T> getRows() {
		return list;
	}

	public void setRows(List<T> list) {
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

    @Override
    public String toString() {
        return "BaseBody{" +
                "rows=" + list +
                ", total=" + total +
                '}';
    }
}
