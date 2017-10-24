package com.example.android.popularmoviesstage1sumita.utils;


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmoviesstage1sumita.MainActivity;
import com.example.android.popularmoviesstage1sumita.R;

public class SortByMoviesMenu extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public static final int FAVORITE_MOVIE_LOADER = 10;
    public static final int POPULAR_MOVIE_LOADER = 11;
    public static final int RATING_MOVIE_LOADER = 12;
    public static final int MOVIE_DETAILS = 15;
    public static final String ACTIVITY_STATE = "activity_state";
    public final String TAG = MainActivity.class.getSimpleName();
    private final LoaderManager.LoaderCallbacks<String[]> callback = SortByMoviesMenu.this;
    public int mLoaderId;


    public void initializeLoader(int loaderId) {
        this.mLoaderId = loaderId;
        Log.i(TAG, "The loader id is : " + mLoaderId);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(mLoaderId);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(mLoaderId, null, callback);
        } else {
            loaderManager.restartLoader(mLoaderId, null, callback);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACTIVITY_STATE, this.mLoaderId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.sortby_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Function onOptionsItemSelected calls FetchApiMoviesLoader once option is selected based on Sort By
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Bundle queryBundle = new Bundle();
        Log.i(TAG, "onOption : " + item.getItemId());
        int id = item.getItemId();
        switch (id) {
            case R.id.popularity:
                this.setTitle(R.string.screen_label_popular);
                this.mLoaderId = POPULAR_MOVIE_LOADER;
                Log.i(TAG, "onOption : popular");
                initializeLoader(POPULAR_MOVIE_LOADER);
                break;
            case R.id.rating:
                this.setTitle(R.string.screen_label_rating);
                this.mLoaderId = RATING_MOVIE_LOADER;
                Log.i(TAG, "onOption : rating");
                initializeLoader(RATING_MOVIE_LOADER);
                break;
            case R.id.favorite_movies:
                this.setTitle(R.string.screen_label_favorite);
                this.mLoaderId = FAVORITE_MOVIE_LOADER;
                Log.i(TAG, "onOption : favorite");
                initializeLoader(FAVORITE_MOVIE_LOADER);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
