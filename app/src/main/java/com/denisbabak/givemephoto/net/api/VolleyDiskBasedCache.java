package com.denisbabak.givemephoto.net.api;

import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

/**
 * Created by denisbabak on /111/16.
 */

public class VolleyDiskBasedCache extends DiskBasedCache {

    public VolleyDiskBasedCache(File rootDirectory, int maxCacheSizeInBytes) {
        super(rootDirectory, maxCacheSizeInBytes);
    }

    public VolleyDiskBasedCache(File rootDirectory) {
        super(rootDirectory);
    }

    @Override
    public synchronized Entry get(String key) {
        try {
            return super.get(key);
        } catch (NegativeArraySizeException e) {
            //volley bug
            remove(key);
            return null;
        }
    }
}
