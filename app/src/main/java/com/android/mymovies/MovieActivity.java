package com.android.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MovieActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView movieHeader;
    private TextView rating;
    private TextView releaseDate;
    private ImageView image;
    private TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent i = getIntent();
        Movie movie = (Movie)i.getSerializableExtra("movie");

        backButton = findViewById(R.id.backButton);
        movieHeader = findViewById(R.id.movieHeader);
        String imagePath = "https://image.tmdb.org/t/p/w500" + movie.getImage();
        rating = findViewById(R.id.rating);
        releaseDate = findViewById(R.id.releaseDate);
        image = findViewById(R.id.moviePoster);
        overview = findViewById(R.id.info);

        movieHeader.setText(movie.getTitle());
        rating.setText(String.valueOf(movie.getRating()));
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        Glide.with(this)
                .load(imagePath)
                .into(image);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieActivity.this, MainActivity.class);
                finish();
                startActivity(intent);

            }
        });
    }
}
