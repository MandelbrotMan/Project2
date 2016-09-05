package com.example.raymondlian.movieappv2;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raymondlian.movieappv2.SyncServices.MovieSyncAdapter;
import com.example.raymondlian.movieappv2.data.MovieContract;
import com.squareup.picasso.Picasso;

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
import android.widget.Button;
import android.widget.Toast;
import android.view.KeyEvent;
/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
  // used to communicate between fragment and main activity

    ArrayList<String> test = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    //To be sent back if selected as favorite
   public static String mImageURLString = "image"; //For posterpath
   public static String mMovieIdString = "id";  //For pulling additional data of selected movie
   public static String mTitle = "title";
   public static String mRating = "vote_average";
   public static String mReleaseDate = "release_date";
   public static String mPlot = "synopsis";;
   public static String mFavStatus = "true";

    //Used for searching through the trailer table and adding favorites in the sqlite database;
    String mQueryId = null;
    String mFavorite = "False";


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

    private static final String[] TRAILER_PROJECTION = new String[] {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_LINK_URL,
            MovieContract.TrailerEntry.COLUMN_TITLE,
            MovieContract.TrailerEntry.COLUMN_IS_FAVORITE
          };


    static final int COLUMN_ID = 0;
    static final int COLUMN_MOVIE_ID = 1;
    static final int COLUMN_LINK_URL = 2;
    static final int COLUMN_TITLE = 3;
    static final int COLUMN_IS_FAVORITE = 4;


    Cursor cursor;






    public MovieDetailFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle recievedPackage = this.getArguments();
        Cursor cursor = null;
        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);
        if(recievedPackage != null) {
            mQueryId = recievedPackage.getString(mMovieIdString);

            cursor = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,TRAILER_PROJECTION,
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",new String[]{mQueryId}, null);

        }

        mAdapter = new TrailerAdapter(getActivity(), cursor, 0);


        listView = (ListView) view.findViewById(R.id.trailerListView);


        listView.setAdapter(mAdapter);



        //Connect UI variables with XML id's
        ReviewButton = (Button) view.findViewById(R.id.reviewButton);
        FavoriteButton = (Button) view.findViewById(R.id.favoriteButton);
        titleView = (TextView) view.findViewById(R.id.movieTitleText);
        dateView = (TextView) view.findViewById(R.id.releaseDateText);
        ratingView = (TextView) view.findViewById(R.id.voteAverageText);
        synopsisView = (TextView) view.findViewById(R.id.synopsisText);
        PosterView = (ImageView) view.findViewById(R.id.posterImageView);




        cursor = getActivity().getContentResolver().
                query(MovieContract.TrailerEntry.CONTENT_URI, null,null,null,null);



        //When the fragment is opened. Used in phone. Initializes the variables for UI
        if(savedInstanceState == null) {
            if(recievedPackage != null) {
                //Assigns values attained to UI
                titleView.setText(recievedPackage.getString(mTitle));
                dateView.setText(recievedPackage.getString(mReleaseDate));
                ratingView.setText(formatRating(recievedPackage.getString(mRating)));
                synopsisView.setText(recievedPackage.getString(mPlot));
                Picasso.with(mContext).load(recievedPackage.getString(mImageURLString)).into(PosterView);
                mFavorite = recievedPackage.getString(mFavStatus);


            } else {


            }
        }
        // If the item screen rotates
        else {

        }
        //mCallback must be initialize with some value to prevent a void error
        if (mFavorite.equals("False")) {
          FavoriteButton.setBackgroundResource(R.drawable.star_black);

        } else {


          FavoriteButton.setBackgroundResource(R.drawable.star_gold);
        }





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    String hyperlink = cursor.getString(COLUMN_LINK_URL);
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(hyperlink)));

                }
            }
        });



        FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQueryId != null) {
                    String result = null;

                    Cursor favStatusMovie = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            new String[]{MovieContract.MovieEntry.COLUMN_FAV_STAT},
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{mQueryId}, null);
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
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mQueryId});
                    getActivity().getContentResolver().update(MovieContract.TrailerEntry.CONTENT_URI, updateTrailer,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mQueryId});

                }
            }
        });

        ReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intentReviewPage = new Intent(getActivity(), ReviewsActivity.class);
              //  intentReviewPage.putExtra("id", MovieIdString);


               // startActivity(intentReviewPage);
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
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);


    }
    public String formatRating(String rating){
        return rating + " out of 10";
    }









}
