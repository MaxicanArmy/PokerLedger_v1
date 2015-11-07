package com.pokerledger.app;

import android.app.Application;

import com.flurry.android.FlurryAgent;

/**
 * Created by max on 11/6/15.
 */
public class PLApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, "TQJ7WDN533GGW3T5YFFJ");
    }
}
