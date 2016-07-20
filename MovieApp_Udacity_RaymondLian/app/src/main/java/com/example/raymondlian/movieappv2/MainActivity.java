package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements  MovieDetailFragment.OnMovieSelectedListener{

    String ImageURLString = " "; //For posterpath
    String MovieIdString = " ";  //For pulling additional data of selected movie
    String Title = " ";
    String Rating = " ";
    String ReleaseDate = " ";
    String Plot= " ";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    boolean FavStatus = false;
    FragmentManager manager;
    MainActivityFragment fragmentMain;
    MovieDetailFragment fragmentDetail;
    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieDetailFragment fragment = new MovieDetailFragment();
        if(findViewById(R.id.movie_detail_container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new MovieDetailFragment())
                    .commit();


        }


    }
    public void switchToMovieDetail(){
        Bundle moviePackage = new Bundle();
        moviePackage.putString("title", Title);
        moviePackage.putString("image", ImageURLString);
        moviePackage.putString("release_date", ReleaseDate);
        moviePackage.putString("vote_average", Rating);
        moviePackage.putString("synopsis", Plot);
        moviePackage.putString("id", MovieIdString);
        moviePackage.putBoolean("favStatus", FavStatus);





    }


    @Override
    public void updateData(String titleS, String dateS, String ratingS, String plotS, String idS, String urlS, boolean statusS, ArrayList<TrailerObject> list){


    }



}
