package com.codepath.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshag on 7/28/16.
 */
public class ArticlesResponse {

    @SerializedName("docs")
    public List<Article> articles;

    public ArticlesResponse(){
        articles = new ArrayList<Article>();
    }


}
