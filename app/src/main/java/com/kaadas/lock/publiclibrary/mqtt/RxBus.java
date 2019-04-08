package com.kaadas.lock.publiclibrary.mqtt;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * rxbus
 *
 * @author FJH
 * @company kaadas
 * created at 2019/3/29 16:33
 */
public class RxBus {
    private static volatile RxBus defaultInstance;

    private final PublishSubject<String> bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        bus = PublishSubject.create();
    }

    // 单例RxBus
    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance;
    }

    // 发送一个新的事件
    public void post(String o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}

