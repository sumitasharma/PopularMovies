package com.example.android.popularmoviesstage1sumita.utils;

/**
 * Class to represent movie data.
 */
public class MovieDetails {
    private String movieTitle;
    private int id;
    private String synopsis;
    private String releaseDate;
    private String rating;
    private String posterPath;

    public String getMovieTitle() {
        return movieTitle;
    }

    void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
