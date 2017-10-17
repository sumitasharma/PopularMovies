package com.example.android.popularmoviesstage1sumita;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmoviesstage1sumita.utils.FavoriteMoviesAdapter;
import com.example.android.popularmoviesstage1sumita.utils.FavoriteMoviesAdapter.FavoriteMoviesClickListener;
import com.example.android.popularmoviesstage1sumita.utils.FetchFavoriteMovies;
import com.example.android.popularmoviesstage1sumita.utils.FetchMovies;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MoviesAdapter;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesClickListener,FetchMovies.AsyncResponse,FetchFavoriteMovies.AsyncResponse,LoaderManager.LoaderCallbacks, FavoriteMoviesClickListener {
    private static final int MOVIE_SEARCH_LOADER = 10;
    private final String POPULARITY = "popular";
    private final String TAG = MainActivity.class.getSimpleName();
    private Cursor mCursor;
    private RecyclerView mMoviesRecyclerView;
    private MovieDetails[] mMovieDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.movies_recyclerview);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        /* Calling the Asynchronous task FetchMovies */
        new FetchMovies(this,mMoviesRecyclerView,mMovieDetails,this,this).execute(POPULARITY);
       // initializeLoader();
    }

    private void initializeLoader(){
        int loaderId = MOVIE_SEARCH_LOADER;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
       // Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(loaderId);
        if(movieSearchLoader==null) {
            loaderManager.initLoader(loaderId, null, callback);
        }
        else {
            loaderManager.restartLoader(loaderId, null, callback);
        }

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
     * Function onOptionsItemSelected calls FetchMovies once option is selected based on Sort By
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String RATINGS = "top_rated";
       // Bundle queryBundle = new Bundle();

        int id = item.getItemId();
        switch (id) {
            case R.id.popularity:
                this.setTitle("Popular Movies");
                new FetchMovies(this,mMoviesRecyclerView,mMovieDetails,this,this).execute(POPULARITY);
                break;
            case R.id.rating:
                this.setTitle("Sort by Ratings");
                new FetchMovies(this,mMoviesRecyclerView,mMovieDetails,this,this).execute(RATINGS);
                break;
            case R.id.favorite_movies:
                this.setTitle("My Favorites");
                initializeLoader();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function calls the MovieDetailsActivity to display the details of the clicked Movie by passing MovieId
     */

    @Override
    public void onClickMovie(int moviePosition) {
        Intent intent = new Intent(this, DetailMainActivity.class);
        intent.putExtra("MovieId", mMovieDetails[moviePosition].getId());
        startActivity(intent);
    }

    @Override
    public void processFinish(MovieDetails[] movieDetails) {
        this.mMovieDetails=movieDetails;
    }
    public void processFinishFavorite(MovieDetails[] movieDetails,Cursor cursor ) {
        this.mMovieDetails=movieDetails;
        this.mCursor=cursor;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.i(TAG,"Inside onCreateLoader");
        return new FetchFavoriteMovies(this,mMoviesRecyclerView,mMovieDetails,this,this);

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        FavoriteMoviesAdapter favoriteMoviesAdapter = new FavoriteMoviesAdapter(mMovieDetails, this,this,mCursor);
                /* Setting the adapter in onPostExecute so the Movies Detail array isn't empty */
        mMoviesRecyclerView.setAdapter(favoriteMoviesAdapter);


    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

