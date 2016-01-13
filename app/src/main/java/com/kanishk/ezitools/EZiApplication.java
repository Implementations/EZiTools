package com.kanishk.ezitools;

import android.app.Application;

/**
 * Created by kanishk on 1/12/16.
 */
public class EZiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PdServiceManager.init(getApplicationContext());
    }
}
