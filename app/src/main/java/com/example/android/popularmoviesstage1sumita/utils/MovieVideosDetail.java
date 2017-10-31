package com.example.android.popularmoviesstage1sumita.utils;

/**
 * Class to Represent Movie Videos
 */

public class MovieVideosDetail {
    private String ID;
    private String key;


    public void setKey(String key) {
        this.key = key;
    }


    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTrailerUrl() {
        return MoviesUtil.VIDEOURLPREFIX + this.key;
    }

    public String getThumbnailUrl() {
        return MoviesUtil.VIDEOTHUMBNAILPREFIX + this.key + MoviesUtil.VIDEOTHUMBNAILPOSTFIX;
    }
}
