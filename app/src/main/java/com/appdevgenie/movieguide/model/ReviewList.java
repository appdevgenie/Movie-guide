package com.appdevgenie.movieguide.model;

import java.util.List;

public class ReviewList {

    private List<ReviewModel> reviewList;

    public List<ReviewModel> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public String toString() {
        return "ReviewList: " + reviewList;
    }
}