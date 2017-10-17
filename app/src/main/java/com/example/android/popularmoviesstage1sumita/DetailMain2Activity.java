package com.example.android.popularmoviesstage1sumita;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MovieReviewsDetail;
import com.example.android.popularmoviesstage1sumita.utils.MovieVideosDetail;
import com.example.android.popularmoviesstage1sumita.utils.MoviesUtil;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesstage1sumita.utils.MoviesUtil.TRAILER_URL;

public class DetailMain2Activity extends AppCompatActivity {
    private static final String WHERE_CLAUSE = "movieID = ";
    private final String POSTER_PATH = "posterpath";
    private final String TAG = MoviesDetailActivity.class.getSimpleName();
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieSynopsis;
    private TextView movieRatings;
    private TextView movieReleaseDate;
    private int movieId;
    private Button saveAsFavorite;
    private String where_clause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_main2);
        Cursor mCursor;

        //Bundle extras = getIntent().getExtras();
        movieId = getIntent().getIntExtra("MovieId", 0);
        setContentView(R.layout.activity_detail_main2);
        movieTitle = (TextView) findViewById(R.id.title);
        moviePoster = (ImageView) findViewById(R.id.poster);
        movieSynopsis = (TextView) findViewById(R.id.synopsis);
        movieRatings = (TextView) findViewById(R.id.user_rating);
        movieReleaseDate = (TextView) findViewById(R.id.release_date);
        saveAsFavorite = (Button) findViewById(R.id.favorite_movie);
        //Checking the status of save as favorite movies from Content Provider
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        where_clause = WHERE_CLAUSE + movieId + ";";

        mCursor = getContentResolver().query(uri, null, where_clause, null, null);
        if (mCursor.getCount() == 0) {
            saveAsFavorite.setText(R.string.save_as_favorite);
        } else
            saveAsFavorite.setText(R.string.remove_from_favorite);
        mCursor.close();


        // FetchMovies function is called for the Movie Details of the Movie clicked on Main Menu by passing
        // the MovieId this Activity received from onClick function.

        new FetchMovies(this).execute(String.valueOf(movieId));
    }

    public void saveAsFavorite(View view) {
        final String saveMovie = "Save As Favorite";
        final String removeMovie = "Remove From Favorite";

        // Getting Save as favorite flag from Content Provider
        if (saveAsFavorite.getText().equals(saveMovie)) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String posterPath = sharedPref.getString(POSTER_PATH, null);

            // Create new empty ContentValues object
            ContentValues contentValues = new ContentValues();

            // Put the task description and selected mPriority into the ContentValues
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, (movieId));
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, (movieTitle).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, (movieRatings).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, (movieReleaseDate).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, (movieSynopsis).getText().toString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            // Insert the content values via a ContentResolver
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            saveAsFavorite.setText(R.string.remove_from_favorite);
        }

        // COMPLETED (8) Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        else if (saveAsFavorite.getText().equals(removeMovie)) {
            saveAsFavorite.setText(R.string.save_as_favorite);
            int rowDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, where_clause, null);
            if (rowDeleted == 0) {
                Log.i(TAG, "Favorite Movie not Deleted");
            }

        }
    }


    /**
     * Make Class FetchMovies for asynchronous task of getting the Movie Details from API Key
     */
    private class FetchMovies extends AsyncTask<String, Void, MovieDetails> {
        private final Context mContext;

        FetchMovies(Context context) {
            mContext = context;
        }

        @Override
        protected MovieDetails doInBackground(String... params) {

            //MovieUtil.getCompleteMovieDetails(param[0]);

            return MoviesUtil.getCompleteMovieDetails(params[0]);
        }

        /**
         * Setting all the details in the XML file
         */
        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(POSTER_PATH, movieDetails.getPosterPath());
            editor.apply();
            movieTitle.setText(movieDetails.getMovieTitle());
            movieRatings.setText(movieDetails.getRating() + " / 10");
            movieReleaseDate.setText(movieDetails.getReleaseDate());
            movieSynopsis.setText(movieDetails.getSynopsis());
            Picasso.with(mContext).load(movieDetails.getPosterPath()).into(moviePoster);

            // Display the Trailers for the Movie
            final MovieVideosDetail[] movieVideosDetail = movieDetails.getMovieVideosDetail();
            Log.i(TAG, "movieVideosDetail Length" + movieVideosDetail.length);
            GridLayout videoGridLayout = (GridLayout) findViewById(R.id.movie_video);
            for (int i = 0; i < movieVideosDetail.length; i++) {
                ImageView img = new ImageView(mContext);
                Picasso.with(mContext).load(movieVideosDetail[i].getThumbnailUrl()).into(img);
                final String movieUrl = movieVideosDetail[i].getTrailerUrl();
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieUrl));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(TRAILER_URL + movieUrl));
                        try {
                            mContext.startActivity(videoIntent);
                        } catch (ActivityNotFoundException ex) {
                            mContext.startActivity(webIntent);
                        }
                    }
                });
                Log.i(TAG, "Thumbnail is : " + movieVideosDetail[i].getThumbnailUrl());
                videoGridLayout.addView(img);
            }

            // Display the Reviews for the Movie
            MovieReviewsDetail[] movieReviewsDetails = movieDetails.getMovieReviewsDetail();
            LinearLayout reviewLinearLayout = (LinearLayout) findViewById(R.id.movie_review);
            Log.i(TAG, "movie review length is : " + movieReviewsDetails.length);
            for (int j = 0; j < movieReviewsDetails.length; j++) {
                Log.i(TAG, "movie review length is : " + movieReviewsDetails.length);

                TextView authorTitle = new TextView(mContext);
                authorTitle.setTypeface(null, Typeface.BOLD_ITALIC);
                authorTitle.setText(R.string.author_title);
                reviewLinearLayout.addView(authorTitle);
                TextView author = new TextView(mContext);
                author.setText(movieReviewsDetails[j].getAuthor());
                reviewLinearLayout.addView(author);
                TextView commentTitle = new TextView(mContext);
                commentTitle.setTypeface(null, Typeface.BOLD_ITALIC);
                commentTitle.setText(R.string.comments);
                reviewLinearLayout.addView(commentTitle);
                TextView comment = new TextView(mContext);
                comment.setText(movieReviewsDetails[j].getContent());
                reviewLinearLayout.addView(comment);

            }

        }
    }


}

