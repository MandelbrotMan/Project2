package com.example.raymondlian.movieappv2;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.ArrayAdapter;

import static com.squareup.picasso.Picasso.LoadedFrom.MEMORY;

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
import android.widget.ListView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewsActivityFragment extends Fragment {
    static final String arrayKey = "id";
    ArrayList<String> Reviews = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String MovieId;


    public ReviewsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = (View) inflater.inflate(R.layout.fragment_reviews, container, false);
        ListView listView = (ListView) root.findViewById(R.id.ReviewsListView);

        if(savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            Bundle recieved = intent.getExtras();
            MovieId = recieved.getString("id");
            new LoadReviews().execute("");
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, Reviews);
            listView.setAdapter(adapter);
        } else {
            Reviews.clear();
            Reviews = savedInstanceState.getStringArrayList(arrayKey);
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.review_item_textview, Reviews);
            listView.setAdapter(adapter);
        }
        if(Reviews.size() < 1){
            Toast.makeText(getActivity(), "No reviews found", Toast.LENGTH_LONG);
        }


        return root;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_movie_detail, menu);
        super.onCreateOptionsMenu(menu, menuInflater);


    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putStringArrayList(arrayKey, Reviews);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadReviews extends AsyncTask<String, Void, Void> {
        HttpURLConnection posterUrlConnection = null;


        protected Void doInBackground(String... param) {
            String movieTrailersUrl = getTrailerJsonURL();


            try {
                getTrailersJSON(movieTrailersUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;

        }

        protected void onPostExecute() {
            adapter.clear();
            String trailerName = "";
            for (int i = 0; i < Reviews.size(); ++i) {
                trailerName = Reviews.get(i);
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
                    appendPath(MovieId).
                    appendPath("reviews").
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

        private void getTrailersJSON(String urlString) throws JSONException {

            JSONObject trailersObject = new JSONObject(urlString);
            JSONArray trailerArray = trailersObject.getJSONArray("results");


            for (int i = 0; i < trailerArray.length(); ++i) {
                JSONObject temp = trailerArray.getJSONObject(i);
                String stringTemp = "Author: "  + temp.getString("author");
                stringTemp = stringTemp + "\n" + temp.getString("content");
                Reviews.add(stringTemp);

            }


        }
    }
}
