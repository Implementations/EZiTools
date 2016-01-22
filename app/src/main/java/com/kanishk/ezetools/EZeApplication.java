package com.kanishk.ezetools;

import android.app.Application;

/**
 * Created by kanishk on 1/12/16.
 */
public class EZeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PdServiceManager.init(getApplicationContext());
    }
}
