package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class WifiVideoLockSetPirBean implements Serializable {
    /**
     "setPir":{
     "stay_time":10,
     "pir_sen":100
     },
     */

  private int stay_time;
  private int pir_sen;




    public int getStay_time() {
        return stay_time;
    }

    public void setStay_time(int stay_time) {
        this.stay_time = stay_time;
    }

    public int getPir_sen() {
        return pir_sen;
    }

    public void setPir_sen(int pir_sen) {
        this.pir_sen = pir_sen;
    }
}
