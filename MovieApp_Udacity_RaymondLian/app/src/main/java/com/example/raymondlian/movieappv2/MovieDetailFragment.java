package com.example.raymondlian.movieappv2;

import android.app.LoaderManager;
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
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
  // used to communicate between fragment and main activity



    //To be sent back if selected as favorite
   public static String mImageURLString = "image"; //For posterpath
   public static String mMovieIdString = "id";  //For pulling additional data of selected movie
   public static String mTitle = "title";
   public static String mRating = "vote_average";
   public static String mReleaseDate = "release_date";
   public static String mPlot = "synopsis";;
   public static String mFavStatus = "true";


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
            MovieContract.TrailerEntry.COLUMN_TITLE};


    static final int COLUMN_ID = 0;
    static final int COLUMN_MOVIE_ID = 1;
    static final int COLUMN_LINK_URL = 2;
    static final int COLUMN_TITLE = 3;


    Cursor cursor;






    public MovieDetailFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);

        mAdapter = new TrailerAdapter(getActivity(), null, 0);
        ArrayList<String> myStringArray = new ArrayList<String>();

        ArrayAdapter adapter = new ArrayAdapter(this, R.id.list_item_trailer_textview, myStringArray)
        listView = (ListView) view.findViewById(R.id.trailerListView);
        Adapter adapter = new Adapter()

        listView.setAdapter(mAdapter);



        Bundle recievedPackage = this.getArguments();

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
                ratingView.setText(recievedPackage.getString(mRating));
                synopsisView.setText(recievedPackage.getString(mPlot));
                Picasso.with(mContext).load(recievedPackage.getString(mImageURLString)).into(PosterView);


            } else {


            }
        }
        // If the item screen rotates
        else {

        }
        //mCallback must be initialize with some value to prevent a void error
        if (mFavStatus.equals("true")) {
           FavoriteButton.setBackgroundResource(R.drawable.star_gold);

        } else {


           FavoriteButton.setBackgroundResource(R.drawable.star_black);
        }





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                startActivity(intent);

            }
        });


        FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cursor.moveToNext()){
                    mAdapter.swapCursor(cursor);

                }else{
                    Log.v("Trailer is empty", "no entry");
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
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                MovieContract.TrailerEntry.CONTENT_URI,
                TRAILER_PROJECTION,
                null,
                null,
                null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        Log.v("TrailerAdapter called ", "on load finished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }






}
