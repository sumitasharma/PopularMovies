package com.example.android.popularmoviesstage1sumita.utils;

/**
 * Class to Represent Movie Videos
 */

public class MovieVideosDetail {
    private String ID;
    private String key;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public String getID() {

        return ID;
    }

    public String getTrailerUrl(){
        return "https://www.youtube.com/watch?v="+ this.key ;
    }

    public String getThumbnailUrl(){
        return "https://i.ytimg.com/vi/"+ this.key+"/hqdefault.jpg" ;
    }
}
