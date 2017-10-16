package com.example.android.popularmoviesstage1sumita.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.popularmoviesstage1sumita";

    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "favorite_movies";

    public static class MovieEntry implements BaseColumns {

        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_ID = "movieID";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POSTER_PATH = "posterPath";

    }

}
