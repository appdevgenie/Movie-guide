package com.appdevgenie.movieguide.viewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.appdevgenie.movieguide.database.MovieDatabase;

public class FavouritesQueryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private MovieDatabase movieDatabase;
    private int movieId;

    public FavouritesQueryViewModelFactory(MovieDatabase movieDatabase, int movieId) {
        this.movieDatabase = movieDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        //noinspection unchecked
        return (T) new FavouritesQueryViewModel(movieDatabase, movieId);
    }
}
