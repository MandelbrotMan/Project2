package com.example.raymondlian.movieappv2;

/**
 * Created by raymondlian on 11/3/15.
 */
public class TrailerObject {
    public String trailer_title;
    public String trailer_url;

    public TrailerObject (){
        trailer_title = "";
        trailer_url = "";
    }
    public TrailerObject (String title, String url){
        trailer_title = title;
        trailer_url = url;
    }
}
