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
import android.util.Log;
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

import java.util.ArrayList;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{

    //To be sent back if selected as favorite
   public static String mImageURLString = "image"; //For posterpath
   public static String mMovieIdString = "id";  //For pulling additional data of selected movie
   public static String mTitle = "title";
   public static String mRating = "vote_average";
   public static String mReleaseDate = "release_date";
   public static String mPlot = "synopsis";;
   public static String mFavStatus = "true";

    public static String Empty = "none";

    //Used for searching through the trailer table and adding favorites in the sqlite database;
    String SaveId = Empty;
    String SaveFavorite = "False";
    String SaveImage;
    String SaveTitle;
    String SaveRating;
    String SaveRelese;
    String SavePlot;

    private static final int DETAIL_LOADER = 0;

    //Used to send back MovieObject if Selected as favorite
    Bundle MoviePackage = null;

    ImageView PosterView;
    ListView listView;
    TextView titleView;
    TextView dateView;
    TextView ratingView;
    TextView synopsisView;
    Button FavoriteButton;
    Button ReviewButton;
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
            MovieContract.TrailerEntry.COLUMN_LINK_URL
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

    Cursor cursor;

    public MovieDetailFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle recievedPackage = this.getArguments();

        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);
        listView = (ListView) view.findViewById(R.id.trailerListView);


        //Connect UI variables with XML id's
        ReviewButton = (Button) view.findViewById(R.id.reviewButton);
        FavoriteButton = (Button) view.findViewById(R.id.favoriteButton);
        titleView = (TextView) view.findViewById(R.id.movieTitleText);
        dateView = (TextView) view.findViewById(R.id.releaseDateText);
        ratingView = (TextView) view.findViewById(R.id.voteAverageText);
        synopsisView = (TextView) view.findViewById(R.id.synopsisText);
        PosterView = (ImageView) view.findViewById(R.id.posterImageView);

        //When the fragment is opened. Used in phone. Initializes the variables for UI
        if(savedInstanceState == null) {
            if(recievedPackage != null) {
                //Assigns values attained to UI
              SaveId = recievedPackage.getString(mMovieIdString);



            }
        } else {
            SaveImage = savedInstanceState.getString(mImageURLString);
            SaveTitle = savedInstanceState.getString(mTitle); ;
            SaveRating = formatRating(savedInstanceState.getString(mRating));
            SaveRelese = savedInstanceState.getString(mReleaseDate);
            SavePlot =   savedInstanceState.getString(mPlot);
            SaveId = savedInstanceState.getString(mMovieIdString);
            SaveFavorite = savedInstanceState.getString(mFavStatus);
        }
        //mCallback must be initialize with some value to prevent a void error
        if (SaveFavorite.equals("False")) {
          FavoriteButton.setBackgroundResource(R.drawable.star_black);

        } else {
          FavoriteButton.setBackgroundResource(R.drawable.star_gold);
        }
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI, NOTIFY_TRAILER_PROJECTION,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[]{SaveId},
                null);
        if(cursor.moveToFirst()) {
            mAdapter = new TrailerAdapter(getActivity(), cursor, 0);
            listView.setAdapter(mAdapter);
        }





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    String hyperlink = cursor.getString(T_COLUMN_LINK_URL);
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(hyperlink)));

                }
            }
        });



        FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SaveId.equals(Empty)) {
                    String result = null;

                    Cursor favStatusMovie = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            new String[]{MovieContract.MovieEntry.COLUMN_FAV_STAT},
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{SaveId}, null);
                    if(favStatusMovie.moveToFirst()) {
                        result = favStatusMovie.getString(0);
                    }
                    ContentValues updateTrailer = new ContentValues();

                    ContentValues updateMovie = new ContentValues();



                    if (result.equals(MovieSyncAdapter.FALSE)) {

                        updateMovie.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, MovieSyncAdapter.TRUE);

                        updateTrailer.put(MovieContract.TrailerEntry.COLUMN_IS_FAVORITE, MovieSyncAdapter.TRUE);
                        FavoriteButton.setBackgroundResource(R.drawable.star_gold);
                    } else {
                        updateMovie.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, MovieSyncAdapter.FALSE);
                        updateTrailer.put(MovieContract.TrailerEntry.COLUMN_IS_FAVORITE, MovieSyncAdapter.FALSE);
                        FavoriteButton.setBackgroundResource(R.drawable.star_black);
                    }

                    getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, updateMovie,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{SaveId});
                    getActivity().getContentResolver().update(MovieContract.TrailerEntry.CONTENT_URI, updateTrailer,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[]{SaveId});

                }
            }
        });

        ReviewButton.setOnClickListener(new View.OnClickListener() {
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
        bundle.putString(mPlot, SavePlot);
        bundle.putString(mFavStatus, SaveFavorite);
        bundle.putString(mImageURLString,SaveImage);
        bundle.putString(mRating,SaveRating);
        bundle.putString(mReleaseDate,SaveRelese);
        bundle.putString(mTitle, SaveTitle);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle) {
        getLoaderManager().initLoader(0, null, this);
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
            titleView.setText(data.getString(M_COLUMN_TITLE));
            dateView.setText(data.getString(M_COLUMN_RELEASE_DATE));
            ratingView.setText(formatRating(data.getString(M_COLUMN_VOTE_AVERAGE)));
            synopsisView.setText(data.getString(M_COLUMN_SYNOPSIS));
            Picasso.with(mContext).load(data.getString(MainActivityFragment.COLUMN_IMG_URL)).into(PosterView);
            FavoriteButton.setVisibility(View.VISIBLE);
            ReviewButton.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
