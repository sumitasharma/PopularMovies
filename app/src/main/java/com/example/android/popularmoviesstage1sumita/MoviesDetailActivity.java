package com.example.android.popularmoviesstage1sumita;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MoviesUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MoviesDetailActivity extends AppCompatActivity {
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieSynopsis;
    private TextView movieRatings;
    private TextView movieReleaseDate;
    private final String TAG = MoviesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bundle extras = getIntent().getExtras();
        int movieId = getIntent().getIntExtra("MovieId", 0);
        setContentView(R.layout.activity_movies_detail);
        movieTitle = (TextView) findViewById(R.id.title);
        moviePoster = (ImageView) findViewById(R.id.poster);
        movieSynopsis = (TextView) findViewById(R.id.synopsis);
        movieRatings = (TextView) findViewById(R.id.user_rating);
        movieReleaseDate = (TextView) findViewById(R.id.release_date);
        Log.i(TAG, "onCreate Called");

        // FetchMovies function is called for the Movie Details of the Movie clicked on Main Menu by passing
        // the MovieId this Activity received from onClick function.


        new FetchMovies(this).execute(String.valueOf(movieId));
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

            URL movieURL = MoviesUtil.buildUrl(params[0]);
            MovieDetails resultMovieDetail = new MovieDetails();
            try {
                String movieResponse = MoviesUtil.getResponseFromHttpUrl(movieURL);
                resultMovieDetail = MoviesUtil.convertJsonToMovieIdDetail(movieResponse);
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return resultMovieDetail;
        }

        /**
         * Setting all the details in the XML file
         */
        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            movieTitle.setText(movieDetails.getMovieTitle());
            movieRatings.setText(movieDetails.getRating());
            movieReleaseDate.setText(movieDetails.getReleaseDate());
            movieSynopsis.setText(movieDetails.getSynopsis());
            Picasso.with(mContext).load(movieDetails.getPosterPath()).into(moviePoster);
        }
    }

}
