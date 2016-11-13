package com.denisbabak.givemephoto;

/**
 * Created by denisbabak on 13/11/16.
 */

public enum WallpaperMode {

    ART, NATURE, PEOPLE, THINGS;

    @Override
    public String toString() {
        switch (this) {
            case ART:
                return "239051233";
            case NATURE:
                return "239050700";
            case PEOPLE:
                return "239050587";
            case THINGS:
                return "239050629";
            default:
                return "239050700";
        }
    }
}
