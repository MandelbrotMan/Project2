package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements  MovieDetailFragment.OnMovieSelectedListener{

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    boolean FavStatus = false;
    FragmentManager manager;
    MainActivityFragment fragmentMain;
    MovieDetailFragment fragmentDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieDetailFragment fragment = new MovieDetailFragment();

        if(findViewById(R.id.movie_detail_container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new MovieDetailFragment())
                    .commit();
            mTwoPane = true;




        }else{
            mTwoPane = false;
        }
        MainActivityFragment mainFragment =  ((MainActivityFragment)getFragmentManager()
                .findFragmentById(R.id.fragment_main));
        mainFragment.setUILayout(mTwoPane);
        MovieSyncAdapter.initializeSyncAdapter(this);

    }




    @Override
    public void updateData(String titleS, String dateS, String ratingS, String plotS, String idS, String urlS, boolean statusS, ArrayList<TrailerObject> list){


    }



}
