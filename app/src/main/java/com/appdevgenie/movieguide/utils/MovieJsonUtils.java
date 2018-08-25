package com.appdevgenie.movieguide.utils;

import android.util.Log;

import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.model.MovieList;
import com.appdevgenie.movieguide.model.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    public static MovieList parseMovieJson(String json) {

        List<MovieModel> movieModelList = new ArrayList<>();
        MovieList movieList = new MovieList();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Constants.KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject movieObject = jsonArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel(
                        movieObject.getInt(Constants.KEY_MOVIE_ID),
                        movieObject.getString(Constants.KEY_TITLE),
                        movieObject.getString(Constants.KEY_IMAGE),
                        movieObject.getString(Constants.KEY_OVERVIEW),
                        movieObject.getString(Constants.KEY_RATING),
                        movieObject.getString(Constants.KEY_RELEASE_DATE));

                movieModelList.add(movieModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseMovieJson: error" + e.toString());
        }

        movieList.setMovieList(movieModelList);
        return movieList;
    }
}
