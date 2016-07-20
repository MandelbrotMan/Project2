package com.example.raymondlian.movieappv2;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;


public class Detail_Activity extends ActionBarActivity {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_detail_container, new MovieDetailFragment())
                .commit();

    }

}
