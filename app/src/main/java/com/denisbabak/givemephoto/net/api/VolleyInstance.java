package com.denisbabak.givemephoto.net.api;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Created by denisbabak on /111/16.
 */

public enum VolleyInstance {
    INSTANCE;

    private RequestQueue volleyRequestQueue;

    private static final String DEFAULT_CACHE_DIR = "volley";

    private void initVolleyQueue(Context ctx) {
        HttpURLConnection.setFollowRedirects(true);
        File cacheDir = new File(ctx.getCacheDir(), DEFAULT_CACHE_DIR);
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        volleyRequestQueue = new RequestQueue(new VolleyDiskBasedCache(cacheDir, 20 * 1024 * 1024), network);
        volleyRequestQueue.start();
        //volleyRequestQueue = Volley.newRequestQueue(ctx);
        HttpURLConnection.setFollowRedirects(true);
    }

    public RequestQueue getQueue(Context ctx) {
        if(volleyRequestQueue == null)
            initVolleyQueue(ctx);
        return volleyRequestQueue;
    }

}
