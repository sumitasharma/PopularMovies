package com.example.android.popularmoviesstage1sumita.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage1sumita.R;
import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.squareup.picasso.Picasso;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.RecyclerViewHolderFavoriteMovies> {
    private MovieDetails[] mMovieDetails = null;
    private final Context mContext;
    private FavoriteMoviesClickListener mClickPositionListener = null;
    private final Cursor mCursor;

    /**
     * Interface to handle clicks on viewHolder
     */
    public interface FavoriteMoviesClickListener {
        void onClickMovie(int moviePosition);
    }

    /**
     * Constructor for Movie Adapter
     *
     * @param detailsOfMovies    Movie Details
     * @param context            Context
     * @param movieClickListener clickListener
     */
    public FavoriteMoviesAdapter(MovieDetails[] detailsOfMovies, Context context, FavoriteMoviesClickListener movieClickListener, Cursor cursor) {
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
    public void onBindViewHolder(FavoriteMoviesAdapter.RecyclerViewHolderFavoriteMovies holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;
        mMovieDetails[position].setId(mCursor.getInt(mCursor.getColumnIndex((MovieContract.MovieEntry.COLUMN_ID))));
       mMovieDetails[position].setMovieTitle(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
        mMovieDetails[position].setPosterPath(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
       mMovieDetails[position].setRating(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
        mMovieDetails[position].setReleaseDate(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        mMovieDetails[position].setSynopsis(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)));
        Picasso.with(mContext).load(mMovieDetails[position].getPosterPath()).into(holder.mMovieImage);
    }



    /**
     * Function returns number of view holders Adapter needs to create
     *
     * @return int
     */
    @Override
    public int getItemCount() {

        return mMovieDetails.length;
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
        }

        /**
         * Function attaches onClick to viewHolder position
         *
         * @param v view
         */
        @Override
        public void onClick(View v) {
            int onClickPosition = getAdapterPosition();
            mClickPositionListener.onClickMovie(onClickPosition);
        }
    }

}

