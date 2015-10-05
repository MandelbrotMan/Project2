package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MovieDetailActivity extends Activity {
    String imageURLString;
    String movieIdString;
    ImageView posterView;
    ArrayList<String> movieTrailers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Bundle recievedPackage = intent.getExtras();
        imageURLString = recievedPackage.getString("image");
        movieIdString = recievedPackage.getString("id");

        if(isNetworkAvailable()) {
            new imageTask().execute("");

            TextView titleView = (TextView) findViewById(R.id.movieTitleText);
            TextView dateView = (TextView) findViewById(R.id.releaseDateText);
            TextView ratingView = (TextView) findViewById(R.id.voteAverageText);
            TextView synopsisView = (TextView) findViewById(R.id.synopsisText);
            posterView = (ImageView) findViewById(R.id.posterImageView);


            titleView.setText(recievedPackage.getString("title"));
            dateView.setText(recievedPackage.getString("release_date"));
            ratingView.setText(recievedPackage.getString("vote_average"));
            synopsisView.setText(recievedPackage.getString("synopsis"));
        } else {
            TextView titleView = (TextView) findViewById(R.id.movieTitleText);
            titleView.setText("Connection lost");
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Home) {
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class imageTask extends AsyncTask<String, Void, Bitmap>{
        HttpURLConnection posterUrlConnection = null;


        protected Bitmap doInBackground(String... param){
            Bitmap bmp = null;
            try {
                URL posterURL = new URL(imageURLString);



                posterUrlConnection = (HttpURLConnection) posterURL.openConnection();
                posterUrlConnection.setRequestMethod("GET");
                posterUrlConnection.connect();

                 bmp = BitmapFactory.decodeStream(posterURL.openConnection().getInputStream());

                // posterView.setImageBitmap(bmp);

            } catch (IOException e){
                Log.e("Error", String.valueOf(e));
            } finally {
                if(posterUrlConnection != null){
                    posterUrlConnection.disconnect();;
                }
            }


            return bmp;
        }
        protected void onPostExecute(Bitmap image){
            posterView.setImageBitmap(image);
        }
        private void getTrailerJsonData() {
            HttpURLConnection trailerUrlConnection = null;

            Uri base = Uri.parse("https://api.themoviedb.org")
                    .buildUpon()
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieIdString)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", "0109ddff503db8186924929b1814320e").
                            appendQueryParameter("language", "en").
                            appendQueryParameter("include_image)langauge", "en, us").build();

            try {

                URL trailersURL = new URL(base.toString());
                trailerUrlConnection = (HttpURLConnection) trailersURL.openConnection();
                trailerUrlConnection.setRequestMethod("GET");
                trailerUrlConnection.connect();

            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } finally {
                if (trailerUrlConnection != null) {
                    trailerUrlConnection.disconnect();

                }
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}

/*
 */