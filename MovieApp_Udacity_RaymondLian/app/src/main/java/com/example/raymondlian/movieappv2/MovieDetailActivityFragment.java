package com.example.raymondlian.movieappv2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    //To be sent back if selected as favorite
    String ImageURLString; //For posterpath
    String MovieIdString;  //For pulling additional data of selected movie
    String Title;
    String Rating;
    String ReleaseDate;
    String Plot;
    Toast MToast;


    //Used to send back MovieObject if Selected as favorite
    Bundle MoviePackage = null;
    MovieObject Movie;



    ImageView PosterView;
    ArrayAdapter<String> adapter;
    ListView listView;
    Button button;

    Context mContext = getContext();
    boolean FavStatus;

    ArrayList<String> trailerTitles = new ArrayList<>();

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_movie_detail, container);

        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        Bundle recievedPackage = intent.getExtras();

        button = (Button) inflater1.findViewById(R.id.favoriteButton);
        TextView titleView = (TextView) inflater1.findViewById(R.id.movieTitleText);
        TextView dateView = (TextView) inflater1.findViewById(R.id.releaseDateText);
        TextView ratingView = (TextView) inflater1.findViewById(R.id.voteAverageText);
        TextView synopsisView = (TextView) inflater1.findViewById(R.id.synopsisText);
        PosterView = (ImageView) inflater1.findViewById(R.id.posterImageView);


        if(savedInstanceState == null) {
            if (isNetworkAvailable()) {
                ImageURLString = recievedPackage.getString("image");
                MovieIdString = recievedPackage.getString("id");
                Plot = recievedPackage.getString("synopsis");
                Title = recievedPackage.getString("title");
                ReleaseDate = recievedPackage.getString("release_date");
                Rating = recievedPackage.getString("vote_average");
                FavStatus = recievedPackage.getBoolean("favStatus");

                Movie = new MovieObject(Title, ReleaseDate, Rating, Plot, MovieIdString, ImageURLString);

                new imageTask().execute("");

            } else {

                titleView.setText("Connection lost");
            }
        } else {
            Movie = savedInstanceState.getParcelable("movie");
            FavStatus = savedInstanceState.getBoolean("status");
            Title = Movie.savedTitle;
            ReleaseDate = Movie.savedDate;
            Rating = Movie.savedRating;
            MovieIdString = Movie.savedId;
            ImageURLString = Movie.savedURL;
            Plot = Movie.savedPlot;
            new imageTask().execute("");
        }
        if (FavStatus == true) {
            button.setBackgroundResource(R.drawable.stargold);
        } else {
            button.setBackgroundResource(R.drawable.starblack);
        }
        titleView.setText(Title);
        dateView.setText(ReleaseDate);
        ratingView.setText(Rating);
        synopsisView.setText(Plot);

        adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.trailer_item,
                R.id.list_item_trailer_textview,
                trailerTitles
        );

        listView = (ListView) inflater1.findViewById(R.id.trailerListView);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FavStatus == false){
                    CharSequence text = "Added!";
                    int duration = Toast.LENGTH_SHORT;
                    MToast = Toast.makeText(getContext(), text, duration);
                    MToast.show();
                    MoviePackage = new Bundle();
                    MovieObject newFavorite = new MovieObject(Title, ReleaseDate, Rating, Plot, MovieIdString, ImageURLString);
                   button.setBackgroundResource(R.drawable.stargold);
                    MoviePackage.putString("title", Title);
                    MoviePackage.putString("releaseDate", ReleaseDate);
                    MoviePackage.putString("rating", Rating);
                    MoviePackage.putString("plot", Plot);
                    MoviePackage.putString("movieIdString", MovieIdString);
                    MoviePackage.putString("imageURLString", ImageURLString);

                }




            }
        });



       return  inflater1;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_movie_detail, menu);
        super.onCreateOptionsMenu(menu, menuInflater);


    }


    /*
    @Override
    public void onBackPressed() {
        String data = mEditText.getText();
        Intent intent = new Intent();
        intent.putExtra("MyData", data);
        setResult(resultcode, intent);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Home) {

            final Intent i = new Intent(getActivity(), MainActivity.class);
            if(MoviePackage != null) {
                i.putExtras(MoviePackage);
            }
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class imageTask extends AsyncTask<String, Void, Void> {
        HttpURLConnection posterUrlConnection = null;


        protected Void doInBackground(String... param){
            Picasso.with(mContext).load(ImageURLString).into(PosterView);
            String movieTrailersUrl = getTrailerJsonURL();

            try {
                getTrailersJSON(movieTrailersUrl);
                Log.v("Size:", Integer.toString(trailerTitles.size()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;

        }
        protected void onPostExecute(){
            adapter.clear();
            String trailerName = "";
            for(int i = 0; i < trailerTitles.size(); ++i) {
                trailerName = trailerTitles.get(i);
                adapter.add(trailerName);
            }
            adapter.notifyDataSetChanged();



        }

        private String getTrailerJsonURL() {
            String JsonUrl = "";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            InputStream stream;
            URL popularURL;


            Uri base = Uri.parse("https://api.themoviedb.org").buildUpon().
                    appendPath("3").
                    appendPath("movie").
                    appendPath(MovieIdString).
                    appendPath("videos").
                    appendQueryParameter("api_key", "0109ddff503db8186924929b1814320e").
                    appendQueryParameter("language", "en").
                    appendQueryParameter("include_image)langauge", "en, us").build();


            try {
                popularURL = new URL(base.toString());

                urlConnection = (HttpURLConnection) popularURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();



                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonUrl = buffer.toString();


            } catch (IOException e) {
                Log.e("error", String.valueOf(e));
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return JsonUrl;

        }
        private void getTrailersJSON (String urlString)  throws JSONException {

            JSONObject trailersObject = new JSONObject(urlString);
            JSONArray trailerArray = trailersObject.getJSONArray("results");


            for(int i = 0; i < trailerArray.length(); ++i){
                JSONObject temp = trailerArray.getJSONObject(i);
                String stringTemp = temp.getString("name");
                trailerTitles.add(stringTemp);

            }



        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}
