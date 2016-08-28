package com.example.raymondlian.movieappv2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.UriMatcher;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;
import com.example.raymondlian.movieappv2.data.MovieContract;
import com.example.raymondlian.movieappv2.data.MovieProvider;

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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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
    private int mPosition = mPosterGridview.INVALID_POSITION;

    View mRoot;
    private boolean isTablet = false;
    static int CurrentList = 0; //0 if its MoviesListed, 1 if FavoriteMovies --used for item selection

    LinearLayout HeaderProgress;
    Bundle formMovieDetailPackage;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_IMG_URL
    };
    static final int COLUMN_TITLE = 0;
    static final int COLUMN_RELEASE_DATE = 1;
    static final int COLUMN_VOTE_AVERAGE = 2;
    static final int COLUMN_ID = 3;
    static final int COLUMN_SYNOPSIS = 4;
    static final int COLUMN_IMG_URL = 5;

    private static  final String [] NOTIFY_TRAILER_PROJECTION = new String[]{
            MovieContract.TrailerEntry.COLUMN_TITLE, MovieContract.TrailerEntry.COLUMN_LINK_URL,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID};

    static final int COLUMN_T_TITLE = 0;
    static final int COLUMN_T_URL = 1;
    static final int COLUMN_T_ID = 2;






    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_main_gridview, container, false);
        Cursor tempCursor = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null,null,null,null);
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
        getLoaderManager().initLoader(0, null, this);
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
            Cursor swapThis = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
            mPosterAdapter.swapCursor(swapThis);




        }
        if (id == R.id.action_ratingMenu
                ) {

            ContentValues value = new ContentValues();
            value.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, "Test title");
            value.put(MovieContract.TrailerEntry.COLUMN_LINK_URL,  "test id");
            value.put(MovieContract.TrailerEntry.COLUMN_TITLE, "test url");
            getActivity().getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, value);

            Cursor tempCursor = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null,null,null,null);
            if(tempCursor.moveToFirst()){
                Log.v("Insert was successful ", tempCursor.getString(COLUMN_T_ID));
            }
            UriMatcher sUriMatcher = MovieProvider.buildUriMatcher();

        }
        if (id == R.id.action_FavoriteMenu){


        }

        return super.onOptionsItemSelected(item);
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

    public void setUILayout(boolean type){

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                NOTIFY_MOVIE_PROJECTION,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       // mPosterAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mPosterGridview.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
