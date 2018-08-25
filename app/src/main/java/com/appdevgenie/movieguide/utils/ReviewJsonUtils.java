package com.appdevgenie.movieguide.utils;

import android.util.Log;

import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.model.ReviewList;
import com.appdevgenie.movieguide.model.ReviewModel;
import com.appdevgenie.movieguide.model.TrailerList;
import com.appdevgenie.movieguide.model.TrailerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewJsonUtils {

    private static final String TAG = ReviewJsonUtils.class.getSimpleName();

    public static ReviewList parseReviewJson(String json) {

        List<ReviewModel> reviewModelList = new ArrayList<>();
        ReviewList reviewList = new ReviewList();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Constants.KEY_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject reviewObject = jsonArray.getJSONObject(i);
                ReviewModel reviewModel = new ReviewModel(
                        reviewObject.getString(Constants.KEY_REVIEW_AUTHOR),
                        reviewObject.getString(Constants.KEY_REVIEW_CONTENT),
                        reviewObject.getString(Constants.KEY_REVIEW_URL)
                );
                reviewModelList.add(reviewModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseReviewJson: error" + e.toString());
        }

        reviewList.setReviewList(reviewModelList);

        return reviewList;
    }

}

