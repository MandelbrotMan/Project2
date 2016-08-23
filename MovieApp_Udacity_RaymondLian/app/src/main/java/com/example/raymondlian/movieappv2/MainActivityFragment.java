package com.example.raymondlian.movieappv2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;
import com.example.raymondlian.movieappv2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //Global variables start with Capital letters
    ArrayList<MovieObject> MoviesListed = new ArrayList<MovieObject>();
    static ArrayList<MovieObject> FavoriteMovies = new ArrayList<MovieObject>();
    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();

    MovieDetailFragment.OnMovieSelectedListener communicator;
    int positionSelected;

    FragmentManager manager;

    MovieAdapter mPosterAdapter;
    GridView mPosterGridview;
    TextView mListTitle;

    View mRoot;
    private boolean isTablet = false;
    static int CurrentList = 0; //0 if its MoviesListed, 1 if FavoriteMovies --used for item selection

    LinearLayout HeaderProgress;
    Bundle formMovieDetailPackage;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_IMG_URL
    };
    static final int COLUMN_TITLE = 0;
    static final int COLUMN_RELEASE_DATE = 1;
    static final int COLUMN_VOTE_AVERAGE = 2;
    static final int COLUMN_ID = 3;
    static final int COLUMN_SYNOPSIS = 4;
    static final int COLUMN_IMG_URL = 5;






    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_main_gridview, container, false);
        mPosterAdapter = new MovieAdapter(getActivity(), null, 0);
        mPosterAdapter.setUseTalbletLayout(isTablet);

        getActivity().setTitle("Blue Ray Movies");
        HeaderProgress = (LinearLayout) mRoot.findViewById(R.id.ProgressBarLayout);

        mPosterGridview = (GridView) mRoot.findViewById(R.id.gridview);
        mListTitle = (TextView) mRoot.findViewById(R.id.textView);
        mPosterGridview.setAdapter(mPosterAdapter);


        Intent intent = getActivity().getIntent();
        formMovieDetailPackage = intent.getExtras();








        //For preserving screen data during screen rotation
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
          //  new DownloadImageTask((GridView) mRoot.findViewById(R.id.gridview)).execute("popular");


        }
        mPosterGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                positionSelected = position;
                Intent intent1 = new Intent(getActivity(), Detail_Activity.class);
                startActivity(intent1);


            }
        });

        setHasOptionsMenu(true);
        return mRoot;

    }
    @Override
    public  void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        communicator = (MovieDetailFragment.OnMovieSelectedListener) getActivity();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.menu_main, menu);


    }
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popularityMenu
                ) {

            MovieSyncAdapter.syncImmediately(getActivity());




        }
        if (id == R.id.action_ratingMenu
                ) {



        }
        if (id == R.id.action_FavoriteMenu){


        }

        return super.onOptionsItemSelected(item);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, ArrayList<MovieObject>> {
        GridView bmImage;
        String listInfo = "";

        public DownloadImageTask(GridView bmImage) {
            this.bmImage = bmImage;
        }

        protected ArrayList<MovieObject> doInBackground(String... urls) {
            //Clear to prevent multiple copies in arrays
            MoviesListed.clear();
            CurrentList = 0;

            if (urls[0] == "popular") {
                listInfo = "Most Popular";
            } else if (urls[0] == "top_rated") {
                listInfo = "Top Rated";
            }


            return MoviesListed;
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
           // HeaderProgress.setVisibility(View.VISIBLE);
            //setProgressBarIndeterminateVisibility(true);
        }


        protected void onPostExecute(ArrayList<MovieObject> movies) {
/*
            JsonAdapter.notifyDataSetChanged();
            Gridview.setAdapter(JsonAdapter);
            ListTitle.setText(listInfo);
            Context context = getActivity();
            HeaderProgress.setVisibility(View.GONE);
*/

        }







        /*
        private class trailerTask extends AsyncTask<String, Void, ArrayList<TrailerObject>> {
            HttpURLConnection posterUrlConnection = null;


            protected ArrayList<TrailerObject> doInBackground(String... param) {
                trailerObjects.clear(); //Ensure there are no trailers from previous tasks
                String movieTrailersUrl = getTrailerJsonURL(param[0]); //Set up URL for pulling the JSON Data


                try {
                    getTrailersJSON(movieTrailersUrl); // Fill up the trailerObjects list with trailers
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;

            }


        }
        */
    }
    public void setUILayout(boolean type){

    }


}
