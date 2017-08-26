package com.example.android.popularmoviesstage1sumita.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage1sumita.R;
import com.squareup.picasso.Picasso;

/**
 * MoviesAdapter class extends RecyclerView.Adapter
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.RecyclerViewHolderMovies> {
    private MovieDetails[] mMovieDetails = null;
    private Context mContext;
    private MoviesClickListener mClickPositionListener = null;

    /**
     * Interface to handle clicks on viewHolder
     */
    public interface MoviesClickListener {
        void onClickMovie(int moviePosition);
    }

    /**
     * Constructor for Movie Adapter
     *
     * @param detailsOfMovies    Movie Details
     * @param context            Context
     * @param movieClickListener clickListener
     */
    public MoviesAdapter(MovieDetails[] detailsOfMovies, Context context, MoviesClickListener movieClickListener) {
        this.mMovieDetails = detailsOfMovies;
        this.mContext = context;
        this.mClickPositionListener = movieClickListener;
    }

    /**
     * Function attaches view to it's parent Layout and passes it to the RecyclerViewHolderMovies
     *
     * @param parent   Parent ViewGroup
     * @param viewType view type
     * @return RecyclerViewHolderMovies
     */
    @Override
    public RecyclerViewHolderMovies onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        RecyclerViewHolderMovies viewHolderMovies = new RecyclerViewHolderMovies(view);
        return viewHolderMovies;
    }

    /**
     * Function binds the Movie posterPath to the ImageView mMovieImage
     *
     * @param holder   viewHolder
     * @param position viewPosition
     */
    @Override
    public void onBindViewHolder(RecyclerViewHolderMovies holder, int position) {
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
    class RecyclerViewHolderMovies extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mMovieImage;

        RecyclerViewHolderMovies(View itemView) {
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
