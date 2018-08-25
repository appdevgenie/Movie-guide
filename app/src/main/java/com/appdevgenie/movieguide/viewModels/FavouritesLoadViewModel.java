package com.appdevgenie.movieguide.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;


import com.appdevgenie.movieguide.database.Favourite;
import com.appdevgenie.movieguide.database.MovieDatabase;

import java.util.List;

public class FavouritesLoadViewModel extends AndroidViewModel {

    private LiveData<List<Favourite>> favs;

    public FavouritesLoadViewModel(Application application) {
        super(application);

        MovieDatabase movieDatabase = MovieDatabase.getInstance(this.getApplication());
        favs = movieDatabase.favouritesDao().loadFavourites();
    }

    public LiveData<List<Favourite>> getFavs(){
        return favs;
    }
}
