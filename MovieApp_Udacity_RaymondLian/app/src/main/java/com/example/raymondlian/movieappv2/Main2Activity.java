package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Main2Activity extends Activity implements MovieDetailActivityFragment.OnFavoriteSelectedListener{
    String ImageURLString; //For posterpath
    String MovieIdString;  //For pulling additional data of selected movie
    String Title;
    String Rating;
    String ReleaseDate;
    String Plot;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Main2ActivityFragment fragment = new Main2ActivityFragment();
        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container_main, fragment, "Gridview");
        transaction.commit();


    }
    /*

    @Override
    public void onFavoriteSelected(String title, String date, String rating, String plot, String id, String url) {
        Title = title;
        Rating = rating;
        ImageURLString = url;
        MovieIdString = id;
        ReleaseDate = date;
        Plot = plot;
    }*/
    public void switchToMovieDetail(){
        MovieDetailActivityFragment fragment1 = new MovieDetailActivityFragment();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container_main, fragment1);
        transaction.commit();



    }

    public void onFavoriteSelected(String title, String date, String rating, String plot, String id, String url){

    }


}
