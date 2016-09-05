package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;
import com.example.raymondlian.movieappv2.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //Global variables start with Capital letters



    MovieAdapter mPosterAdapter;
    GridView mPosterGridview;
    TextView mListTitle;
    private int mPosition = mPosterGridview.INVALID_POSITION;

    View mRoot;
    private boolean isTablet = false;
    static int CurrentList = 0; //0 if its MoviesListed, 1 if FavoriteMovies --used for item selection

    LinearLayout HeaderProgress;
    Bundle formMovieDetailPackage;
    Callback mCallback;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_IMG_URL,
            MovieContract.MovieEntry.COLUMN_FAV_STAT

    };
    static final int COLUMN_ID = 0;
    static final int COLUMN_TITLE = 1;
    static final int COLUMN_RELEASE_DATE = 2;
    static final int COLUMN_VOTE_AVERAGE = 3;
    static final int COLUMN_ID_MOVIE = 4;
    static final int COLUMN_SYNOPSIS = 5;
    static final int COLUMN_IMG_URL = 6;
    static final int COLUMN_FAV_STAT = 7;



    private static  final String [] NOTIFY_TRAILER_PROJECTION = new String[]{
            MovieContract.TrailerEntry.COLUMN_TITLE, MovieContract.TrailerEntry.COLUMN_LINK_URL,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID};

    static final int COLUMN_T_ID = 0;
    static final int COLUMN_T_TITLE = 1;
    static final int COLUMN_T_URL = 2;
    static final int COLUMN_T_ID_TRAILERS = 3;






    public MainActivityFragment() {
    }
    public interface Callback {
        public void onItemSelected(String moviePosterURL, String title, String releaseDate, String voteAvg, String synopsis, String favStatus, String id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_main_gridview, container, false);

        mPosterAdapter = new MovieAdapter(getActivity(), null, 0);
        mPosterAdapter.setUseTalbletLayout(isTablet);

        getActivity().setTitle("Blue Ray Movies");

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
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    /*
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                    */
                    //(String moviePosterURL, String title, String releaseDate, String voteAvg, String synopsis)
                    mCallback.onItemSelected(cursor.getString(COLUMN_IMG_URL),cursor.getString(COLUMN_TITLE),
                            cursor.getString(COLUMN_RELEASE_DATE),cursor.getString(COLUMN_VOTE_AVERAGE),
                            cursor.getString(COLUMN_SYNOPSIS), cursor.getString(COLUMN_FAV_STAT),
                            cursor.getString(COLUMN_ID_MOVIE));
                    mPosition = position;
                    Log.v("Favorite status ", cursor.getString(COLUMN_FAV_STAT));
                }
            }
        });


        setHasOptionsMenu(true);
        return mRoot;

    }
    @Override
    public  void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
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

            Cursor swapThis = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,NOTIFY_MOVIE_PROJECTION,
                    MovieContract.MovieEntry.COLUMN_LIST_TYPE + " = ?",new String[]{MovieSyncAdapter.SEARCH_POPULAR}, null);
            mPosterAdapter.swapCursor(swapThis);
            mListTitle.setText(MovieSyncAdapter.SEARCH_POPULAR);




        }
        if (id == R.id.action_ratingMenu
                ) {
            Cursor swapThis = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,NOTIFY_MOVIE_PROJECTION,
                    MovieContract.MovieEntry.COLUMN_LIST_TYPE + " = ?",new String[]{MovieSyncAdapter.SEARCH_TOP_RATED}, null);
            mPosterAdapter.swapCursor(swapThis);
            mListTitle.setText(MovieSyncAdapter.SEARCH_TOP_RATED);


        }
        if (id == R.id.action_FavoriteMenu){
            Cursor swapThis = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,NOTIFY_MOVIE_PROJECTION,
                    MovieContract.MovieEntry.COLUMN_FAV_STAT + " = ?",new String[]{MovieSyncAdapter.TRUE}, null);
            mPosterAdapter.swapCursor(swapThis);
            mListTitle.setText("Favorites");


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
        mPosterAdapter.setUseTalbletLayout(type);
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
        mPosterAdapter.swapCursor(data);
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
