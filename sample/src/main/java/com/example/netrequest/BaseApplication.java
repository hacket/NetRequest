package com.example.netrequest;

import com.example.netlibrary.util.RunningContext;

import android.app.Application;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initRunningContext();
    }

    private void initRunningContext() {
        RunningContext.initContext(getApplicationContext());
        RunningContext.initLogDebug(BuildConfig.LOG_DEBUG);
    }

}
