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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class MovieDetailActivity extends Activity {
    String ImageURLString;
    String MovieIdString;
    ImageView PosterView;
    ArrayAdapter<String> adapter;
    ListView listView;
    int size;

    ArrayList<String> trailerTitles = new ArrayList<>();

    ArrayList<String> movieTrailers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Bundle recievedPackage = intent.getExtras();
        ImageURLString = recievedPackage.getString("image");
        MovieIdString = recievedPackage.getString("id");



        if(isNetworkAvailable()) {
            new imageTask().execute("");

            TextView titleView = (TextView) findViewById(R.id.movieTitleText);
            TextView dateView = (TextView) findViewById(R.id.releaseDateText);
            TextView ratingView = (TextView) findViewById(R.id.voteAverageText);
            TextView synopsisView = (TextView) findViewById(R.id.synopsisText);
            PosterView = (ImageView) findViewById(R.id.posterImageView);


            titleView.setText(recievedPackage.getString("title"));
            dateView.setText(recievedPackage.getString("release_date"));
            ratingView.setText(recievedPackage.getString("vote_average"));
            synopsisView.setText(recievedPackage.getString("synopsis"));
        } else {
            TextView titleView = (TextView) findViewById(R.id.movieTitleText);
            titleView.setText("Connection lost");
        }
        ArrayList<String> forecast = new ArrayList<>();
        forecast.add("Today - Sunny - 88 / 63");
        forecast.add("Tomorrow - Foggy - 70 / 46");
        forecast.add("Weds - Cloudy - 72 / 63");
        forecast.add("Thurs - Rainy - 64 / 51");
        forecast.add("Fri - Foggy - 70 / 46");
        forecast.add("Sat - Sunny - 76 / 68");

        adapter = new ArrayAdapter<String>(
                this,
                R.layout.trailer_item,
                R.id.trailerListText,
                trailerTitles
        );

        listView = (ListView) findViewById(R.id.trailerListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });


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
                URL posterURL = new URL(ImageURLString);
                posterUrlConnection = (HttpURLConnection) posterURL.openConnection();
                posterUrlConnection.setRequestMethod("GET");
                posterUrlConnection.connect();

                String url = getTrailerJsonURL();
                try {
                    getTrailersJSON(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
            PosterView.setImageBitmap(image);
            listView.setAdapter(adapter);
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, Integer.toString(size), duration);
            toast.show();

        }

        private String getTrailerJsonURL() {
            HttpURLConnection trailerUrlConnection = null;
            URL trailersUrl = null;



            Uri base = Uri.parse("https://api.themoviedb.org")
                    .buildUpon()
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(MovieIdString)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", "0109ddff503db8186924929b1814320e").
                            appendQueryParameter("language", "en").
                            appendQueryParameter("include_image)langauge", "en, us").build();

            try {

               trailersUrl = new URL(base.toString());
                trailerUrlConnection = (HttpURLConnection) trailersUrl.openConnection();
                trailerUrlConnection.setRequestMethod("GET");
                trailerUrlConnection.connect();


            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } finally {
                if (trailerUrlConnection != null) {
                    trailerUrlConnection.disconnect();

                }
            }
            String trailerURLString = trailersUrl.toString();
            return trailerURLString;

        }
        private void getTrailersJSON (String urlString)  throws JSONException {

                JSONObject trailersObject = new JSONObject(urlString);
                 JSONArray trailerArray = trailersObject.getJSONArray("results");

                size = trailerArray.length();
                for(int i = 0; i < trailerArray.length(); i++){
                    JSONObject temp = trailerArray.getJSONObject(i);
                    String stringTemp = temp.getString("name").substring(1);
                    trailerTitles.add(stringTemp);

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