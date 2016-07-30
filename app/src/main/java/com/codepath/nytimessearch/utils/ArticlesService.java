package com.codepath.nytimessearch.utils;

import com.codepath.nytimessearch.models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by santoshag on 7/29/16.
 */


public interface ArticlesService {

    String BASE_URL = "https://api.nytimes.com";

    @GET("/svc/search/v2/articlesearch.json")
    public Call<Response> listArticles(@Query("q") String query, @Query("page") int page);


}