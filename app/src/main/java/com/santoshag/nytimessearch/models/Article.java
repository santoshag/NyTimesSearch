package com.santoshag.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshag on 7/26/16.
 */
@Parcel
public class Article {



    @SerializedName("headline")
    public Headline headline;

    @SerializedName("web_url")
    public String webUrl;

    @SerializedName("snippet")
    public String snippet;

    @SerializedName("multimedia")
    public List<Thumbnail> thumbnails;

    public Article(){
        thumbnails = new ArrayList<Thumbnail>();
    }

    public Boolean doesArticleHaveThumbnail() {
        if(thumbnails.size() > 0){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

}
