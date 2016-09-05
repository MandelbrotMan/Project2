package com.example.raymondlian.movieappv2.SyncServices;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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
import java.util.Vector;

/**
 * Created by raymond on 8/7/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    static Context mContext;
    public static final String SEARCH_POPULAR = "popular";
    public static final String SEARCH_TOP_RATED = "top_rated";
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    public static final int SYNC_INTERVAL = 60 * 60 * 24; //everyday
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String queryType = extras.getString(getContext().getString(R.string.QueryType));
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
        getContext().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI,null,null);


        String popularURL = getJsonURL(SEARCH_POPULAR);
        String topRatedURL = getJsonURL(SEARCH_TOP_RATED);

        if (isNetworkAvailable()) {
            try {
               getJsonData(popularURL, SEARCH_POPULAR);

            } catch (JSONException e) {
                e.printStackTrace();
            }try {
                getJsonData(topRatedURL, SEARCH_TOP_RATED);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    /*
    Pulling movie objects from db
     */
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

    //Each json object returns a id required to make the url for the posterviews
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
    protected void getJsonData(String url, String searchType) throws JSONException {
        final String get_RESULTS = "results";
        final String get_PATH = "poster_path";
        final String get_AVERAGE = "vote_average";
        final String get_SYNOPSIS = "overview";
        final String get_RELEASE_DATE = "release_date";
        final String get_TITLE = "title";
        final String get_ID = "id";

        Log.v("Trailers array List", url);
        JSONObject popularJSON = new JSONObject(url);

        JSONArray movieArray = popularJSON.getJSONArray(get_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        //Extracting details of movies from json
        for (int j = 0; j < movieArray.length(); j++) {
            JSONObject singleMovie = movieArray.getJSONObject(j);
            String urlBitmap = null;
            try {
                urlBitmap = getBitMapURL(singleMovie.getString(get_PATH).substring(1));

            }catch (IOException e){

            }


            ContentValues value = new ContentValues();
            value.put(MovieContract.MovieEntry.COLUMN_TITLE, singleMovie.getString(get_TITLE));
            value.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,  singleMovie.getString(get_RELEASE_DATE));
            value.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, Integer.toString(singleMovie.getInt(get_AVERAGE)));

            //Getting trailers is performed in this stage
            String id = Integer.toString(singleMovie.getInt(get_ID));
            value.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            getTrailersJSON( getTrailerJsonURL(id), id);


            value.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, singleMovie.getString(get_SYNOPSIS));
            value.put(MovieContract.MovieEntry.COLUMN_IMG_URL, urlBitmap);
            value.put(MovieContract.MovieEntry.COLUMN_LIST_TYPE, searchType);
            value.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, FALSE);
            cVVector.add(value);

        }
        ContentValues values[] = cVVector.toArray(new ContentValues[cVVector.size()]);
        int count = 0;
        count = cVVector.size();
        //Where insertion in the table is made
        getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,values);


    }
    //Trailers are created after the movie database is created
    private String getTrailerJsonURL(String trailerId) {
        String JsonUrl = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader;

        InputStream stream;
        URL popularURL;


        Uri base = Uri.parse("https://api.themoviedb.org").buildUpon().
                appendPath("3").
                appendPath("movie").
                appendPath(trailerId).
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
    private void getTrailersJSON(String urlString, String id) throws JSONException {

        JSONObject trailersObject = new JSONObject(urlString);
        JSONArray trailerArray = trailersObject.getJSONArray("results");
        Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

        for (int i = 0; i < trailerArray.length(); ++i) {
            JSONObject temp = trailerArray.getJSONObject(i);
            String trailerLink = null;

            Uri base = Uri.parse("https://youtube.com").buildUpon().
                    appendPath("watch").
                    appendQueryParameter("v", temp.getString("key")).build();

            HttpURLConnection urlConnection = null;

            try {
                URL trailerURL = new URL(base.toString());

                urlConnection = (HttpURLConnection) trailerURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

            } catch (IOException e) {
                Log.e("error", String.valueOf(e));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    trailerLink = base.toString();
                }
            }
            ContentValues trailer = new ContentValues();
            trailer.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, id);
            trailer.put(MovieContract.TrailerEntry.COLUMN_TITLE, temp.getString("name"));
            trailer.put(MovieContract.TrailerEntry.COLUMN_LINK_URL, trailerLink);
            trailer.put(MovieContract.TrailerEntry.COLUMN_IS_FAVORITE, FALSE);
            cVVector.add(trailer);


        }
        ContentValues values[] = cVVector.toArray(new ContentValues[cVVector.size()]);
        getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, values);


    }
















    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    /*
    Sync Related functions
     */
    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(null, context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(null,
                    null, new Bundle(), syncInterval);
        }
    }
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }





}





