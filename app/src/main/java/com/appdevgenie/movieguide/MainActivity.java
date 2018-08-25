package com.appdevgenie.movieguide;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appdevgenie.movieguide.adapters.FavouritesListAdapter;
import com.appdevgenie.movieguide.adapters.MainMovieListAdapter;
import com.appdevgenie.movieguide.constants.Constants;
import com.appdevgenie.movieguide.database.Favourite;
import com.appdevgenie.movieguide.model.MovieList;
import com.appdevgenie.movieguide.model.MovieModel;
import com.appdevgenie.movieguide.utils.MovieJsonUtils;
import com.appdevgenie.movieguide.utils.NetworkUtils;
import com.appdevgenie.movieguide.viewModels.FavouritesLoadViewModel;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements MainMovieListAdapter.MovieClickedListener, FavouritesListAdapter.FavouriteClickListener {

    private static final String RECYCLER_LAYOUT_STATE = "rvState";

    @BindView(R.id.rvMovieList)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Toolbar toolbar;
    private Context context;
    private String selection;
    private MainMovieListAdapter mainMovieListAdapter;
    private FavouritesListAdapter favouritesListAdapter;
    private Parcelable recyclerLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grid);

        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        progressBar.setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, calculateSpan());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        mainMovieListAdapter = new MainMovieListAdapter(context, this);
        favouritesListAdapter = new FavouritesListAdapter(context, this);

        if (savedInstanceState == null) {
            selection = Constants.SELECT_POPULAR;
            populateList();
        } else {
            selection = savedInstanceState.getString(Constants.KEY_MENU_SELECTION, Constants.SELECT_POPULAR);
            if(TextUtils.equals(selection, Constants.SELECT_FAVOURITE)){
                setupFavList();
            }else{
                populateList();
            }
        }
    }

    private int calculateSpan() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.menu_select_popular:
                selection = Constants.SELECT_POPULAR;
                populateList();
                return true;

            case R.id.menu_select_rated:
                selection = Constants.SELECT_TOP_RATED;
                populateList();
                return true;

            case R.id.menu_select_favorites:
                selection = Constants.SELECT_FAVOURITE;
                setupFavList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupFavList() {
        recyclerView.setAdapter(favouritesListAdapter);

        FavouritesLoadViewModel favouritesLoadViewModel = ViewModelProviders.of(this).get(FavouritesLoadViewModel.class);
        favouritesLoadViewModel.getFavs().observe(this, new Observer<List<Favourite>>() {
            @Override
            public void onChanged(@Nullable List<Favourite> favourites) {
                favouritesListAdapter.setAdapterData(favourites);
            }
        });
        toolbar.setSubtitle(selection);
    }

    private void populateList() {
        recyclerView.setAdapter(mainMovieListAdapter);
        new DownloadMoviesTask().execute(selection);
        toolbar.setSubtitle(selection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.KEY_MENU_SELECTION, selection);
        outState.putParcelable(RECYCLER_LAYOUT_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_LAYOUT_STATE);
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerLayoutState);
    }

    @Override
    public void onMovieClicked(MovieModel selectedID) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Constants.KEY_PARSE_MOVIE, selectedID);
        startActivity(intent);
    }

    @Override
    public void onFavClickListener(int selectedFav) {
        Intent intent = new Intent(MainActivity.this, FavouritesDetailsActivity.class);
        intent.putExtra(Constants.KEY_PARSE_FAVOURITE, selectedFav);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadMoviesTask extends AsyncTask<String, Void, List<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieModel> doInBackground(String... strings) {

            URL url = NetworkUtils.buildMoviesUrl(Constants.API_KEY, strings[0]);

            try {
                String jsonString = null;
                if (url != null) {
                    jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                }
                MovieList movieList = MovieJsonUtils.parseMovieJson(jsonString);
                return movieList.getMovieList();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieModel> movieModels) {
            progressBar.setVisibility(View.GONE);
            if (movieModels != null) {
                mainMovieListAdapter.setAdapterData(movieModels);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerLayoutState);
            } else {
                Toast.makeText(context, context.getText(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
