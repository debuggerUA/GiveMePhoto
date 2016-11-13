package com.denisbabak.givemephoto.net.model;

/**
 * Created by denisbabak on /111/16.
 */

public class PhotoModel {

    public String previewUrl;
    public String fullUrl;

    public PhotoModel(String previewUrl, String fullUrl) {
        this.previewUrl = previewUrl;
        this.fullUrl = fullUrl;
    }
}
