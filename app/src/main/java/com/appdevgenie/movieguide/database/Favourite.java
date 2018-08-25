package com.appdevgenie.movieguide.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Favourites")
public class Favourite {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int movieId;
    private String title;
    private String thumbnail;
    private String overview;
    private String userRating;
    private String releaseDate;

    @Ignore
    public Favourite(int movieId, String title, String thumbnail, String overview, String userRating, String releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Favourite(int id, int movieId, String title, String thumbnail, String overview, String userRating, String releaseDate) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
