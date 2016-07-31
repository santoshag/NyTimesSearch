package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

/**
 * Created by santoshag on 7/30/16.
 */
@Parcel
public class Thumbnail {

    public String url;

    public String getThumbnailUrl(){
        return "https://www.nytimes.com/" + url;
    }

}
