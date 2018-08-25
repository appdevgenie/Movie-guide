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
import com.appdevgenie.movieguide.database.Favourite;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.FavouriteHolder> {

    private Context context;
    private FavouriteClickListener favouriteClickListener;
    private List<Favourite> favouriteList;

    public FavouritesListAdapter(Context context, FavouriteClickListener favouriteClickListener) {
        this.context = context;
        this.favouriteClickListener = favouriteClickListener;
    }

    @NonNull
    @Override
    public FavouritesListAdapter.FavouriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_image, parent, false);
        return new FavouriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesListAdapter.FavouriteHolder holder, int position) {
        Favourite favourite = favouriteList.get(holder.getAdapterPosition());
        Picasso.with(context)
                .load(favourite.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(holder.ivThumbnail);
        holder.tvMovieTitle.setText(favourite.getTitle());
    }

    @Override
    public int getItemCount() {
        if(favouriteList == null) {
            return 0;
        }
        return favouriteList.size();
    }

    public void setAdapterData(List<Favourite> favList) {
        favouriteList = favList;
        notifyDataSetChanged();
    }

    class FavouriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivThumbnail;
        private TextView tvMovieTitle;

        public FavouriteHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivMainImageItem);
            tvMovieTitle = itemView.findViewById(R.id.tvMainImageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int selectedMovie = favouriteList.get(getAdapterPosition()).getId();
            favouriteClickListener.onFavClickListener(selectedMovie);
        }
    }

    public interface FavouriteClickListener {
        void onFavClickListener(int selectedFav);
    }
}
