package com.codepath.nytimessearch.models;

/**
 * Created by santoshag on 7/30/16.
 */
public class Thumbnail {

    public String url;

    public String getThumbnailUrl(){
        return "https://www.nytimes.com/" + url;
    }

}
