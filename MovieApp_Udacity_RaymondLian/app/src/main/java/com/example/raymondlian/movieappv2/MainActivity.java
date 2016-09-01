package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements  MainActivityFragment.Callback{

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
    public void onItemSelected(String moviePosterURL, String title, String releaseDate, String voteAvg, String synopsis, String favStatus, String id) {
        Bundle toDetails = new Bundle();
        toDetails.putString(MovieDetailFragment.mPlot, synopsis);
        toDetails.putString(MovieDetailFragment.mTitle, title);
        toDetails.putString(MovieDetailFragment.mReleaseDate, releaseDate);
        toDetails.putString(MovieDetailFragment.mRating, voteAvg);
        toDetails.putString(MovieDetailFragment.mImageURLString, moviePosterURL);
        toDetails.putString(MovieDetailFragment.mMovieIdString, id);
        toDetails.putString(MovieDetailFragment.mFavStatus, favStatus);
        if(mTwoPane) {

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(toDetails);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG).commit();
        }else{
            Intent intent = new Intent(this, Detail_Activity.class);
            intent.putExtras(toDetails);
            startActivity(intent);

        }
    }
}
