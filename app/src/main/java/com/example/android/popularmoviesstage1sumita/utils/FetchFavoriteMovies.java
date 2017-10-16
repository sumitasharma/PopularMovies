package com.example.android.popularmoviesstage1sumita.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.example.android.popularmoviesstage1sumita.data.MovieContract;

import static android.content.ContentValues.TAG;





public class FetchFavoriteMovies extends AsyncTaskLoader<String> {
    private final  Context mContext;
    private final MovieDetails[] mMovieDetails;

    public interface AsyncResponse {
        void processFinishFavorite(MovieDetails[] movieDetails,Cursor cursor);
    }

    private final AsyncResponse mDelegate;

    public FetchFavoriteMovies(Context context, RecyclerView recyclerView, MovieDetails[] movieDetails, MoviesAdapter.MoviesClickListener listener, AsyncResponse asyncResponse) {
        super(context);
        RecyclerView mMoviesRecyclerView = recyclerView;
        this.mContext = context;
        this.mMovieDetails = movieDetails;
        MoviesAdapter.MoviesClickListener mClickPositionListener = listener;
        this.mDelegate = asyncResponse;
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        try {
            Cursor cursor = mContext.getContentResolver().query(uri,null,null,null,null);
            onPostExecuteLoading(cursor);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private void onPostExecuteLoading(Cursor cursor) {
        Cursor mCursor;
        if (cursor != null) {
            mCursor = cursor;
           mDelegate.processFinishFavorite(mMovieDetails,mCursor);

        } else {
            Toast.makeText(mContext, "No Internet Connection or API Limit exceeded.Connect and then choose from Sort By Menu", Toast.LENGTH_LONG).show();
        }
    }
}
