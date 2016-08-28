package com.example.raymondlian.movieappv2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {
    private final int PHONE_VIEW_TYPE = 0;
    private final int TABLET_VIEW_TYPE = 1;
    boolean mTwoPane = false;
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //To determine which view to inflate and which data belongs to which view

    @Override
    public int getViewTypeCount(){
        return 2;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        int layoutId = -1;
        if(mTwoPane == false) {
            layoutId = R.layout.movie_image_phone;
        }
        else {
            layoutId = R.layout.movie_image_tablet;
        }


        view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;


    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ViewHolder viewHeld = (ViewHolder) view.getTag();

            Picasso.with(mContext).load(cursor.getString(MainActivityFragment.COLUMN_IMG_URL)).into(viewHeld.posterView);




    }
    public static class ViewHolder{


        ImageView posterView;


        public ViewHolder(View view) {

            posterView = (ImageView) view.findViewById(R.id.movie_imageview);
        }

    }
    public void setUseTalbletLayout(boolean useTodayLayout) {
        mTwoPane = useTodayLayout;

    }

}