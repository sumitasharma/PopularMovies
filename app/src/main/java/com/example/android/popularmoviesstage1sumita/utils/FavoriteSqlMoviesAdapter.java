package com.example.android.popularmoviesstage1sumita.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage1sumita.R;
import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class FavoriteSqlMoviesAdapter extends RecyclerView.Adapter<FavoriteSqlMoviesAdapter.RecyclerViewHolderFavoriteMovies> {
    private final Context mContext;
    private final Cursor mCursor;
    MovieDetails[] mMovieDetails = null;
    private FavoriteMoviesClickListener mClickPositionListener = null;

    /**
     * Constructor for Movie Adapter
     *
     * @param detailsOfMovies    Movie Details
     * @param context            Context
     * @param movieClickListener clickListener
     */
    public FavoriteSqlMoviesAdapter(MovieDetails[] detailsOfMovies, Context context, FavoriteMoviesClickListener movieClickListener, Cursor cursor) {
        this.mMovieDetails = detailsOfMovies;
        this.mContext = context;
        this.mClickPositionListener = movieClickListener;
        this.mCursor = cursor;
    }

    /**
     * Function attaches view to it's parent Layout and passes it to the RecyclerViewHolderMovies
     *
     * @param parent   Parent ViewGroup
     * @param viewType view type
     * @return RecyclerViewHolderMovies
     */
    @Override
    public RecyclerViewHolderFavoriteMovies onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "Inside RecyclerViewHolderFavoriteMovies");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        return new RecyclerViewHolderFavoriteMovies(view);
    }

    /**
     * Function binds the Movie posterPath to the ImageView mMovieImage
     *
     * @param holder   viewHolder
     * @param position viewPosition
     */
    @Override
    public void onBindViewHolder(FavoriteSqlMoviesAdapter.RecyclerViewHolderFavoriteMovies holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            Log.i(TAG, "mCursor is null on BindHolder");

            return;
        }
        Log.i(TAG, "Inside onBindViewHolder" + position);
        mCursor.moveToPosition(position);
        Log.i(TAG, "onBindViewHolder posterPath is : " + mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
        Picasso.with(mContext).load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH))).into(holder.mMovieImage);

    }

    /**
     * Function returns number of view holders Adapter needs to create
     *
     * @return int
     */
    @Override
    public int getItemCount() {
        Log.i(TAG, "Movie Detail Length " + mCursor.getCount());
        return mCursor.getCount();

    }


    /**
     * Interface to handle clicks on viewHolder
     */
    public interface FavoriteMoviesClickListener {
        void onClickFavoriteMovie(int moviePosition);
    }

    /**
     * RecyclerViewHolderMovies is the viewHolder for MovieAdapter
     */
    class RecyclerViewHolderFavoriteMovies extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMovieImage;

        RecyclerViewHolderFavoriteMovies(View itemView) {
            super(itemView);
            mMovieImage = (ImageView) itemView.findViewById(R.id.action_image);
            mMovieImage.setOnClickListener(this);
            Log.i(TAG, "RecyclerViewHolderFavoriteMovies constructor");
        }

        /**
         * Function attaches onClick to viewHolder position
         *
         * @param v view
         */
        @Override
        public void onClick(View v) {
            int onClickPosition = getAdapterPosition();
            mClickPositionListener.onClickFavoriteMovie(onClickPosition);
        }
    }

}

