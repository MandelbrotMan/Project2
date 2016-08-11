package com.example.raymondlian.movieappv2.SyncServices;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.raymondlian.movieappv2.MovieObject;
import com.example.raymondlian.movieappv2.R;
import com.example.raymondlian.movieappv2.data.MovieContract;

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
import java.util.Vector;

/**
 * Created by raymond on 8/7/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String queryType = extras.getString(getContext().getString(R.string.QueryType));


        String popularURL = getJsonURL(queryType);



        if (isNetworkAvailable()) {
            try {
                if (jsonArray == null) {
                }
                jsonArray = getJsonData(popularURL, 100);
                getBitMapURL(jsonArray);

            } catch (JSONException e) {
            } catch (IOException e) {

            }
        }


    }
    public String getJsonURL(String sort) {


        String JsonUrl = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader;

        InputStream stream;
        URL popularURL;


        Uri base = Uri.parse("https://api.themoviedb.org").buildUpon().
                appendPath("3").
                appendPath("movie").
                appendPath(sort).
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
    private String getBitMapURL(String urlId) throws IOException {
        HttpURLConnection posterUrlConnection = null;




            Uri builtUri = Uri.parse("http://image.tmdb.org").buildUpon()
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w500")
                    .appendPath(urlId).build();
            try {
                URL bitmapURL = new URL(builtUri.toString());


                posterUrlConnection = (HttpURLConnection) bitmapURL.openConnection();
                posterUrlConnection.setRequestMethod("GET");
                posterUrlConnection.connect();


                return builtUri.toString();


            } catch (IOException e) {
                return null;
            } finally {
                if (posterUrlConnection != null) {
                    posterUrlConnection.disconnect();
                }

            }
    }
    protected void getJsonData(String s, int top) throws JSONException {
        ArrayList<String> imagePathArray = new ArrayList<>();
        final String get_RESULTS = "results";

        final String get_PATH = "poster_path";
        final String get_AVERAGE = "vote_average";
        final String get_SYNOPSIS = "overview";
        final String get_RELEASE_DATE = "release_date";
        final String get_TITLE = "title";
        final String get_ID = "id";


        JSONObject popularJSON = new JSONObject(s);

        JSONArray movieArray = popularJSON.getJSONArray(get_RESULTS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        //Extracting details of movies from json
        for (int j = 0; j < movieArray.length(); j++) {
            JSONObject singleMovie = movieArray.getJSONObject(j);
            imagePathArray.add(singleMovie.getString(get_PATH).substring(1));


            ContentValues value = new ContentValues();
            test.put(MovieContract.MovieEntry.COLUMN_TITLE, MoviesListed.get(0).savedTitle);
            test.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MoviesListed.get(0).savedDate);
            test.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, MoviesListed.get(0).savedRating);
            test.put(MovieContract.MovieEntry.COLUMN_ID, MoviesListed.get(0).savedId);
            test.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, MoviesListed.get(0).savedPlot);
            test.put(MovieContract.MovieEntry.COLUMN_IMG_URL, MoviesListed.get(0).savedURL);
            test.put(MovieContract.MovieEntry.COLUMN_CURRENT_LIST, "True");
            test.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, "False");
            MovieObject temp = new MovieObject(singleMovie.getString(get_TITLE),
                    singleMovie.getString(get_RELEASE_DATE),
                    Integer.toString(singleMovie.getInt(get_AVERAGE)),
                    singleMovie.getString(get_SYNOPSIS), Integer.toString(singleMovie.getInt(get_ID)));


        }


        String[] convertedArray = new String[imagePathArray.size()];
        convertedArray = imagePathArray.toArray(convertedArray);
        return convertedArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}




}
