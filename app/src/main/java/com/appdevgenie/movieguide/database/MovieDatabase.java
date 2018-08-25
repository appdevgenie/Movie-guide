package com.appdevgenie.movieguide.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Favourite.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String TAG = MovieDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "movie_db";
    private static final Object LOCK = new Object();
    private static MovieDatabase dbInstance;

    public static MovieDatabase getInstance(Context context){

        if(dbInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "getInstance: creating new database instance");
                dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, MovieDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return dbInstance;
    }

    public abstract FavouritesDao favouritesDao();
}
