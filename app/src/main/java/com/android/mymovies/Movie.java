package com.android.mymovies;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String overview;
    private String releaseDate;
    private double rating;
    private String image;

    public Movie(String title, String overview, String releaseDate, double rating, String image) {
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getOverview() { return overview; }

    public String getReleaseDate() { return releaseDate; }

    public double getRating() {
        return rating;
    }
}


