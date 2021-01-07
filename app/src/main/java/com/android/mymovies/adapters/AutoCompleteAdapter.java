package com.android.mymovies.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.android.mymovies.Movie;
import com.android.mymovies.activities.MainActivity;

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

public class AutoCompleteAdapter extends ArrayAdapter implements Filterable {

    private ArrayList<String> suggestions;

    public AutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        suggestions = new ArrayList<>();
    }

    public void setSuggestions(ArrayList<String> list) {
        suggestions.clear();
        suggestions.addAll(list);
    }

    public String getSuggestion(int position) {
        return suggestions.get(position);
    }
    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}
