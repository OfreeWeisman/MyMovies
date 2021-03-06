package com.android.mymovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mymovies.Movie;
import com.android.mymovies.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private ArrayList<Movie> movies;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Set A Customized Layout For Each Item (Movie) In The List
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    // This Function Sets The Layout's UI Views With The Information Of Movies At Each Position
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        double movie_rating = movie.getRating();
        if (movie_rating == 0.0) {
            holder.ratings.setText("");
            holder.star.setImageResource(R.drawable.ic_grade_black_24dp);
        } else {
            holder.ratings.setText(String.valueOf(movie.getRating()));
        }
        String imagePath = "https://image.tmdb.org/t/p/w500" + movie.getImage();
        Glide.with(context)
                .load(imagePath)
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Inner Class Defined With The UI Views Of The Customized Layout
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView ratings;
        private ImageView image;
        private ImageView star;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.movieTitle);
            ratings = itemView.findViewById(R.id.movieRating);
            image = itemView.findViewById(R.id.movieImg);
            star = itemView.findViewById(R.id.movie_star_icon);
        }

        // Set 'OnCLick' Method To Items In The List.
        // Invoke The Listener's 'onItemClick' With The Position The Was Clicked To Handle The Event
        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }
}
