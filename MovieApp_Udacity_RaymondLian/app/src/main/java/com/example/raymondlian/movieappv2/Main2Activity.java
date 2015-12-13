package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Main2Activity extends Activity implements MovieDetailActivityFragment.OnFavoriteSelectedListener, Main2ActivityFragment.OnMovieSelectedListener{
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
    public void switchToMovieDetail(){
        Bundle moviePackage = new Bundle();
        moviePackage.putString("title", Title);
        moviePackage.putString("image", ImageURLString);
        moviePackage.putString("release_date", ReleaseDate);
        moviePackage.putString("vote_average", Rating);
        moviePackage.putString("synopsis", Plot);
        moviePackage.putString("id", MovieIdString);
        moviePackage.putBoolean("favStatus", true);

        MovieDetailActivityFragment fragment1 = new MovieDetailActivityFragment();
        fragment1.setArguments(moviePackage);
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container_main, fragment1);

        transaction.commit();



    }
    @Override
    public void onFavoriteSelected(String title, String date, String rating, String plot, String id, String url){
        ImageURLString  = url;
        MovieIdString = id;
        Title = title;
        ReleaseDate = date;
        Rating = rating;
        Plot = plot;
    }
    @Override
    public void onMovieSelected(String titleS, String dateS, String ratingS, String plotS, String idS, String urlS){
        ImageURLString  = urlS;
        MovieIdString = idS;
        Title = titleS;
        ReleaseDate = dateS;
        Rating = ratingS;
        Plot = plotS;
    }


}
