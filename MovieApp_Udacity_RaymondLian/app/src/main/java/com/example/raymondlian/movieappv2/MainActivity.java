package com.example.raymondlian.movieappv2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends Activity implements  MovieDetailFragment.OnMovieSelectedListener{

    String ImageURLString = " "; //For posterpath
    String MovieIdString = " ";  //For pulling additional data of selected movie
    String Title = " ";
    String Rating = " ";
    String ReleaseDate = " ";
    String Plot= " ";
    boolean FavStatus = false;
    FragmentManager manager;
    MainActivityFragment fragmentMain;
    MovieDetailFragment fragmentDetail;
    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        boolean tabletSize = getResources().getBoolean(R.bool.has_two_panes);
        if (!tabletSize) {
            fragmentMain = new MainActivityFragment();
            manager = getFragmentManager();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_main, fragmentMain, "Gridview");
            transaction.commit();

        } else {

            MainActivityFragment fragmentMain = new MainActivityFragment();
            manager = getFragmentManager();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_main, fragmentMain, "Gridview");


            fragmentDetail = new MovieDetailFragment();
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





    }


    @Override
    public void updateData(String titleS, String dateS, String ratingS, String plotS, String idS, String urlS, boolean statusS, ArrayList<TrailerObject> list){
        Log.v("Number of trailers", Integer.toString(list.size()));
        ImageURLString  = urlS;
        MovieIdString = idS;
        Title = titleS;
        ReleaseDate = dateS;
        Rating = ratingS;
        Plot = plotS;
        FavStatus = statusS;
        if(!trailerObjects.isEmpty()) {
            trailerObjects.clear();
        }
        for(int i = 0; i < list.size(); ++i){
                trailerObjects.add(list.get(i));
        }




        MovieDetailFragment temp = (MovieDetailFragment) manager.findFragmentByTag("Details");

        temp.update(Title, ReleaseDate, Rating, Plot, MovieIdString,ImageURLString,FavStatus, trailerObjects);



    }



}
