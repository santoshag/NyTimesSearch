package com.santoshag.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by santoshag on 7/30/16.
 */
@Parcel
public class Headline {

    @SerializedName("main")
    public String mainHeadline;
}
