package com.appdevgenie.movieguide.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavouritesDao {

    @Insert
    void insertFavourite(Favourite favourite);

    @Delete
    void deleteFavourite(Favourite favourite);

    @Update (onConflict = OnConflictStrategy.REPLACE)
    void updateFavourite(Favourite favourite);

    @Query("SELECT * FROM favourites WHERE id = :id")
    LiveData<Favourite> loadFavouriteById(int id);

    @Query("SELECT * FROM favourites WHERE movieId = :movieId")
    LiveData<List<Favourite>> loadFavouriteByMovieId(int movieId);

    @Query("DELETE FROM favourites WHERE movieId = :movieId")
    void deleteFav(int movieId);

    @Query("SELECT * FROM favourites")
    LiveData<List<Favourite>> loadFavourites();
}
