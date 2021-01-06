package com.android.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        Intent i = getIntent();
        Movie movie = (Movie)i.getSerializableExtra("movie");
        Toast.makeText(getApplicationContext(), movie.getTitle(),Toast.LENGTH_SHORT).show();

    }
}
