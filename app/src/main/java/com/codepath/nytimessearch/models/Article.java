package com.codepath.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshag on 7/26/16.
 */
public class Article {



    @SerializedName("headline")
    public Headline headline;

    @SerializedName("web_url")
    public String webUrl;

    @SerializedName("multimedia")
    public List<Thumbnail> thumbnails;

    public Article(){
        thumbnails = new ArrayList<Thumbnail>();
    }

//    public String getWebUrl() {
//        return webUrl;
//    }


//    public String getThumbNail() {
//        return thumbNail;
//    }

    public Boolean doesArticleHaveThumbnail() {
        if(thumbnails.size() > 0){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

/*    public Article(JSONObject jsonObject){
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if(multimedia.length() > 0 ){
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "https://www.nytimes.com/" + multimediaJson.getString("url");
            }else{
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray){
        ArrayList<Article> results = new ArrayList<>();
        for(int x=0; x<jsonArray.length(); x++){
            try {
                results.add(new Article(jsonArray.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  results;
    }*/

}
