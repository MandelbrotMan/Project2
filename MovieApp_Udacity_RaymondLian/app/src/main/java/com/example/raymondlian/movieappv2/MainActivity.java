package com.example.raymondlian.movieappv2;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;

public class MainActivity extends ActionBarActivity implements  MainActivityFragment.Callback{

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

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
    public void onItemSelected(String id) {
        Bundle toDetails = new Bundle();
        toDetails.putString(MovieDetailFragment.mMovieIdString, id);

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
