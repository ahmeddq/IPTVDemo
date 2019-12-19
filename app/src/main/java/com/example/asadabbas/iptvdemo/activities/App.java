package com.example.asadabbas.iptvdemo.activities;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class App extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

    }
}
