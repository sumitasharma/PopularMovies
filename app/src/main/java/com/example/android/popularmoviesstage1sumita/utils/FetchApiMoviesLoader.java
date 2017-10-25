package com.example.android.popularmoviesstage1sumita.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class FetchApiMoviesLoader extends AsyncTaskLoader<String> {

    private final Context mContext;
    //private final RecyclerView mMoviesRecyclerView;
    //private final MoviesAdapter.MoviesClickListener mClickPositionListener;
    private final AsyncResponse mDelegate;
    private final String mSortBy;
    private MovieDetails[] mMovieDetails;

    public FetchApiMoviesLoader(Context context, RecyclerView recyclerView, MovieDetails[] movieDetails, MoviesAdapter.MoviesClickListener listener, AsyncResponse asyncResponse, String sortBy) {
        super(context);
        this.mContext = context;
        //this.mMoviesRecyclerView = recyclerView;
        this.mMovieDetails = movieDetails;
        //this.mClickPositionListener = listener;
        this.mDelegate = asyncResponse;
        this.mSortBy = sortBy;
        Log.i(TAG, "Constructor called. FetchApiMoviesLoader");
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
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    /**
     * Setting all the details in the XML file
     */
    private void onPostExecuteLoading(MovieDetails[] movieDetails) {
        //MoviesAdapter moviesAdapter;
        Log.i(TAG, "Post Execute Function... Fetch Movies");
        if (movieDetails != null) {
            Log.i(TAG, "Fetch Movies.... onPostExecute. movie not null");
            mMovieDetails = movieDetails;
            //moviesAdapter = new MoviesAdapter(movieDetails,this.mContext,mClickPositionListener);
                /* Setting the adapter in onPostExecuteLoading so the Movies Detail array isn't empty */
            //mMoviesRecyclerView.setAdapter(moviesAdapter);
            mDelegate.processFinish(mMovieDetails);

        } else {
//            Toast.makeText(mContext, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Post Execute Function. movie details null");
        }
    }

    @Override
    public String loadInBackground() {
        if (!isOnline())
            return null;
        URL movieURL = MoviesUtil.buildUrl(mSortBy);
        try {
            Log.i(TAG, "Fetch Movies... loadInBackground");
            String movieResponse = MoviesUtil.getResponseFromHttpUrl(movieURL);
            mMovieDetails = MoviesUtil.convertJsonToMovieSortBy(movieResponse);
        } catch (IOException | JSONException e) {
            Log.i(TAG, "Inside loadInBackground Exception");
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        onPostExecuteLoading(mMovieDetails);
        return null;
    }

    public interface AsyncResponse {
        void processFinish(MovieDetails[] movieDetails);
    }
}