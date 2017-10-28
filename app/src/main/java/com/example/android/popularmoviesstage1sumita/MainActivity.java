package com.example.android.popularmoviesstage1sumita;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.FavoriteSqlMoviesAdapter;
import com.example.android.popularmoviesstage1sumita.utils.FavoriteSqlMoviesAdapter.FavoriteMoviesClickListener;
import com.example.android.popularmoviesstage1sumita.utils.FetchApiMoviesLoader;
import com.example.android.popularmoviesstage1sumita.utils.FetchSqlMoviesLoader;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MoviesAdapter;
import com.example.android.popularmoviesstage1sumita.utils.MoviesUtil;
import com.example.android.popularmoviesstage1sumita.utils.SortByMoviesMenu;


public class MainActivity extends SortByMoviesMenu implements MoviesAdapter.MoviesClickListener, FetchApiMoviesLoader.AsyncResponse, FetchSqlMoviesLoader.AsyncResponse, LoaderManager.LoaderCallbacks, FavoriteMoviesClickListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private final String POPULARITY = "popular";
    private final String RATINGS = "top_rated";
    private final LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
    private int mLoaderId;
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
        if (savedInstanceState != null) {
            this.mLoaderId = savedInstanceState.getInt(ACTIVITY_STATE, POPULAR_MOVIE_LOADER);
            if (mLoaderId == POPULAR_MOVIE_LOADER)
                this.setTitle(R.string.screen_label_popular);
            else if (mLoaderId == FAVORITE_MOVIE_LOADER)
                this.setTitle(R.string.screen_label_favorite);
            else if (mLoaderId == RATING_MOVIE_LOADER)
                this.setTitle(R.string.screen_label_rating);
            else {
                this.setTitle(R.string.screen_label_popular);
            }
        }
        // initializeLoader();
        initializeLoader(mLoaderId);


    }


    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed
        switch (this.mLoaderId) {
            case POPULAR_MOVIE_LOADER:
                mLoaderId = POPULAR_MOVIE_LOADER;
                this.setTitle(R.string.screen_label_popular);
                break;
            case RATING_MOVIE_LOADER:
                mLoaderId = RATING_MOVIE_LOADER;
                this.setTitle(R.string.screen_label_rating);
                break;
            case FAVORITE_MOVIE_LOADER:
                mLoaderId = FAVORITE_MOVIE_LOADER;
                this.setTitle(R.string.screen_label_favorite);
                break;
            default:
                this.setTitle(R.string.screen_label_popular);
                break;
        }
        getSupportLoaderManager().restartLoader(this.mLoaderId, null, callback);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoaderId = savedInstanceState.getInt(ACTIVITY_STATE);
        Log.i(TAG, "savedInstanceState onRestoreInstanceState : " + mLoaderId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Title on resume is : " + this.getTitle() + mLoaderId);
        getSupportLoaderManager().restartLoader(this.mLoaderId, null, callback);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACTIVITY_STATE, this.mLoaderId);
    }

    protected void initializeLoader(int loaderId) {
        this.mLoaderId = loaderId;

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(mLoaderId);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(mLoaderId, null, callback);
        } else {
            loaderManager.restartLoader(mLoaderId, null, callback);
        }

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

    /**
     * Checks Internet Connectivity
     *
     * @return true if the Internet Connection is available, false otherwise.
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void processFinish(MovieDetails[] movieDetails) {
        this.mMovieDetails = movieDetails;
    }

    public void processFinishFavorite(MovieDetails[] movieDetails, Cursor cursor) {
        if (movieDetails == null && !isOnline()) {

            Log.i(TAG, "not online... loadInBackground");
            return;
        }

        this.mMovieDetails = movieDetails;
        this.mCursor = cursor;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        super.onCreateLoader(mLoaderId, args);
        checkIntent();
        if (id == POPULAR_MOVIE_LOADER) {
            this.setTitle(R.string.screen_label_popular);
            return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, POPULARITY);
        } else if (id == RATING_MOVIE_LOADER) {
            this.setTitle(R.string.screen_label_rating);
            return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, RATINGS);
        } else if (id == FAVORITE_MOVIE_LOADER) {
            this.setTitle(R.string.screen_label_favorite);
            return new FetchSqlMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this);
        } else {
            Log.i(TAG, "onCreateLoader default is called" + mLoaderId);
            this.setTitle(R.string.screen_label_popular);
            return new FetchApiMoviesLoader(this, mMoviesRecyclerView, mMovieDetails, this, this, POPULARITY);
        }

    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {

        switch (mLoaderId) {
            case FAVORITE_MOVIE_LOADER:
                if (mCursor == null || !isOnline()) {
                    Toast.makeText(this, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Creating Toast");
                } else {
                    Log.i(TAG, "mCursor not null");
                    FavoriteSqlMoviesAdapter favoriteSqlMoviesAdapter = new FavoriteSqlMoviesAdapter(mMovieDetails, this, this, mCursor);
                /* Setting the adapter in onPostExecute so the Movies Detail array isn't empty */
                    mMoviesRecyclerView.setAdapter(favoriteSqlMoviesAdapter);
                }
                break;
            default:
                if (!isOnline() || MoviesUtil.MOVIES_API_KEY.equals("")) {
                    Toast.makeText(this, "No Internet Connection. Connect and then choose from Sort By Menu / API Key not set or API Limit exceeded. Kindly check and restart Application.",Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Creating Toast");
                } else {
                    MoviesAdapter moviesAdapter;
                    moviesAdapter = new MoviesAdapter(mMovieDetails, this, this);
                    mMoviesRecyclerView.setAdapter(moviesAdapter);
                    break;
                }

        }
    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            this.mLoaderId = intent.getIntExtra("loader", mLoaderId);
            Log.i(TAG, "intent extra is : " + mLoaderId);
            setIntent(null);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}

