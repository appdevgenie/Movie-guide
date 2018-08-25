package com.appdevgenie.movieguide.utils;

import android.util.Log;

import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.model.TrailerList;
import com.appdevgenie.movieguide.model.TrailerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrailerJsonUtils {

    private static final String TAG = TrailerJsonUtils.class.getSimpleName();

    public static TrailerList parseTrailerJson(String json) {

        List<TrailerModel> trailerModelList = new ArrayList<>();
        TrailerList trailerList = new TrailerList();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Constants.KEY_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject trailerObject = jsonArray.getJSONObject(i);
                TrailerModel trailerModel = new TrailerModel(
                        trailerObject.getString(Constants.KEY_TRAILER_ID),
                        trailerObject.getString(Constants.KEY_TRAILER_KEY),
                        trailerObject.getString(Constants.KEY_TRAILER_NAME),
                        trailerObject.getString(Constants.KEY_TRAILER_SITE),
                        trailerObject.getString(Constants.KEY_TRAILER_TYPE)
                );
                trailerModelList.add(trailerModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseTrailerJson: error" + e.toString());
        }

        trailerList.setTrailerList(trailerModelList);

        return trailerList;
    }

}
