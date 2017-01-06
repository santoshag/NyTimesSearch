package com.santoshag.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshag on 7/28/16.
 */
public class ArticlesResponse {

    public static int MAX_PAGES_TO_RETRIEVE = 20;

    @SerializedName("docs")
    public List<Article> articles;

    public ArticlesResponse(){
        articles = new ArrayList<Article>();
    }


}
