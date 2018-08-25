package com.appdevgenie.movieguide.asyncTasks;

import android.os.AsyncTask;

import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.model.ReviewList;
import com.appdevgenie.movieguide.model.ReviewModel;
import com.appdevgenie.movieguide.utils.NetworkUtils;
import com.appdevgenie.movieguide.utils.ReviewJsonUtils;

import java.net.URL;
import java.util.List;

public class DownloadReviewsTask extends AsyncTask<String, Void, List<ReviewModel>> {

    private String movieId;

    protected DownloadReviewsTask(String movieId) {
        this.movieId = movieId;
    }

    @Override
    protected List<ReviewModel> doInBackground(String... strings) {

        URL url = NetworkUtils.buildReviewsUrl(Constants.API_KEY, movieId);

        try {
            String jsonString = null;
            if (url != null) {
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            }

            ReviewList reviewList = ReviewJsonUtils.parseReviewJson(jsonString);

            return reviewList.getReviewList();

        } catch (Exception e) {

            return null;

        }
    }
}
