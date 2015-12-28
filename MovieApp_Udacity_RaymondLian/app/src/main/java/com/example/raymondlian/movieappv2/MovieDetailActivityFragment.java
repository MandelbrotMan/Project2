package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class MovieDetailActivityFragment extends Fragment{
  // used to communicate between fragment and main activity
    OnMovieSelectedListener movieSelectedListener;



    //To be sent back if selected as favorite
    String ImageURLString; //For posterpath
    String MovieIdString;  //For pulling additional data of selected movie
    String Title;
    String Rating;
    String ReleaseDate;
    String Plot;
    boolean FavStatus;
    Toast MToast;


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






    public MovieDetailActivityFragment() {
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
//On create

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TrailerObject ex1 = new TrailerObject("xyz", "xvy");
        TrailerObject ex2 = new TrailerObject("xyz", "xvy");
        TrailerObject ex3 = new TrailerObject("xyz", "xvy");
        trailerObjects.add(ex1);
        trailerObjects.add(ex2);
        trailerObjects.add(ex3);

        view=inflater.inflate(R.layout.fragment_movie_detail, container,false);
        adapter = new TrailerAdapter(getContext(), trailerObjects);

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
            if (isNetworkAvailable() && recievedPackage != null) {
                ImageURLString = recievedPackage.getString("image");
                MovieIdString = recievedPackage.getString("id");
                Plot = recievedPackage.getString("synopsis");
                Title = recievedPackage.getString("title");
                ReleaseDate = recievedPackage.getString("release_date");
                Rating = recievedPackage.getString("vote_average");
                FavStatus = recievedPackage.getBoolean("favStatus");

                Movie = new MovieObject(Title, ReleaseDate, Rating, Plot, MovieIdString, ImageURLString);


            } else {

                titleView.setText("Connection lost");
            }
        }
        // If the item screen rotates
        else {
            Movie = savedInstanceState.getParcelable("movie");
            FavStatus = savedInstanceState.getBoolean("status");
            Title = Movie.savedTitle;
            ReleaseDate = Movie.savedDate;
            Rating = Movie.savedRating;
            MovieIdString = Movie.savedId;
            ImageURLString = Movie.savedURL;
            Plot = Movie.savedPlot;

        }
        //mCallback must be initialize with some value to prevent a void error
        if (FavStatus == true) {
            FavoriteButton.setBackgroundResource(R.drawable.stargold);

        } else {


            FavoriteButton.setBackgroundResource(R.drawable.starblack);
        }

        //Assigns values attained to UI
        titleView.setText(Title);
        dateView.setText(ReleaseDate);
        ratingView.setText(Rating);
        synopsisView.setText(Plot);




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

                if (FavStatus == false) {

                    CharSequence text = "Added!";
                    int duration = Toast.LENGTH_SHORT;
                    MToast = Toast.makeText(getActivity(), text, duration);
                    MToast.show();
                    FavStatus = true;
                    MoviePackage = new Bundle();
                    FavoriteButton.setBackgroundResource(R.drawable.stargold);

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
       // I = new Intent(getActivity(), Main2Activity.class);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable("movie", Movie);
        bundle.putBoolean("status", FavStatus);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);


    }




////////////////////////////////////////////////////////////////////////////////////////////
//Interface


    public void update(String title, String date, String rating, String plot, String id, String url, boolean status, ArrayList<TrailerObject> list){
       ImageURLString = url; //For posterpath
       MovieIdString = id;  //For pulling additional data of selected movie
       Title = title;
       Rating = rating;
       ReleaseDate = rating;
       Plot = plot;


        titleView.setText(title);
        dateView.setText(date);
        ratingView.setText(rating);
        synopsisView.setText(plot);
        Picasso.with(mContext).load(url).into(PosterView);

        addList(list);
        Log.v("Size" , Integer.toString(list.size()));


    }
    public interface OnMovieSelectedListener {
        void updateData(String title, String date, String rating, String plot, String id, String url, boolean status, ArrayList<TrailerObject> list);
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
           movieSelectedListener  = (OnMovieSelectedListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException(c
                    + " must implement movie selected listener");
        }
    }
    public void addList(ArrayList<TrailerObject> flist){
        flist.clear();

        //TrailerObject exa = new TrailerObject("ayz", "dvy");
        //TrailerObject exb = new TrailerObject("byz", "evy");
        TrailerObject ex3c = new TrailerObject("cyz", "fvy");
        //flist.add(exa);
        //flist.add(exb);
        flist.add(ex3c);


        if(adapter != null) {
            adapter.clear();
        }
        trailerObjects.clear();

        for(int i = 0; i < flist.size(); ++i){
                trailerObjects.add(flist.get(i));
        }
        for(int i = 0; i < trailerObjects.size(); ++i) {
            adapter.add(trailerObjects.get(i));

        }
        Log.v("Size of new list" , Integer.toString(trailerObjects.size()));
        adapter.notifyDataSetChanged();



    }

    ////////////////////////////////////////////////////////////////////////////////////////////
//Image tasks and async methods

    public class TrailerAdapter extends ArrayAdapter<TrailerObject> {
        public TrailerAdapter(Context context, ArrayList<TrailerObject> trailer) {
            super(getActivity(), 0, trailer);
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
