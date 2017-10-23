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
import android.widget.Toast;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.FavoriteSqlMoviesAdapter;
import com.example.android.popularmoviesstage1sumita.utils.FavoriteSqlMoviesAdapter.FavoriteMoviesClickListener;
import com.example.android.popularmoviesstage1sumita.utils.FetchApiMoviesLoader;
import com.example.android.popularmoviesstage1sumita.utils.FetchSqlMoviesLoader;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MoviesAdapter;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesClickListener, FetchApiMoviesLoader.AsyncResponse, FetchSqlMoviesLoader.AsyncResponse, LoaderManager.LoaderCallbacks, FavoriteMoviesClickListener {
    static final int FAVORITE_MOVIE_LOADER = 10;
    static final int POPULAR_MOVIE_LOADER = 11;
    static final int RATING_MOVIE_LOADER = 12;
    final String POPULARITY = "popular";
    final String RATINGS = "top_rated";
    private final String TAG = MainActivity.class.getSimpleName();
    private final LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
    int mLoaderId;
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
        // initializeLoader();
        this.setTitle(R.string.screen_label_popular);
        initializeLoader(POPULAR_MOVIE_LOADER);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed
        switch (mLoaderId) {
            case POPULAR_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_popular);
            case RATING_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_rating);
            case FAVORITE_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_favorite);
        }
        Log.i(TAG, "Title on restart is : " + this.getTitle());
        getSupportLoaderManager().restartLoader(mLoaderId, null, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //When BACK BUTTON is pressed
        switch (mLoaderId) {
            case POPULAR_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_popular);
            case RATING_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_rating);
            case FAVORITE_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_favorite);
        }
        Log.i(TAG, "Title on restart is : " + this.getTitle());
        getSupportLoaderManager().restartLoader(mLoaderId, null, callback);
    }


    private void initializeLoader(int loaderId) {
        this.mLoaderId = loaderId;

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(mLoaderId);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(mLoaderId, null, callback);
        } else {
            loaderManager.restartLoader(mLoaderId, null, callback);
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
     * Function onOptionsItemSelected calls FetchApiMoviesLoader once option is selected based on Sort By
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Bundle queryBundle = new Bundle();

        int id = item.getItemId();
        switch (id) {
            case R.id.popularity:
                this.setTitle(R.string.screen_label_popular);
                initializeLoader(POPULAR_MOVIE_LOADER);
                break;
            case R.id.rating:
                this.setTitle(R.string.screen_label_rating);
                initializeLoader(RATING_MOVIE_LOADER);
                break;
            case R.id.favorite_movies:
                this.setTitle(R.string.screen_label_favorite);
                initializeLoader(FAVORITE_MOVIE_LOADER);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function calls the MovieDetailsActivity to display the details of the clicked Movie by passing MovieId
     */

    @Override
    public void onClickMovie(int moviePosition) {
        String movieId = String.valueOf(mMovieDetails[moviePosition].getId());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MovieId", movieId);
        startActivity(intent);
    }

    @Override
    public void onClickFavoriteMovie(int moviePosition) {
        mCursor.moveToPosition(moviePosition);
        String movieId = String.valueOf(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MovieId", movieId);
        startActivity(intent);
    }


    @Override
    public void processFinish(MovieDetails[] movieDetails) {
        this.mMovieDetails = movieDetails;
    }

    public void processFinishFavorite(MovieDetails[] movieDetails, Cursor cursor) {

        this.mMovieDetails = movieDetails;
        this.mCursor = cursor;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case POPULAR_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_popular);
                return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, POPULARITY);
            case RATING_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_rating);
                return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, RATINGS);
            case FAVORITE_MOVIE_LOADER:
                this.setTitle(R.string.screen_label_favorite);
                return new FetchSqlMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this);
            default:
                Log.i(TAG, "onCreateLoader default is called");
                this.setTitle(R.string.screen_label_popular);
                return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, POPULARITY);
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (mLoaderId) {
            case FAVORITE_MOVIE_LOADER:
                if (mCursor == null) {
                    Toast.makeText(this, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Creating Toast");
                } else {
                    Log.i(TAG, "mCurson not null");
                    FavoriteSqlMoviesAdapter favoriteSqlMoviesAdapter = new FavoriteSqlMoviesAdapter(mMovieDetails, this, this, mCursor);
                /* Setting the adapter in onPostExecute so the Movies Detail array isn't empty */
                    mMoviesRecyclerView.setAdapter(favoriteSqlMoviesAdapter);
                }
                break;
            default:
                MoviesAdapter moviesAdapter;
                moviesAdapter = new MoviesAdapter(mMovieDetails, this, this);
                /* Setting the adapter in onPostExecuteLoading so the Movies Detail array isn't empty */
                mMoviesRecyclerView.setAdapter(moviesAdapter);
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

