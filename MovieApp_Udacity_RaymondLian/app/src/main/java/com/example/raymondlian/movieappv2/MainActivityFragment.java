package com.example.raymondlian.movieappv2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //Global variables start with Capital letters
    ArrayList<MovieObject> MoviesListed = new ArrayList<MovieObject>();
    static ArrayList<MovieObject> FavoriteMovies = new ArrayList<MovieObject>();
    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();

    MovieDetailFragment.OnMovieSelectedListener communicator;
    int positionSelected;

    FragmentManager manager;

    GridView Gridview;
    ImageAdapter JsonAdapter;
    ImageAdapter LocalAdapter;
    TextView ListTitle;
    View root;
    static int CurrentList = 0; //0 if its MoviesListed, 1 if FavoriteMovies --used for item selection

    LinearLayout HeaderProgress;
    Bundle formMovieDetailPackage;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_IMG_URL
    };
    private static int COLUMN_TITLE = 0;
    private static int COLUMN_RELEASE_DATE = 1;
    private static int COLUMN_VOTE_AVERAGE = 2;
    private static int COLUMN_ID = 3;
    private static int COLUMN_SYNOPSIS = 4;
    private static int COLUMN_IMG_URL = 5;






    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main_gridview, container, false);

        getActivity().setTitle("Blue Ray Movies");
        HeaderProgress = (LinearLayout) root.findViewById(R.id.ProgressBarLayout);

        Gridview = (GridView) root.findViewById(R.id.gridview);
        ListTitle = (TextView) root.findViewById(R.id.textView);

        JsonAdapter = new ImageAdapter(getActivity(), MoviesListed);
        LocalAdapter = new ImageAdapter(getActivity(), FavoriteMovies);

        Intent intent = getActivity().getIntent();
        formMovieDetailPackage = intent.getExtras();



        if(formMovieDetailPackage != null){
            if(!checkInList(formMovieDetailPackage.getString("movieIdString"), FavoriteMovies)){
                MovieObject newFavorite = new MovieObject(
                        formMovieDetailPackage.getString("title"),
                        formMovieDetailPackage.getString("releaseDate"),
                        formMovieDetailPackage.getString("rating"),
                        formMovieDetailPackage.getString("plot"),
                        formMovieDetailPackage.getString("movieIdString"),
                        formMovieDetailPackage.getString("imageURLString"));
                FavoriteMovies.add(newFavorite);

            }


        }





        //For preserving screen data during screen rotation
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            new DownloadImageTask((GridView) root.findViewById(R.id.gridview)).execute("popular");


        } else {
            if(!isNetworkAvailable()){
                ListTitle.setText("No Network Connection");
            }

            MoviesListed = savedInstanceState.getParcelableArrayList("movies");
            FavoriteMovies = savedInstanceState.getParcelableArrayList("favorites");
            Context context = getActivity().getApplicationContext();
            if(CurrentList == 0) {
                JsonAdapter.restore(getActivity(), MoviesListed);
                JsonAdapter.notifyDataSetChanged();
                Gridview.setAdapter(JsonAdapter);

            }else if (CurrentList == 1) {
                LocalAdapter.restore(getActivity(), FavoriteMovies);
                LocalAdapter.notifyDataSetChanged();
                Gridview.setAdapter(LocalAdapter);
            }



        }


        Gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                positionSelected = position;
                Intent intent1 = new Intent(getActivity(), Detail_Activity.class);
                startActivity(intent1);



            }
        });


        setHasOptionsMenu(true);
        return root;

    }
    @Override
    public  void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        communicator = (MovieDetailFragment.OnMovieSelectedListener) getActivity();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.menu_main, menu);


    }
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("movies", MoviesListed);
        bundle.putParcelableArrayList("favorites", FavoriteMovies);
        super.onSaveInstanceState(bundle);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popularityMenu
                ) {
            Gridview.setAdapter(null);


           new DownloadImageTask((GridView) getActivity().findViewById(R.id.gridview)).execute("popular");
            return true;
        }
        if (id == R.id.action_ratingMenu
                ) {
            Gridview.setAdapter(null);
            new DownloadImageTask((GridView) getActivity().findViewById(R.id.gridview)).execute("top_rated");


            return true;
        }
        if (id == R.id.action_FavoriteMenu){
            ContentValues test = new ContentValues();
            test.put(MovieContract.MovieEntry.COLUMN_TITLE, MoviesListed.get(0).savedTitle);
            test.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MoviesListed.get(0).savedDate);
            test.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, MoviesListed.get(0).savedRating);
            test.put(MovieContract.MovieEntry.COLUMN_ID, MoviesListed.get(0).savedId);
            test.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, MoviesListed.get(0).savedPlot);
            test.put(MovieContract.MovieEntry.COLUMN_IMG_URL, MoviesListed.get(0).savedURL);
            test.put(MovieContract.MovieEntry.COLUMN_CURRENT_LIST, "True");
            test.put(MovieContract.MovieEntry.COLUMN_FAV_STAT, "False");
            getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, test);
            Cursor testCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, NOTIFY_MOVIE_PROJECTION, null, null, null);
            if(testCursor.moveToFirst()){
                Toast.makeText(getActivity(), testCursor.getString(COLUMN_ID),
                        Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, ArrayList<MovieObject>> {
        GridView bmImage;
        String listInfo = "";

        public DownloadImageTask(GridView bmImage) {
            this.bmImage = bmImage;
        }

        protected ArrayList<MovieObject> doInBackground(String... urls) {
            //Clear to prevent multiple copies in arrays
            MoviesListed.clear();
            CurrentList = 0;

            if (urls[0] == "popular") {
                listInfo = "Most Popular";
            } else if (urls[0] == "top_rated") {
                listInfo = "Top Rated";
            }


            String popularURL = getJsonURL(urls[0]);
            String[] jsonArray = {};


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
            return MoviesListed;
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
           HeaderProgress.setVisibility(View.VISIBLE);
            //setProgressBarIndeterminateVisibility(true);
        }


        protected void onPostExecute(ArrayList<MovieObject> movies) {

            JsonAdapter.notifyDataSetChanged();
            Gridview.setAdapter(JsonAdapter);
            ListTitle.setText(listInfo);
            Context context = getActivity();
            HeaderProgress.setVisibility(View.GONE);


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

        protected String[] getJsonData(String s, int top) throws JSONException {
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

            //Extracting details of movies from json
            for (int j = 0; j < movieArray.length(); j++) {
                JSONObject singleMovie = movieArray.getJSONObject(j);
                imagePathArray.add(singleMovie.getString(get_PATH).substring(1));

                MovieObject temp = new MovieObject(singleMovie.getString(get_TITLE),
                        singleMovie.getString(get_RELEASE_DATE),
                        Integer.toString(singleMovie.getInt(get_AVERAGE)),
                        singleMovie.getString(get_SYNOPSIS), Integer.toString(singleMovie.getInt(get_ID)));
                MoviesListed.add(temp);

            }


            String[] convertedArray = new String[imagePathArray.size()];
            convertedArray = imagePathArray.toArray(convertedArray);
            return convertedArray;
        }

        protected Void getBitMapURL(String[] paths) throws IOException {
            HttpURLConnection posterUrlConnection = null;



            for (int i = 0; i < paths.length; ++i) {
                Uri builtUri = Uri.parse("http://image.tmdb.org").buildUpon()
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w500")
                        .appendPath(paths[i]).build();
                try {
                    URL bitmapURL = new URL(builtUri.toString());


                    posterUrlConnection = (HttpURLConnection) bitmapURL.openConnection();
                    posterUrlConnection.setRequestMethod("GET");
                    posterUrlConnection.connect();



                    MoviesListed.get(i).savedURL = bitmapURL.toString();

                } catch (IOException e) {
                    return null;
                } finally {
                    if (posterUrlConnection != null) {
                        posterUrlConnection.disconnect();
                    }

                }
            }
            return null;

        }


    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<MovieObject> loadList = new ArrayList<MovieObject>();

        public ImageAdapter(Context c, ArrayList<MovieObject> toDisplay) {
            loadList = toDisplay;
            mContext = c;
        }

        public int getCount() {
            return loadList.size();
        }

        public Object getItem(int position) {

            return null;
        }

        public long getItemId(int position) {

            return 0;
        }
        private void restore(Context rContext, ArrayList<MovieObject> rList){
            mContext = rContext;
            loadList = rList;
        }
        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 425));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(4, 4, 4, 4);
            } else {
                imageView = (ImageView) convertView;
            }


            Picasso.with(mContext).load(loadList.get(position).savedURL).into(imageView);


            return imageView;
        }


        // references to our images

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getTrailerJsonURL(String trailerUrl) {
        String JsonUrl = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader;

        InputStream stream;
        URL popularURL;


        Uri base = Uri.parse("https://api.themoviedb.org").buildUpon().
                appendPath("3").
                appendPath("movie").
                appendPath(trailerUrl).
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
            String trailerLink = null;

            Uri base = Uri.parse("https://youtube.com").buildUpon().
                    appendPath("watch").
                    appendQueryParameter("v", temp.getString("key")).build();

            HttpURLConnection urlConnection = null;

            try {
                URL  trailerURL = new URL(base.toString());

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

            TrailerObject tempTrailer = new TrailerObject(temp.getString("name"),trailerLink);

            trailerObjects.add(tempTrailer);




        }



    }
    private class trailerTask extends AsyncTask<String, Void, ArrayList<TrailerObject>> {
        HttpURLConnection posterUrlConnection = null;



        protected ArrayList<TrailerObject> doInBackground(String... param){
            trailerObjects.clear(); //Ensure there are no trailers from previous tasks
            String movieTrailersUrl = getTrailerJsonURL(param[0]); //Set up URL for pulling the JSON Data


            try {
                getTrailersJSON(movieTrailersUrl); // Fill up the trailerObjects list with trailers
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;

        }




    }


    //Holds all the movie contents Information
    private boolean checkInList( String id, ArrayList<MovieObject> objects){
        boolean status = false;
        for(int i = 0; i < objects.size(); ++i){
            if(id.equals(objects.get(i).savedId)){
                Log.v("Favorite Id list:", objects.get(i).savedId);
                status = true;
            }
        }
        return  status;

    }
}
