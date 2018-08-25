package com.appdevgenie.movieguide.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevgenie.movieguide.R;
import com.appdevgenie.movieguide.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainMovieListAdapter extends RecyclerView.Adapter<MainMovieListAdapter.MovieAdapterViewHolder> {

    private Context ctx;
    private List<MovieModel> movieModelList;
    private MovieClickedListener movieClickedListener;

    public MainMovieListAdapter(@NonNull Context context, MovieClickedListener listener) {
        this.ctx = context;
        this.movieClickedListener = listener;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(ctx).inflate(R.layout.item_main_image, viewGroup, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder movieAdapterViewHolder, int i) {

        int position = movieAdapterViewHolder.getAdapterPosition();

        Picasso.with(ctx)
                .load(movieModelList.get(position).getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(movieAdapterViewHolder.ivThumbnail);

        movieAdapterViewHolder.tvMovieTitle.setText(movieModelList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {

        if (movieModelList == null) {
            return 0;
        }
        return movieModelList.size();
    }

    public void setAdapterData(List<MovieModel> modelList) {
        movieModelList = modelList;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivThumbnail;
        private TextView tvMovieTitle;

        public MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.ivMainImageItem);
            tvMovieTitle = itemView.findViewById(R.id.tvMainImageItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            MovieModel selectedMovie = movieModelList.get(getAdapterPosition());
            movieClickedListener.onMovieClicked(selectedMovie);
        }
    }

    public interface MovieClickedListener {
        void onMovieClicked(MovieModel selected);
    }
}
