package com.android.mymovies.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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
    private ImageButton searchButton;
    private NestedScrollView scrollView;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;
    private int page = 1;
    private String url = "https://api.themoviedb.org/3/discover/movie?api_key=6da65f3de080488aba7cb19a8e1601ce&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.toolbar_search_button);
        scrollView = findViewById(R.id.scrollView);
        movies = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        // Check If We Received A Query To Display Movies By.
        // Otherwise Use The Default Query
        Intent i = getIntent();
        String intentExtra = i.getStringExtra("query");
        if (intentExtra != null) {
            String apiKey = "api_key=6da65f3de080488aba7cb19a8e1601ce";
            String searchApiUrl = "https://api.themoviedb.org/3/search/movie?";
            String params = "&language=en-US&include_adult=false&query=";
            url = searchApiUrl + apiKey + params + intentExtra + "&page=";
        }

        // Sending The First Request To Bring The First Page Of Movies
        sendApiRequest(url, page++);

        // Event Listening - When Reaching The Bottom Of The ScrollView, Bring The Next Page Of Movies
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY >= v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    sendApiRequest(url, page++);
                }
            }
        });

        // Click Event - When Clicking The Search Button, Launch The Search Activity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    // This Function Manage The Http Request And Response
    // The Requests Are Done In The Background And The Response Is Handled In The Foreground (In The UI thread)
    private void sendApiRequest(String url, int page) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url+page)
                .build();

        // The 'call.enqueue()' Works In The Background
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    // Process The Response In The UI Thread To Display The Data
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(myResponse);
                            displayMovies();
                        }
                    });
                }
            }
        });
    }

    // This Function Converts JSON Format String To JSONObject To Read The List Of Movies Received.
    // For Each Result, Creates A Movie Object With The Related Information And Adds It To The Movies List
    private void parseResponse (String response) {
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray resArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonMovie = resArray.getJSONObject(i);
                String image = jsonMovie.getString("poster_path");
                // Insert To The List Only Movies With Poster
                if (image.equals("null")) {
                    continue;
                }
                String title = jsonMovie.getString("title");
                String overview = jsonMovie.getString("overview");
                String release_date = jsonMovie.getString("release_date");
                double rating = jsonMovie.getDouble("vote_average");
                Movie movie = new Movie(title, overview, release_date, rating, image);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // This Function Sets An Adapter To The RecyclerView With The Movies List To Display
    private void displayMovies() {
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(adapter);

        // Event Listener - When Clicking On A List Item (Movie) Launch The Movie Activity To Display More Information About The Selected Movie
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