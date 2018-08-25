package com.appdevgenie.movieguide.model;

import com.appdevgenie.movieguide.model.MovieModel;

import java.util.List;

public class MovieList {

    private List<MovieModel> movieList;

    public List<MovieModel> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<MovieModel> movieList) {
        this.movieList = movieList;
    }

    @Override
    public String toString() {
        return "MovieList: " + movieList;
    }
}
