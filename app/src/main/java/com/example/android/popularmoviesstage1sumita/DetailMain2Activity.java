package com.example.android.popularmoviesstage1sumita;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MovieReviewsDetail;
import com.example.android.popularmoviesstage1sumita.utils.MovieVideosDetail;
import com.example.android.popularmoviesstage1sumita.utils.MoviesUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.security.KeyFactorySpi;

import static android.os.Build.VERSION_CODES.M;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class DetailMain2Activity extends AppCompatActivity{
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieSynopsis;
    private TextView movieRatings;
    private TextView movieReleaseDate;
    private int movieId;
    private Button saveAsFavorite;
    private static final String WHERE_CLAUSE = "movieID = ";
    private String where_clause;
    private final String POSTER_PATH = "posterpath";
    private final String TAG = MoviesDetailActivity.class.getSimpleName();

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
        try {
            mCursor = getContentResolver().query(uri, null, where_clause, null, null);
            if (mCursor.getCount() == 0) {
                saveAsFavorite.setText(R.string.save_as_favorite);
            }
            else
                saveAsFavorite.setText(R.string.remove_from_favorite);
            mCursor.close();
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

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
            int rowDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,where_clause, null);
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
            movieRatings.setText(movieDetails.getRating()+" / 10");
            movieReleaseDate.setText(movieDetails.getReleaseDate());
            movieSynopsis.setText(movieDetails.getSynopsis());
            Picasso.with(mContext).load(movieDetails.getPosterPath()).into(moviePoster);
            //MovieVideosDetail[] movieVideosDetail = movieDetails.getMovieVideosDetail();

            MovieVideosDetail[] movieVideosDetail = movieDetails.getMovieVideosDetail();
            Log.i(TAG,"movieVideosDetail Length" + movieVideosDetail.length);

            ConstraintLayout trailerConstraintLayout = (ConstraintLayout) findViewById (R.id.detail_movie_layout);
            ConstraintSet set = new ConstraintSet();

            ScrollView videoScrollView = new ScrollView(mContext);
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            videoScrollView.addView(linearLayout);
            trailerConstraintLayout.addView(videoScrollView);

            for (int i=0 ; i<movieVideosDetail.length; i++){
                ImageView img = new ImageView(mContext);
                Picasso.with(mContext).load(movieVideosDetail[i].getThumbnailUrl()).into(img);
                Log.i(TAG,"Thumbnail is : "+movieVideosDetail[i].getThumbnailUrl());
                linearLayout.addView(img);
                set.clone(trailerConstraintLayout);
                set.connect(linearLayout.getId(),ConstraintSet.TOP,R.id.trailer_title,ConstraintSet.BOTTOM);
                set.applyTo(trailerConstraintLayout);

            }


            MovieReviewsDetail[] movieReviewsDetails = movieDetails.getMovieReviewsDetail();
            for (int j=0;j<movieReviewsDetails.length;j++){
                TextView authorTitle = new TextView(mContext);
                authorTitle.setText("Author : ");
                TextView author = new TextView(mContext);
                author.setText(movieReviewsDetails[j].getAuthor());
                TextView commentTitle = new TextView(mContext);
                commentTitle.setText("Comment : ");
                TextView comment = new TextView(mContext);
                comment.setText(movieReviewsDetails[j].getContent());

            }
        }
    }


}

