package com.kaadas.lock.publiclibrary.linphone.linphone.callback;



public abstract class RegistrationCallback {
    public void registrationNone() {}

    public void registrationProgress() {}
//注册成功
    public void registrationOk() {}

    public void registrationCleared() {}

    public void registrationFailed() {}
}
