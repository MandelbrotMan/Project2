package com.example.raymondlian.movieappv2;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;
import com.example.raymondlian.movieappv2.data.MovieContract;
import com.squareup.picasso.Picasso;

import android.widget.Button;

/*
 */
public class MovieDetailFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{


   public static String mMovieIdString = "id";  //For pulling additional data of selected movie
    public static String Empty = "none";

    //Used for searching through the trailer table and adding favorites in the sqlite database;
    String SaveId = Empty;
    private static final int DETAIL_LOADER = 0;

    // U for ui elements
    ImageView uPosterView;
    ListView uListView;
    TextView uTitleView;
    TextView uDateView;
    TextView uRatingView;
    TextView uPlotView;
    Button uFavoriteButton;
    Button uReviewButton;
    Context mContext = getActivity();
    View view;

    TrailerAdapter mAdapter;

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
    private static final String[] NOTIFY_TRAILER_PROJECTION = new String[] {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_LINK_URL,
            MovieContract.TrailerEntry.COLUMN_TITLE
            };

    static final int T_COLUMN_ID = 0;
    static final int T_COLUMN_MOVIE_ID = 1;
    static final int T_COLUMN_LINK_URL = 2;
    static final int T_COLUMN_TITLE = 3;

    static final int M_COLUMN_ID = 0;
    static final int M_COLUMN_TITLE = 1;
    static final int M_COLUMN_RELEASE_DATE = 2;
    static final int M_COLUMN_VOTE_AVERAGE = 3;
    static final int M_COLUMN_ID_MOVIE = 4;
    static final int M_COLUMN_SYNOPSIS = 5;
    static final int M_COLUMN_IMG_URL = 6;
    static final int M_COLUMN_FAV_STAT = 7;


    public MovieDetailFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle recievedPackage = this.getArguments();
        if (recievedPackage != null) {
            SaveId = recievedPackage.getString(mMovieIdString); // Received from The poster gridview Fragment
        }

        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);
        uListView = (ListView) view.findViewById(R.id.trailerListView);


        //Connect UI variables with XML id's
        uReviewButton = (Button) view.findViewById(R.id.reviewButton);
        uFavoriteButton = (Button) view.findViewById(R.id.favoriteButton);
        uTitleView = (TextView) view.findViewById(R.id.movieTitleText);
        uDateView = (TextView) view.findViewById(R.id.releaseDateText);
        uRatingView = (TextView) view.findViewById(R.id.voteAverageText);
        uPlotView = (TextView) view.findViewById(R.id.synopsisText);
        uPosterView = (ImageView) view.findViewById(R.id.posterImageView);

        mAdapter = new TrailerAdapter(getActivity(), null, 0);
        uListView.setAdapter(mAdapter);
        uListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    String hyperlink = cursor.getString(T_COLUMN_LINK_URL);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hyperlink)));

                }
            }
        });



        uFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SaveId.equals(Empty)) {
                    String result = null;

                    //This makes sure that the only entry saved as a favorite is always the first entry of a movie.
                    //During each new call in the sync adapter everything that isnt marked as a favorite is saved
                    //A favorite a entry can then have a second copy if the api gives us back the same movie
                    String firstEntryId = null;
                    Cursor favStatusMovie = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_FAV_STAT},
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{SaveId}, null);
                    if (favStatusMovie.moveToFirst()) {
                        result = favStatusMovie.getString(1);
                        firstEntryId = favStatusMovie.getString(0);
                    }
                    ContentValues updateTrailer = new ContentValues();

                    ContentValues updateMovie = new ContentValues();


                    if (result.equals(MovieSyncAdapter.FALSE)) {
                        updateMovie.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, MovieSyncAdapter.TRUE);
                        updateTrailer.put(MovieContract.TrailerEntry.COLUMN_IS_FAVORITE, MovieSyncAdapter.TRUE);
                        uFavoriteButton.setBackgroundResource(R.drawable.star_gold);

                    } else {
                        updateMovie.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, MovieSyncAdapter.FALSE);
                        updateTrailer.put(MovieContract.TrailerEntry.COLUMN_IS_FAVORITE, MovieSyncAdapter.FALSE);
                        uFavoriteButton.setBackgroundResource(R.drawable.star_black);
                    }

                    getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateMovie,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{SaveId});
                    getActivity().getContentResolver().update(MovieContract.TrailerEntry.CONTENT_URI, updateTrailer,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[]{SaveId});

                }
            }
        });

        uReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SaveId != null) {
                    Intent intentReviewPage = new Intent(getActivity(), Reviews_Activity.class);
                    intentReviewPage.putExtra("id", SaveId);
                    startActivity(intentReviewPage);
                }
            }
        });




       return  view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(mMovieIdString, SaveId);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(bundle);
    }

    public String formatRating(String rating) {
        return rating + " out of 10";
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                NOTIFY_MOVIE_PROJECTION,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{SaveId},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            SaveId = data.getString(M_COLUMN_ID_MOVIE);
            String favStatus = data.getString(M_COLUMN_FAV_STAT);
            if(favStatus.equals(MovieSyncAdapter.TRUE)){
                uFavoriteButton.setBackgroundResource(R.drawable.star_gold);
            }else{
                uFavoriteButton.setBackgroundResource(R.drawable.star_black);
            }

            Cursor TrailerCursor;
                TrailerCursor = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, NOTIFY_TRAILER_PROJECTION,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{SaveId}, null);
            mAdapter.swapCursor(TrailerCursor);


            //Filling the UI with content
            uTitleView.setText(data.getString(M_COLUMN_TITLE));
            uDateView.setText(data.getString(M_COLUMN_RELEASE_DATE));
            uRatingView.setText(formatRating(data.getString(M_COLUMN_VOTE_AVERAGE)));
            uPlotView.setText(data.getString(M_COLUMN_SYNOPSIS));
            Picasso.with(mContext).load(data.getString(MovieDetailFragment.M_COLUMN_IMG_URL)).into(uPosterView);
            uFavoriteButton.setVisibility(View.VISIBLE);
            uReviewButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
