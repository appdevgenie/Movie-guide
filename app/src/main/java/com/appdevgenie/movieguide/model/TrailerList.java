package com.appdevgenie.movieguide.model;

import java.util.List;

public class TrailerList {

    private List<TrailerModel> trailerList;

    public List<TrailerModel> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<TrailerModel> trailerList) {
        this.trailerList = trailerList;
    }

    @Override
    public String toString() {
        return "TrailerList: " + trailerList;
    }
}
