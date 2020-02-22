package com.kaadas.lock.bean;

public class SingleSwitchTimerShowBean {
    String time;
    String repeat;
    String action;
    boolean open;

    public SingleSwitchTimerShowBean() {
    }

    public SingleSwitchTimerShowBean(String time, String repeat, String action, boolean open) {
        this.time = time;
        this.repeat = repeat;
        this.action = action;
        this.open = open;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "SingleSwitchTimerShowBean{" +
                "time='" + time + '\'' +
                ", repeat='" + repeat + '\'' +
                ", action='" + action + '\'' +
                ", open=" + open +
                '}';
    }
}
