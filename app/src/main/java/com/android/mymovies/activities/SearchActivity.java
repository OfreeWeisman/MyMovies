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

        // Initialize Variables
        Button findMovies = findViewById(R.id.find_movies);
        ImageButton back = findViewById(R.id.searchBackButton);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        suggestions = new ArrayList<>();
        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);

        // Set The Autocomplete With Threshold - Suggestions Will Be Displayed Only For At Least 2 Letters
        autoCompleteTextView.setThreshold(2);

        // Set The Autocomplete With The Adapter That Will Handle The Suggestions
        autoCompleteTextView.setAdapter(adapter);

        // Event Listener - Define When To Invoke New Requests To The Web API
        // The Requests Are Being Delayed For 0.5 Seconds In Order To Deny Lots Of Requests From Being Sent When The Text Is Changed
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

        // Create A Handler Object To Manage The Requests
        // The Handler Sends The Requests When It Is Being Triggered And The Autocomplete Isn't Empty
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

        // Click Event - When Clicking On The 'Find Movies' Button The Keyword Typed/Selected Is Sent To The Main Screen
        // On The Main Screen, A List Of Movies Which Contains That Keyword Is Displayed
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

        // Click Event -  Clicking The 'Back' Button Closes The Current Activity And The MAin Activity Is Launched With The Default Query
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    // This Function Manage The Http Request And Response
    // The Requests Are Done In The Background And The Response Is Handled In The Foreground (In The UI thread)
    private void sendApiRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Process The Response In The UI Thread
                            parseResponse(myResponse);

                        }
                    });
                }
            }
        });
    }

    // This Function Converts JSON Format String To JSONObject To Read The List Of Movies Received.
    // Each Result Inserted To A String List
    // Set The Adapter With The Results List To Display As Suggestions To The User
    private void parseResponse (String response) {
        try {
            suggestions.clear();
            JSONObject mainObject = new JSONObject(response);
            JSONArray resArray = mainObject.getJSONArray("results");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonMovie = resArray.getJSONObject(i);
                String result = jsonMovie.getString("name");
                suggestions.add(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.setSuggestions(suggestions);
        adapter.notifyDataSetChanged();
    }
}
