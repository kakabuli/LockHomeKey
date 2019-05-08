package com.kaidishi.lock.util;

public class g711mux {
    public g711mux(){}
    public native int mux(String avcpath,String g711path,String mkvpath,int framerate,int samplerate);
    public native String getver();
    static {
        // The runtime will add "lib" on the front and ".so" on the end of
        // the name supplied to loadLibrary.
        System.loadLibrary("muxg711");
    }
}
