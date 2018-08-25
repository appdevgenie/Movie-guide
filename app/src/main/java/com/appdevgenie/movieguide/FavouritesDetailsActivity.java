package com.appdevgenie.movieguide;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.appdevgenie.movieguide.model.ReviewModel;
import com.appdevgenie.movieguide.model.TrailerModel;
import com.appdevgenie.movieguide.viewModels.FavouritesQueryViewModel;
import com.appdevgenie.movieguide.viewModels.FavouritesQueryViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouritesDetailsActivity extends AppCompatActivity {

    private static final String KEY_FAVOURITE = "isFavourite";

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
    //@BindView(R.id.detailsCoordinatorLayout)
    //CoordinatorLayout coordinatorLayout;

    private MovieDatabase movieDatabase;
    private Context context;
    private Favourite fav;
    private boolean isFavourite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        context = getApplicationContext();

        ButterKnife.bind(this);

        movieDatabase = MovieDatabase.getInstance(context);

        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isFavourite = savedInstanceState == null || savedInstanceState.getBoolean(KEY_FAVOURITE);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constants.KEY_PARSE_FAVOURITE)) {

                int itemId = intent.getIntExtra(Constants.KEY_PARSE_FAVOURITE, Constants.DEFAULT_ID);

                FavouritesQueryViewModelFactory favouritesQueryViewModelFactory = new FavouritesQueryViewModelFactory(movieDatabase, itemId);
                final FavouritesQueryViewModel favouritesQueryViewModel = ViewModelProviders.of(this, favouritesQueryViewModelFactory).get(FavouritesQueryViewModel.class);
                favouritesQueryViewModel.getDetailFavourite().observe(this, new Observer<Favourite>() {
                    @Override
                    public void onChanged(@Nullable Favourite favourite) {

                        populateSheet(favourite);
                        fav = favourite;
                        if (favourite == null) {
                            //close activity if null ie. if movie is removed from database
                            finish();
                        }
                    }
                });
            }
        } else {
            closeOnError();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void populateSheet(Favourite favourite) {

        if (favourite == null) {
            return;
        }

        llTrailers.setVisibility(View.GONE);
        tvTrailerLabel.setVisibility(View.GONE);
        trailersLine.setVisibility(View.GONE);
        llReviews.setVisibility(View.GONE);
        tvReviewLabel.setVisibility(View.GONE);
        reviewsLine.setVisibility(View.GONE);

        Picasso.with(ivThumb.getContext())
                .load(favourite.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(ivThumb);

        tvTitle.setText(favourite.getTitle());
        tvOverview.setText(favourite.getOverview());
        tvReleaseDate.setText(favourite.getReleaseDate());
        ratingBar.setRating(Float.parseFloat(favourite.getUserRating()));
        tvRatingValue.setText(favourite.getUserRating());

        new DownloadTrailersTask(String.valueOf(favourite.getMovieId())) {

            @Override
            protected void onPostExecute(List<TrailerModel> trailerModels) {
                super.onPostExecute(trailerModels);
                populateTrailerViews(trailerModels);
            }
        }.execute();

        new DownloadReviewsTask(String.valueOf(favourite.getMovieId())) {

            @Override
            protected void onPostExecute(List<ReviewModel> reviewModels) {
                super.onPostExecute(reviewModels);
                populateReviewViews(reviewModels);
            }
        }.execute();
    }

    private void onFavClicked() {

        if (isFavourite) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    movieDatabase.favouritesDao().deleteFavourite(fav);
                }
            });
            isFavourite = false;
            Toast.makeText(context, fav.getTitle() + " removed from favourites", Toast.LENGTH_SHORT).show();
            //Snackbar.make(coordinatorLayout, fav.getTitle() + " " + getString(R.string.removed_from_favourites), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void populateTrailerViews(List<TrailerModel> trailers) {

        final LayoutInflater inflater = LayoutInflater.from(FavouritesDetailsActivity.this);

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

        final LayoutInflater inflater = LayoutInflater.from(FavouritesDetailsActivity.this);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_FAVOURITE, isFavourite);
    }
}
