package com.kaadas.lock.publiclibrary.ble;


import com.kaadas.lock.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by 瑜哥 on 2018/4/13.
 */

public class RetryWithTime implements Function<Observable<Throwable>, ObservableSource<?>> {
    int current = -1;
    //延时时间随心所欲，媳妇再也不用担心时间太短了，只需在数组任意添加延时时间即可

    int times = 5;  //
    long interval = 1;


    public RetryWithTime() {
//        LogUtils.e("重连");
    }

    /**
     * @param time     次数
     * @param interval 每次的间隔时间
     */
    public RetryWithTime(int time, long interval) {
        this.times = time;
        this.interval = interval;
    }

    @Override
    public Observable apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Observable<Long>>() {
            @Override
            public Observable<Long> apply(Throwable throwable) throws Exception {
                ++current;
                LogUtils.e("重连中   " + current);
                if (current < times) {
                    return Observable.timer(interval, TimeUnit.MILLISECONDS);
                } else {
                    return Observable.error(throwable);
                }
            }
        });
    }
}
