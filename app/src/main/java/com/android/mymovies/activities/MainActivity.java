package com.android.mymovies.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mymovies.Movie;
import com.android.mymovies.R;
import com.android.mymovies.adapters.MoviesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private ImageButton searchButoon;
    private NestedScrollView scrollView;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;
    private int page = 1;
    private int scroll = -1;
    private String url = "https://api.themoviedb.org/3/discover/movie?api_key=6da65f3de080488aba7cb19a8e1601ce&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButoon = findViewById(R.id.toolbar_search_button);
        scrollView = findViewById(R.id.scrollView);
        movies = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        String apiKey = "api_key=6da65f3de080488aba7cb19a8e1601ce";
        String defaultApiUrl = "https://api.themoviedb.org/3/discover/movie?";
        String searchApiUrl = "https://api.themoviedb.org/3/search/movie?";
        String input = "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=";

        //check if we received query to display movies by, using intent:
        Intent i = getIntent();
        String intentExtra = i.getStringExtra("query");
        if (intentExtra != null) {
            url = searchApiUrl + apiKey + "&language=en-US&include_adult=false&query=" + intentExtra + "&page=";
        }

        sendApiRequest(url, page++);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY >= v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    sendApiRequest(url, page++);
                }
            }
        });

        searchButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }


    private void sendApiRequest(String url, int page) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url+page)
                .build();

        // enqueue runs the request in the background
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(myResponse);
                            displayMovies();
                            scroll = -1;
                        }
                    });
                }
            }
        });

    }

    private void parseResponse (String response) {
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray resArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonMovie = resArray.getJSONObject(i);
                String image = jsonMovie.getString("poster_path");
                if (image.equals("null")) {
                    continue;
                }
                String id = jsonMovie.getString("id");
                String title = jsonMovie.getString("title");
                String overview = jsonMovie.getString("overview");
                String release_date = jsonMovie.getString("release_date");
                double rating = jsonMovie.getDouble("vote_average");

                Movie movie = new Movie(id, title, overview, release_date, rating, image);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayMovies() {

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Movie movie = movies.get(position);
                Intent i = new Intent(getApplicationContext(), MovieActivity.class);
                i.putExtra("movie", movie);
                startActivity(i);
            }
        });

    }

}