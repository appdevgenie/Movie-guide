package com.appdevgenie.movieguide.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY = "api_key";
    private static final String BASE_URL = "http://api.themoviedb.org";
    private static final String GROUP = "3";
    private static final String CATEGORY = "movie";
    private static final String TYPE_TRAILER = "videos";
    private static final String TYPE_REVIEWS = "reviews";

    public static URL buildMoviesUrl(String apiKeyString, String selection) {

        Uri moviesQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(GROUP)
                .appendPath(CATEGORY)
                .appendPath(selection)
                .appendQueryParameter(API_KEY, apiKeyString)
                .build();

        try {
            URL moviesURL = new URL(moviesQueryUri.toString());
            Log.d(TAG, "buildMoviesUrl: " + moviesURL);
            return moviesURL;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static URL buildTrailerUrl(String apiKeyString, String movieId){

        Uri trailerQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(GROUP)
                .appendPath(CATEGORY)
                .appendPath(movieId)
                .appendPath(TYPE_TRAILER)
                .appendQueryParameter(API_KEY, apiKeyString)
                .build();

        try{
            URL trailerURL = new URL(trailerQueryUri.toString());
            Log.d(TAG, "buildTrailerUrl: " + trailerURL);
            return trailerURL;
        } catch (MalformedURLException e){
            return null;
        }
    }

    public static URL buildReviewsUrl(String apiKeyString, String movieId){

        Uri reviewsQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(GROUP)
                .appendPath(CATEGORY)
                .appendPath(movieId)
                .appendPath(TYPE_REVIEWS)
                .appendQueryParameter(API_KEY, apiKeyString)
                .build();

        try{
            URL reviewsURL = new URL(reviewsQueryUri.toString());
            Log.d(TAG, "buildTrailerUrl: " + reviewsURL);
            return reviewsURL;
        } catch (MalformedURLException e){
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {

            InputStream inputStream = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;

        } finally {
            httpURLConnection.disconnect();
        }
    }
}
