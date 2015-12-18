package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class Main2Activity extends Activity implements  MovieDetailActivityFragment.OnMovieSelectedListener{

    String ImageURLString = " "; //For posterpath
    String MovieIdString = " ";  //For pulling additional data of selected movie
    String Title = " ";
    String Rating = " ";
    String ReleaseDate = " ";
    String Plot= " ";
    boolean FavStatus = false;
    FragmentManager manager;
    Main2ActivityFragment fragmentMain;
    MovieDetailActivityFragment fragmentDetail;
    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        boolean tabletSize = getResources().getBoolean(R.bool.has_two_panes);
        if (!tabletSize) {
            fragmentMain = new Main2ActivityFragment();
            manager = getFragmentManager();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_main, fragmentMain, "Gridview");
            transaction.commit();

        } else {
            Main2ActivityFragment fragmentMain = new Main2ActivityFragment();
            manager = getFragmentManager();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_main, fragmentMain, "Gridview");


            fragmentDetail = new MovieDetailActivityFragment();
            manager = getFragmentManager();
            transaction.add(R.id.fragment_container_main, fragmentDetail, "Details");
            transaction.commit();
        }




    }
    public void switchToMovieDetail(){
        Bundle moviePackage = new Bundle();
        moviePackage.putString("title", Title);
        moviePackage.putString("image", ImageURLString);
        moviePackage.putString("release_date", ReleaseDate);
        moviePackage.putString("vote_average", Rating);
        moviePackage.putString("synopsis", Plot);
        moviePackage.putString("id", MovieIdString);
        moviePackage.putBoolean("favStatus", FavStatus);

        MovieDetailActivityFragment fragment1 = new MovieDetailActivityFragment();
        fragment1.setArguments(moviePackage);
        FragmentTransaction transaction = manager.beginTransaction();


    transaction.replace(R.id.fragment_container_main, fragment1);
    transaction.addToBackStack("Gridview");
    transaction.commit();




    }


    @Override
    public void updateData(String titleS, String dateS, String ratingS, String plotS, String idS, String urlS, boolean statusS, ArrayList<TrailerObject> list){

        ImageURLString  = urlS;
        MovieIdString = idS;
        Title = titleS;
        ReleaseDate = dateS;
        Rating = ratingS;
        Plot = plotS;
        FavStatus = statusS;



        MovieDetailActivityFragment temp = (MovieDetailActivityFragment) manager.findFragmentByTag("Details");

        temp.update(Title, ReleaseDate, Rating, Plot, MovieIdString,ImageURLString,FavStatus, trailerObjects);



    }



}
