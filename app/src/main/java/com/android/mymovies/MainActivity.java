package com.android.mymovies;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        pages = 0;

        String apiKey = "api_key=6da65f3de080488aba7cb19a8e1601ce";
        String apiUrl = "https://api.themoviedb.org/3/discover/movie?";
        String input = "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=";
        String page = "1";
        String url = apiUrl + apiKey + input + page;
        //Toast.makeText(MainActivity.this, "onCreate" + movies.size(), Toast.LENGTH_SHORT).show();
        sendApiRequest(url);

//        for(int i = 1; i <= 3; i++) {
//            String new_url = apiUrl + apiKey + input + i;
//            sendApiRequest(new_url);
//        }

    }

    private void sendApiRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                            try {
                                JSONObject mainObject = new JSONObject(myResponse);
                                pages = mainObject.getInt("total_pages");
                                JSONArray resArray = mainObject.getJSONArray("results");
                                for (int i = 0; i < resArray.length(); i++) {
                                    JSONObject jsonMovie = resArray.getJSONObject(i);
                                    String id = jsonMovie.getString("id");
                                    String title = jsonMovie.getString("title");
                                    String overview = jsonMovie.getString("overview");
                                    String release_date = jsonMovie.getString("release_date");
                                    double rating = jsonMovie.getDouble("vote_average");
                                    String image = jsonMovie.getString("poster_path");
                                    Movie movie = new Movie(id, title, overview, release_date, rating, image);
                                    movies.add(movie);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            displayMovies();
                        }
                    });
                }
            }
        });

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