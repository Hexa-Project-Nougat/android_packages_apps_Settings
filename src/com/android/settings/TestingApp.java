package com.android.settings;

import me.dawson.applock.core.LockManager;
import android.app.Application;

public class TestingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LockManager.getInstance().enableAppLock(this);
    }

}