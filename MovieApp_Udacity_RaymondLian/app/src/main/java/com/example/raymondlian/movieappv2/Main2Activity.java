package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Main2Activity extends Activity implements MovieDetailActivityFragment.OnFavoriteSelectedListener {
    String ImageURLString; //For posterpath
    String MovieIdString;  //For pulling additional data of selected movie
    String Title;
    String Rating;
    String ReleaseDate;
    String Plot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    @Override
    public void onFavoriteSelected(String title, String date, String rating, String plot, String id, String url) {
        Title = title;
        Rating = rating;
        ImageURLString = url;
        MovieIdString = id;
        ReleaseDate = date;
        Plot = plot;
    }



}
