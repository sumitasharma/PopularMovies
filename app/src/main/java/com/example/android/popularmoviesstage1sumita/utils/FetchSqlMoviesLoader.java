package com.example.android.popularmoviesstage1sumita.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;


public class FetchSqlMoviesLoader extends AsyncTaskLoader<String> {
    private final Context mContext;
    private final MovieDetails[] mMovieDetails;
    private final AsyncResponse mDelegate;

    public FetchSqlMoviesLoader(Context context, RecyclerView recyclerView, MovieDetails[] movieDetails, MoviesAdapter.MoviesClickListener listener, AsyncResponse asyncResponse) {
        super(context);
        // RecyclerView mMoviesRecyclerView = recyclerView;
        this.mContext = context;
        this.mMovieDetails = movieDetails;
        //  MoviesAdapter.MoviesClickListener mClickPositionListener = listener;
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
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (!isOnline()) {
            return null;
        }
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;

        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        onPostExecuteLoading(cursor);

        return null;
    }

    private void onPostExecuteLoading(Cursor cursor) {
        Cursor mCursor;
        if (cursor != null) {
            mCursor = cursor;
            mDelegate.processFinishFavorite(mMovieDetails, mCursor);

        } else {
            Toast.makeText(mContext, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
        }
    }


    public interface AsyncResponse {
        void processFinishFavorite(MovieDetails[] movieDetails, Cursor cursor);
    }
}
