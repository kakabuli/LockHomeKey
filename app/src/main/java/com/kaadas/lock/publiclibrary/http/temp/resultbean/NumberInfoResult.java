package com.kaadas.lock.publiclibrary.http.temp.resultbean;

import java.util.List;

/**
 * Create By lxj  on 2019/1/29
 * Describe
 */
public class NumberInfoResult {

    List<NumberInfo> data;

    public List<NumberInfo> getData() {
        return data;
    }

    public void setData(List<NumberInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NumberInfoResult{" +
                "data=" + data +
                '}';
    }
}
