package com.android.mymovies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.mymovies.Movie;
import com.android.mymovies.R;
import com.android.mymovies.adapters.AutoCompleteAdapter;

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

public class SearchActivity extends AppCompatActivity {
    private ArrayList<String> suggestions;
    private AutoCompleteTextView autoCompleteTextView;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500;
    private Handler handler;
    private AutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button findMovies = findViewById(R.id.find_movies);
        ImageButton back = findViewById(R.id.searchBackButton);
        suggestions = new ArrayList<>();
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        String url = "https://api.themoviedb.org/3/search/keyword?api_key=6da65f3de080488aba7cb19a8e1601ce&page=1&query=" + autoCompleteTextView.getText().toString();
                        sendApiRequest(url);
                    }
                }
                return false;
            }
        });

        findMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                if(!autoCompleteTextView.getText().toString().isEmpty()) {
                    intent.putExtra("query", autoCompleteTextView.getText().toString());
                }
                finish();
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });


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
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseResponse(myResponse);

                        }
                    });
                }
            }
        });

    }

    private void parseResponse (String response) {
        try {
            suggestions.clear();
            JSONObject mainObject = new JSONObject(response);
            JSONArray resArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonMovie = resArray.getJSONObject(i);
                String title = jsonMovie.getString("name");
                suggestions.add(title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.setSuggestions(suggestions);
        adapter.notifyDataSetChanged();
    }
}
