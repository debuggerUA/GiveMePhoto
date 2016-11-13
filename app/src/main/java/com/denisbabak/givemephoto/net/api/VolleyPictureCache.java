package com.denisbabak.givemephoto.net.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by denisbabak on /111/16.
 */

public class VolleyPictureCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public VolleyPictureCache(int maxSize) {
        super(maxSize);
    }

    public VolleyPictureCache(Context ctx) {
        this(getCacheSize(ctx));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    // Returns a cache size equal to approximately three screens worth of images.
    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        int cacheSize = screenBytes * 5;

        return cacheSize;
    }

    /**
     * Creates a cache key for use with the L1 cache.
     * @param url The URL of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     * @param scaleType The scaleType of the imageView.
     */
    public static String getCacheKey(String url, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append("#S").append(scaleType.ordinal()).append(url)
                .toString();
    }
}

