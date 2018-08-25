package com.appdevgenie.movieguide.asyncTasks;

import android.os.AsyncTask;

import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.model.TrailerList;
import com.appdevgenie.movieguide.model.TrailerModel;
import com.appdevgenie.movieguide.utils.NetworkUtils;
import com.appdevgenie.movieguide.utils.TrailerJsonUtils;

import java.net.URL;
import java.util.List;

public class DownloadTrailersTask extends AsyncTask<String, Void, List<TrailerModel>> {

    private String movieId;

    protected DownloadTrailersTask(String movieId) {
        this.movieId = movieId;
    }

    @Override
    protected List<TrailerModel> doInBackground(String... strings) {

        URL url = NetworkUtils.buildTrailerUrl(Constants.API_KEY, movieId);

        try {
            String jsonString = null;
            if (url != null) {
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            }

            TrailerList trailerList = TrailerJsonUtils.parseTrailerJson(jsonString);

            return trailerList.getTrailerList();

        } catch (Exception e) {

            return null;

        }
    }
}
