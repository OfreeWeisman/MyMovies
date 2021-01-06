package com.android.mymovies;

import java.io.Serializable;
import java.net.URL;

public class Movie implements Serializable {
    private String id;
    private String title;
    private String overview;
    private String releaseDate;
    private double rating;
    private String image;

    public double getRating() {
        return rating;
    }

    public Movie(String id, String title, String overview, String releaseDate, double rating, String image) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.image = image;
    }

    public Movie() {
        this.id = "";
        this.title = "";
        this.overview = "";
        this.releaseDate = "";
        this.rating = 0;
        this.image = "";
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}


