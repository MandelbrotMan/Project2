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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends Activity    implements MovieDetailActivityFragment.OnFavoriteSelectedListener {

    String ImageURLString; //For posterpath
    String MovieIdString;  //For pulling additional data of selected movie
    String Title;
    String Rating;
    String ReleaseDate;
    String Plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

    }

    @Override
    public void onFavoriteSelected(String title, String date, String rating, String plot, String id, String url) {
        Title = title;
        Rating = rating;
        ImageURLString = url;
        MovieIdString = id;
        ReleaseDate = date;
        Plot = plot;
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, Main2Activity.class);
        if(!Title.isEmpty()) {
            Bundle MoviePackage = new Bundle();
            MoviePackage.putString("title", Title);
            MoviePackage.putString("releaseDate", ReleaseDate);
            MoviePackage.putString("rating", Rating);
            MoviePackage.putString("plot", Plot);
            MoviePackage.putString("movieIdString", MovieIdString);
            MoviePackage.putString("imageURLString", ImageURLString);
            i.putExtras(MoviePackage);
        }
        startActivity(i);
    }


}

/*
 */