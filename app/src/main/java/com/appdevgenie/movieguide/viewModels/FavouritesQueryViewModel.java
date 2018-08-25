package com.appdevgenie.movieguide.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.appdevgenie.movieguide.database.Favourite;
import com.appdevgenie.movieguide.database.MovieDatabase;

import java.util.List;

public class FavouritesQueryViewModel extends ViewModel {

    private LiveData<List<Favourite>> queryFavourites;
    private LiveData<Favourite> loadDetailFavourites;

    public FavouritesQueryViewModel(MovieDatabase movieDatabase, int movieId) {

        queryFavourites = movieDatabase.favouritesDao().loadFavouriteByMovieId(movieId);
        loadDetailFavourites = movieDatabase.favouritesDao().loadFavouriteById(movieId);
    }

    public LiveData<List<Favourite>> checkFavourites(){
        return queryFavourites;
    }

    public LiveData<Favourite> getDetailFavourite(){
        return loadDetailFavourites;
    }
}
