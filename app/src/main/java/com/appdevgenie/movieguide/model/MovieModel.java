package com.appdevgenie.movieguide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.appdevgenie.movieguide.constants.Constants;

public class MovieModel implements Parcelable {

    private int movieId;
    private String title;
    private String thumbnail;
    private String overview;
    private String userRating;
    private String releaseDate;

    public MovieModel() {
    }

    public MovieModel(int movieId, String title, String thumbnail, String overview, String userRating, String releaseDate) {

        this.movieId = movieId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    protected MovieModel(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        thumbnail = in.readString();
        overview = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int id) {
        this.movieId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {

        return Constants.IMAGE_URL + thumbnail;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(title);
        parcel.writeString(thumbnail);
        parcel.writeString(overview);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
    }
}
