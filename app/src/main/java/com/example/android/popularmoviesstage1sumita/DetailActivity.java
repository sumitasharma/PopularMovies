package com.example.android.popularmoviesstage1sumita;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmoviesstage1sumita.data.MovieContract;
import com.example.android.popularmoviesstage1sumita.utils.DetailMovieLoader;
import com.example.android.popularmoviesstage1sumita.utils.MovieDetails;
import com.example.android.popularmoviesstage1sumita.utils.MovieReviewsDetail;
import com.example.android.popularmoviesstage1sumita.utils.MovieVideosDetail;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesstage1sumita.utils.MoviesUtil.TRAILER_URL;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, DetailMovieLoader.MovieDetailAsyncResponse {
    private static final int MOVIE_DETAILS = 15;
    private static final String WHERE_CLAUSE = "movieID = ";
    private final String POSTER_PATH = "posterpath";
    private final String TAG = DetailActivity.class.getSimpleName();
    private final LoaderManager.LoaderCallbacks<String[]> callback = DetailActivity.this;
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieSynopsis;
    private TextView movieRatings;
    private TextView movieReleaseDate;
    private String movieId;
    private Button saveAsFavorite;
    private String where_clause;
    private MovieDetails mMovieDetail;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_detail_main);
        Cursor mCursor;

        //Bundle extras = getIntent().getExtras();
        movieId = getIntent().getStringExtra("MovieId");
        Log.i(TAG, "Movie Id Clicked:" + movieId);

        setContentView(R.layout.activity_detail_main);
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


        // FetchFavoriteMovieDetails function is called for the Movie Details of the Movie clicked on Main Menu by passing
        // the MovieId this Activity received from onClick function.

        initializeLoader();
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

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "onCreateLoader");
        return new DetailMovieLoader(this, this, movieId);
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(POSTER_PATH, mMovieDetail.getPosterPath());
        editor.apply();
        movieTitle.setText(mMovieDetail.getMovieTitle());
        movieRatings.setText(mMovieDetail.getRating() + " / 10");
        movieReleaseDate.setText(mMovieDetail.getReleaseDate());
        movieSynopsis.setText(mMovieDetail.getSynopsis());
        Picasso.with(mContext).load(mMovieDetail.getPosterPath()).into(moviePoster);
        Log.i(TAG, "Movie Title :" + movieTitle.getText());

        // Display the Trailers for the Movie
        final MovieVideosDetail[] movieVideosDetails = mMovieDetail.getMovieVideosDetail();
        LinearLayout videoLinearLayout = (LinearLayout) findViewById(R.id.movie_video);
        //for (int i = 0; i < movieVideosDetail.length; i++) {
        for (MovieVideosDetail movieVideosDetail : movieVideosDetails) {
            ImageView img = new ImageView(mContext);
            Picasso.with(mContext).load(movieVideosDetail.getThumbnailUrl()).into(img);
            // Picasso.with(mContext).load(movieVideosDetail[i].getThumbnailUrl()).into(img);
            img.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            img.setPadding(20, 20, 20, 20);
            final String movieUrl = movieVideosDetail.getTrailerUrl();
            //final String movieUrl = movieVideosDetail[i].getTrailerUrl();
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieUrl));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(TRAILER_URL + movieUrl));
                    try {
                        startActivity(videoIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                }
            });
            Log.i(TAG, "Thumbnail is : " + movieVideosDetail.getThumbnailUrl());
            videoLinearLayout.addView(img);
        }

        // Display the Reviews for the Movie
        MovieReviewsDetail[] movieReviewsDetails = mMovieDetail.getMovieReviewsDetail();
        LinearLayout reviewLinearLayout = (LinearLayout) findViewById(R.id.movie_review);
        for (MovieReviewsDetail movieReviewsDetail : movieReviewsDetails) {
            TextView authorTitle = new TextView(mContext);
            authorTitle.setTypeface(null, Typeface.BOLD_ITALIC);
            authorTitle.setText(R.string.author_title);
            authorTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            authorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            authorTitle.setPadding(20, 20, 20, 10);
            reviewLinearLayout.addView(authorTitle);
            TextView author = new TextView(mContext);
            author.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            author.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            author.setPadding(20, 10, 20, 10);
            author.setText(movieReviewsDetail.getAuthor());
            reviewLinearLayout.addView(author);
            TextView commentTitle = new TextView(mContext);
            commentTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            commentTitle.setTypeface(null, Typeface.BOLD_ITALIC);
            commentTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            commentTitle.setPadding(20, 20, 20, 10);
            commentTitle.setText(R.string.comments);
            reviewLinearLayout.addView(commentTitle);
            TextView comment = new TextView(mContext);
            comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            comment.setText(movieReviewsDetail.getContent());
            comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            comment.setPadding(20, 10, 20, 100);
            reviewLinearLayout.addView(comment);

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void initializeLoader() {

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_DETAILS);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_DETAILS, null, callback);
        } else {
            loaderManager.restartLoader(MOVIE_DETAILS, null, callback);
        }

    }

    /**
     * Setting all the details in the XML file
     */
    public void processFinishFavorite(MovieDetails movieDetails, Context context) {
        this.mContext = context;
        this.mMovieDetail = movieDetails;
    }

}

