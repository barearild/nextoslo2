package com.barearild.next.v2;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class NextOsloApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
