package com.denisbabak.givemephoto;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.vk.sdk.VKSdk;

/**
 * Created by denisbabak on /111/16.
 */

public class GiveMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        VKSdk.initialize(this);
    }
}
