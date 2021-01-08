package com.android.mymovies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mymovies.Movie;
import com.android.mymovies.R;
import com.bumptech.glide.Glide;

public class MovieActivity extends AppCompatActivity {

    private ImageView star;
    private TextView movieHeader;
    private TextView rating;
    private TextView releaseDate;
    private ImageView image;
    private TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Receive Which Movie Was Clicked
        Intent i = getIntent();
        Movie movie = (Movie)i.getSerializableExtra("movie");

        // Initialize Variables
        ImageButton backButton = findViewById(R.id.backButton);
        star = findViewById(R.id.star);
        movieHeader = findViewById(R.id.movieHeader);
        rating = findViewById(R.id.rating);
        releaseDate = findViewById(R.id.releaseDate);
        image = findViewById(R.id.moviePoster);
        overview = findViewById(R.id.info);

        // Set The UI Views With The Movie's Information
        setData(movie);

        // Click Event Listener - When Clicking The 'Back' Button, Close The Current Activity
        // Return To The Main Activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // This Function Sets The Movie's Information To The UI Views
    private void setData(Movie movie) {
        if (movie != null) {
            movieHeader.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            overview.setText(movie.getOverview());
            double movie_rating = movie.getRating();
            if (movie_rating == 0.0) {
                rating.setText("");
                star.setImageResource(R.drawable.ic_grade_black_24dp);
            } else {
                rating.setText(String.valueOf(movie.getRating()));
            }
            String imagePath = "https://image.tmdb.org/t/p/w500" + movie.getImage();
            Glide.with(this)
                    .load(imagePath)
                    .into(image);
        }
    }
}
