package la.xiong.androidquick.ui.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.exceptions.CompositeException;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by yanliang
 * on 2018/9/12 10:24
 */

public class HttpSubRequest {
        private Set<DisposableObserver> subscriptions;
        private volatile boolean unsubscribed;

        public HttpSubRequest() {
        }

        public HttpSubRequest(DisposableObserver... subscriptions) {
            this.subscriptions = new HashSet(Arrays.asList(subscriptions));
        }
        public boolean isUnsubscribed() {
            return this.unsubscribed;
        }

        public  <T extends DisposableObserver> T add(T s) {

            synchronized (this) {
                if (this.subscriptions == null) {
                    this.subscriptions = new HashSet(4);
                }
                this.subscriptions.add(s);

            }
            return s;
        }
        public void remove(DisposableObserver s) {
            synchronized(this) {
            if(this.subscriptions == null) {
                  subscriptions.remove(s);
               }
            }
        }
        public void clear() {
            Collection<DisposableObserver> unsubscribe = null;
                synchronized(this) {
                    if(this.subscriptions == null) {
                        return;
                    }
                    unsubscribe=subscriptions;
                    this.subscriptions = null;
                }
                unsubscribeFromAll(unsubscribe);
            }
        private  void unsubscribeFromAll(Collection<DisposableObserver> subscriptions) {
            if(subscriptions != null) {
                List<Throwable> es = null;
                Iterator i$ = subscriptions.iterator();

                while(i$.hasNext()) {
                    DisposableObserver s = (DisposableObserver)i$.next();
                    try {
                        synchronized(HttpSubRequest.this) {
                          if(!s.isDisposed()){
                              s.dispose();
                          }
                        }
                    } catch (Throwable var5) {
                        if(es == null) {
                            es = new ArrayList();
                        }

                        es.add(var5);
                    }
                }

                throwIfAny(es);
            }
        }

    private  void throwIfAny(List<? extends Throwable> exceptions) {
        if (exceptions != null && !exceptions.isEmpty()) {
            if (exceptions.size() == 1) {
                Throwable t = exceptions.get(0);
                // had to manually inline propagate because some tests attempt StackOverflowError
                // and can't handle it with the stack space remaining
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else if (t instanceof Error) {
                    throw (Error) t;
                } else {
                    throw new RuntimeException(t); // NOPMD
                }
            }
            throw new CompositeException(exceptions);
        }
    }
}
