package com.example.android.popularmoviesstage1sumita.utils;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmoviesstage1sumita.DetailActivity;

/**
 * Make Class FetchFavoriteMovieDetails for asynchronous task of getting the Movie Details from API Key
 */
public class DetailMovieLoader extends AsyncTaskLoader<String> {
    private final Context mContext;
    private final String TAG = DetailActivity.class.getSimpleName();
    private final String movieId;
    private final MovieDetailAsyncResponse mDelegate;

    public DetailMovieLoader(Context context, MovieDetailAsyncResponse delegate, String movieId) {

        super(context);
        this.mContext = context;
        this.mDelegate = delegate;
        this.movieId = movieId;
        Log.i(TAG, "Inside DetailMovieLoader Constructor");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        MovieDetails mMovieDetail;
        Log.i(TAG, "loadInBackground");
        mMovieDetail = MoviesUtil.getCompleteMovieDetails(movieId);
        //onPostExecuteLoading(mMovieDetail);
        mDelegate.processFinishFavorite(mMovieDetail, mContext);
        return null;
    }

    public interface MovieDetailAsyncResponse {
        void processFinishFavorite(MovieDetails movieDetails, Context context);
    }

}
