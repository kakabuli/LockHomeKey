package com.kaadas.lock.publiclibrary.rxutils;

/**
 * Create By lxj  on 2019/1/25
 * Describe
 */
public class TimeOutException extends Throwable {

    @Override
    public String getMessage() {
        return "Time Out";
    }
}
