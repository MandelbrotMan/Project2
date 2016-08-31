package com.example.raymondlian.movieappv2;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
public class MovieDetailFragment extends Fragment{
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

    MovieObject Movie; //For on rotation saveInstance



    ImageView PosterView;
    TrailerAdapter adapter;
    ListView listView;
    TextView titleView;
    TextView dateView;
    TextView ratingView;
    TextView synopsisView;
    Button FavoriteButton;
    Button ReviewButton;
    Context mContext = getActivity();
    View view;

    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();






    public MovieDetailFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);
        adapter = new TrailerAdapter(getActivity(), trailerObjects);

        listView = (ListView) view.findViewById(R.id.trailerListView);

        listView.setAdapter(adapter);
        super.onCreate(savedInstanceState);


        Bundle recievedPackage = this.getArguments();

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
            Movie = savedInstanceState.getParcelable("movie");
            mFavStatus = savedInstanceState.getParcelable("status");
            mTitle = Movie.savedTitle;
            mReleaseDate = Movie.savedDate;
            mRating = Movie.savedRating;
            mMovieIdString = Movie.savedId;
            mImageURLString = Movie.savedURL;
            mPlot = Movie.savedPlot;

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
                intent.setData(Uri.parse(trailerObjects.get(position).trailer_url));
                startActivity(intent);

            }
        });


        FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                if (FavStatus == false) {

                    CharSequence text = "Added!";
                    int duration = Toast.LENGTH_SHORT;
                    FavStatus = true;
                    MoviePackage = new Bundle();
                   FavoriteButton.setBackgroundResource(R.drawable.star_gold);

                }
*/


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
        bundle.putParcelable("movie", Movie);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);


    }




////////////////////////////////////////////////////////////////////////////////////////////
//Interface




    public void addList(ArrayList<TrailerObject> flist){

        for(int i = 0; i < flist.size(); ++i){
                trailerObjects.add(flist.get(i));
            adapter.notifyDataSetChanged();
        }
        for(int i = 0; i < trailerObjects.size(); ++i) {
            adapter.add(trailerObjects.get(i));

        }



    }

    ////////////////////////////////////////////////////////////////////////////////////////////
//Image tasks and async methods

    public class TrailerAdapter extends ArrayAdapter<TrailerObject> {
        public TrailerAdapter(Context context, ArrayList<TrailerObject> trailer) {
            super(context, 0, trailer);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TrailerObject item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
            }
            // Lookup view for data population
            TextView Name = (TextView) convertView.findViewById(R.id.list_item_trailer_textview);
            // Populate the data into the template view using the data object
            Name.setText((CharSequence) item.trailer_title);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}
