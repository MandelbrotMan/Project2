package com.example.raymondlian.movieappv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class Detail_Activity extends ActionBarActivity {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_);
        MovieDetailFragment fragment = new MovieDetailFragment();
        Intent fromMain = getIntent();
        Bundle toFrag = fromMain.getExtras();
        fragment.setArguments(toFrag);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_detail_container, fragment)
                .commit();
    }

}
