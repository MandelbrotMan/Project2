package com.example.raymondlian.movieappv2;

import android.os.Parcel;
import android.os.Parcelable;
import android.content.Intent;
import android.util.Log;
/**
 * Created by raymondlian on 10/20/15.
 */
public class MovieObject implements Parcelable {
    public String savedTitle;
    public String savedDate;
    public String savedRating;
    public String savedPlot;
    public String savedURL;  //Saved URL is used for the posterPath
    public String savedId;

    public MovieObject(String title, String date, String rating, String plot, String id) {
        this.savedURL = null;
        this.savedTitle = title;
        this.savedDate = date;
        this.savedRating = rating;
        this.savedPlot = plot;
        this.savedId = id;

    }
    public MovieObject(String title, String date, String rating, String plot, String id, String url) {
        this.savedURL = url;
        this.savedTitle = title;
        this.savedDate = date;
        this.savedRating = rating;
        this.savedPlot = plot;
        this.savedId = id;

    }

    public MovieObject createFromParcel(Parcel in) {
        return new MovieObject(in);
    }


    public MovieObject(Parcel in) {
        savedURL = in.readString();
        savedTitle = in.readString();
        savedDate = in.readString();
        savedRating = in.readString();
        savedPlot = in.readString();
        savedId = in.readString();


    }

    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(savedURL);
        out.writeString(savedTitle);
        out.writeString(savedDate);
        out.writeString(savedRating);
        out.writeString(savedPlot);
        out.writeString(savedId);


    }

    public final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>() {
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };


}