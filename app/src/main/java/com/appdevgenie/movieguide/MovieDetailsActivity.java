package com.appdevgenie.movieguide;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.movieguide.asyncTasks.DownloadReviewsTask;
import com.appdevgenie.movieguide.asyncTasks.DownloadTrailersTask;
import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.database.Favourite;
import com.appdevgenie.movieguide.database.MovieDatabase;
import com.appdevgenie.movieguide.executors.AppExecutors;
import com.appdevgenie.movieguide.model.MovieModel;
import com.appdevgenie.movieguide.model.ReviewModel;
import com.appdevgenie.movieguide.model.TrailerModel;
import com.appdevgenie.movieguide.viewModels.FavouritesQueryViewModel;
import com.appdevgenie.movieguide.viewModels.FavouritesQueryViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tvMovieDetailsTitle)
    TextView tvTitle;
    @BindView(R.id.ivDetailsImage)
    ImageView ivThumb;
    @BindView(R.id.rbMovieDetailsRating)
    RatingBar ratingBar;
    @BindView(R.id.tvMovieDetailsRatingValue)
    TextView tvRatingValue;
    @BindView(R.id.tvMovieDetailsReleaseDate)
    TextView tvReleaseDate;
    @BindView(R.id.tvMovieDetailsOverview)
    TextView tvOverview;
    @BindView(R.id.layout_trailers)
    LinearLayout llTrailers;
    @BindView(R.id.layout_reviews)
    LinearLayout llReviews;
    @BindView(R.id.tvMovieDetailsTrailer)
    TextView tvTrailerLabel;
    @BindView(R.id.tvMovieDetailsReviews)
    TextView tvReviewLabel;
    @BindView(R.id.view_trailers_line)
    View trailersLine;
    @BindView(R.id.view_reviews_line)
    View reviewsLine;
    @BindView(R.id.detailsCoordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private MovieModel movieModel;
    private MovieDatabase movieDatabase;
    private Favourite favourite;
    private boolean isFavourite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        Context context = getApplicationContext();

        ButterKnife.bind(this);

        movieDatabase = MovieDatabase.getInstance(context);

        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constants.KEY_PARSE_MOVIE)) {
                movieModel = intent.getParcelableExtra(Constants.KEY_PARSE_MOVIE);
                populateUI();
                checkIfFavourite();
            }
        } else {
            closeOnError();
        }
    }

    private void onFavClicked() {

        String movieTitle = movieModel.getTitle();
        favourite = new Favourite(movieModel.getMovieId(), movieTitle, movieModel.getThumbnail(), movieModel.getOverview(), movieModel.getUserRating(), movieModel.getReleaseDate());

        Snackbar snackbar;
        if (!isFavourite) {

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    movieDatabase.favouritesDao().insertFavourite(favourite);
                }
            });

            isFavourite = true;
            //Log.d("fav", "run: fav insert " + movieTitle);
            //Toast.makeText(context, movieTitle + " added to favourites", Toast.LENGTH_SHORT).show();
            snackbar =
                    Snackbar.make(coordinatorLayout, movieTitle + " " + getString(R.string.added_to_favourites), Snackbar.LENGTH_SHORT);
            View view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.colorGold));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            snackbar.show();

        } else {

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    movieDatabase.favouritesDao().deleteFav(movieModel.getMovieId());
                }
            });

            isFavourite = false;
            //Log.d("fav", "run: fav del " + movieTitle);
            //Toast.makeText(context, movieTitle + " removed from favourites", Toast.LENGTH_SHORT).show();
            snackbar =
                    Snackbar.make(coordinatorLayout, movieTitle + " " + getString(R.string.removed_from_favourites), Snackbar.LENGTH_SHORT);
            View view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            snackbar.show();
        }
    }

    private void checkIfFavourite() {
        //query db for movie id, if exists movie is a favourite, else not favourite
        FavouritesQueryViewModelFactory favouritesQueryViewModelFactory = new FavouritesQueryViewModelFactory(movieDatabase, movieModel.getMovieId());
        FavouritesQueryViewModel favouritesQueryViewModel = ViewModelProviders.of(this, favouritesQueryViewModelFactory).get(FavouritesQueryViewModel.class);
        favouritesQueryViewModel.checkFavourites().observe(this, new Observer<List<Favourite>>() {
            @Override
            public void onChanged(@Nullable List<Favourite> favourites) {

                if (favourites != null) {
                    if (favourites.size() == 0) {
                        isFavourite = false;
                    } else {
                        isFavourite = true;
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void populateUI() {

        if (movieModel == null) {
            return;
        }

        llTrailers.setVisibility(View.GONE);
        tvTrailerLabel.setVisibility(View.GONE);
        trailersLine.setVisibility(View.GONE);
        llReviews.setVisibility(View.GONE);
        tvReviewLabel.setVisibility(View.GONE);
        reviewsLine.setVisibility(View.GONE);

        Picasso.with(ivThumb.getContext())
                .load(movieModel.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(ivThumb);

        tvTitle.setText(movieModel.getTitle());
        ratingBar.setRating(Float.parseFloat(movieModel.getUserRating()));
        tvRatingValue.setText(movieModel.getUserRating());
        tvReleaseDate.setText(movieModel.getReleaseDate());
        tvOverview.setText(movieModel.getOverview());

        new DownloadTrailersTask(String.valueOf(movieModel.getMovieId())) {
            @Override
            protected void onPostExecute(List<TrailerModel> trailerModels) {
                super.onPostExecute(trailerModels);
                populateTrailerViews(trailerModels);
            }
        }.execute();

        new DownloadReviewsTask(String.valueOf(movieModel.getMovieId())) {
            @Override
            protected void onPostExecute(List<ReviewModel> reviewModels) {
                super.onPostExecute(reviewModels);
                populateReviewViews(reviewModels);
            }
        }.execute();
    }

    private void populateTrailerViews(List<TrailerModel> trailers) {

        final LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);

        if (trailers != null && !trailers.isEmpty()) {
            llTrailers.setVisibility(View.VISIBLE);
            tvTrailerLabel.setVisibility(View.VISIBLE);
            trailersLine.setVisibility(View.VISIBLE);

            for (final TrailerModel trailerModel : trailers) {
                if (TextUtils.equals(trailerModel.getSite(), Constants.KEY_YOU_TUBE)) {
                    View trailerView = inflater.inflate(R.layout.item_trailer, llTrailers, false);
                    TextView tvTrailer = trailerView.findViewById(R.id.tvMovieDetailsTrailerName);
                    tvTrailer.setText(trailerModel.getName());

                    llTrailers.addView(trailerView);

                    trailerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(Constants.YOU_TUBE_URL + trailerModel.getKey()));
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    private void populateReviewViews(List<ReviewModel> reviews) {

        final LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);

        if (reviews != null && !reviews.isEmpty()) {
            llReviews.setVisibility(View.VISIBLE);
            tvReviewLabel.setVisibility(View.VISIBLE);
            reviewsLine.setVisibility(View.VISIBLE);

            for (final ReviewModel reviewModel : reviews) {
                View reviewView = inflater.inflate(R.layout.item_review, llReviews, false);
                TextView tvAuthor = reviewView.findViewById(R.id.tvReviewItemAuthor);
                TextView tvContent = reviewView.findViewById(R.id.tvReviewItemComment);
                tvAuthor.setText(reviewModel.getAuthor());
                tvContent.setText(reviewModel.getContent());

                llReviews.addView(reviewView);

                reviewView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(reviewModel.getUrl()));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_update_favourite:
                invalidateOptionsMenu();
                onFavClicked();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isFavourite) {
            menu.getItem(0).setIcon(R.drawable.ic_fav_on);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_fav_off);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(getApplicationContext(), R.string.data_unavailable, Toast.LENGTH_SHORT).show();
    }
}
