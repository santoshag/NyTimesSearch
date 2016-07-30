package com.codepath.nytimessearch.utils;

import com.codepath.nytimessearch.models.ArticlesResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by santoshag on 7/29/16.
 */


public interface ArticlesService {
    @GET("/lists/movies/box_office.json")
    public Call<ArticlesResponse> listRepos();
}