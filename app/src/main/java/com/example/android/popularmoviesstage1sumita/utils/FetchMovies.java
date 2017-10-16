package com.example.android.popularmoviesstage1sumita.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class FetchMovies extends AsyncTask<String, Void, MovieDetails[]> {

    private final Context mContext;
    private final RecyclerView mMoviesRecyclerView;
    private MovieDetails[] mMovieDetails;
    private final MoviesAdapter.MoviesClickListener mClickPositionListener;


    public interface AsyncResponse {
        void processFinish(MovieDetails[] movieDetails);
    }

    private final AsyncResponse mDelegate;

    public FetchMovies(Context context, RecyclerView recyclerView,MovieDetails[] movieDetails, MoviesAdapter.MoviesClickListener listener, AsyncResponse asyncResponse) {
        this.mContext = context;
        this.mMoviesRecyclerView = recyclerView;
        this.mMovieDetails = movieDetails;
        this.mClickPositionListener = listener;
        this.mDelegate = asyncResponse;
    }

    /**
     * Checks Internet Connectivity
     *
     * @return true if the Internet Connection is available, false otherwise.
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected MovieDetails[] doInBackground(String... params) {
        if (!isOnline()) {
            return null;
        }
        URL movieURL = MoviesUtil.buildUrl(params[0]);
        try {
            String movieResponse = MoviesUtil.getResponseFromHttpUrl(movieURL);
            mMovieDetails = MoviesUtil.convertJsonToMovieSortBy(movieResponse);
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return mMovieDetails;
    }

    /**
     * Setting all the details in the XML file
     */
    @Override
    protected void onPostExecute(MovieDetails[] movieDetails) {
        MoviesAdapter moviesAdapter;
        if (movieDetails != null) {

            mMovieDetails = movieDetails;
            moviesAdapter = new MoviesAdapter(movieDetails,this.mContext,mClickPositionListener);
                /* Setting the adapter in onPostExecute so the Movies Detail array isn't empty */
            mMoviesRecyclerView.setAdapter(moviesAdapter);
            mDelegate.processFinish(mMovieDetails);

        } else {
            Toast.makeText(mContext, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Post Execute Function. movie details null");
        }
    }
}