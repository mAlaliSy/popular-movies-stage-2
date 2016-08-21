package com.shamdroid.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.DialogInterface;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Utilities.MyPreferencesManager;
import Utilities.Util;
import Utilities.VolleySingleton;
import butterknife.BindView;
import butterknife.ButterKnife;
import data.MovieContract;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.mainRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.txtMainNoFavorite) TextView txtNoFavorite ;

    RequestQueue requestQueue;
    ArrayList<Movie> movies;

    Cursor favoriteOnly;

    MyRecyclerViewAdapter popularTopRatedAdapter;

    CursorRecyclerViewAdapter favoriteAdapter;

    String currentSortingBy = "";

    final int LOADER_ID = 0;


    final String SELECTED_POSITION_KEY = "selected_position" ;
    int mSelected = 0 ;

    ProgressDialog progressDialog;


    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.mainNumberOfColumns))); // Setting the layout to GridLayout. The number of columns depends on the device type ( Mobile - Tablet )



        requestQueue = VolleySingleton.getInstance().getRequestQueue(getActivity());
        movies = new ArrayList<>();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.plesaseWait));
        progressDialog.show();



        if (savedInstanceState != null )
            mSelected = savedInstanceState.getInt(SELECTED_POSITION_KEY);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {



        boolean isConnected = Util.isConnected(getActivity());

        currentSortingBy = MyPreferencesManager.getSortingSetting(getActivity()); // Getting the sorting method stored in the SharedPreferences
        if (isConnected && !currentSortingBy.equals(MyPreferencesManager.FAVORITE_VALUE)) { // If the device is connected to the internet .. all movies will be shown .
            getMoviesFromAPI(currentSortingBy); // Getting the data with the selected sorting method and notify the adapter about the changes
        } else { // If not , only favorite movies will be shown .
            getFavoriteMoviesOnly();
        }


        if (!isConnected) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.noConnectionFavoriteOnly))
                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }


        super.onActivityCreated(savedInstanceState);
    }



    public void refreshFavorite(){
        if (currentSortingBy.equals(MyPreferencesManager.FAVORITE_VALUE)) // Refresh the favorite list to check if there new favorite movie
            // checked when movie's details viewed and the user add or remove from favorite .
            getLoaderManager().restartLoader(LOADER_ID , null , this);
    }


    private void getFavoriteMoviesOnly() {

        getLoaderManager().restartLoader(LOADER_ID , null , this);

    }

    private void getMoviesFromAPI(String sortBy) { // Getting the movies with the selected sorting method passed and update the data in the recyclerview's adapter

        if (!movies.isEmpty()) // Make sure that the movies ArrayList is empty
            movies.clear();  // If it is not empty , clear it

        //## Start Building the URL for getting the movies depending on the order chosen by the user ##//
        String urlRequest; // The url for getting the movies depending on the order chosen by the user


        if (sortBy.equals(MyPreferencesManager.POPULAR_VALUE)) { // Determine the URL of the sorting by method chosen by the user
            urlRequest = VolleySingleton.POPULAR_URL;
        } else {
            urlRequest = VolleySingleton.TOP_RATER_URL;
        }

        urlRequest += "?" + VolleySingleton.API_KEY_GET_REQUEST_NAME + "=" + VolleySingleton.API_KEY; // Appending the API Key required by themoviedb

        //## End Building the URL for getting the movies depending on the order chosen by the user ##//

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    // Getting the data from the response and fill the ArrayList movies

                    JSONObject mResponse = new JSONObject(response);
                    JSONArray moviesArray = mResponse.getJSONArray("results");

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject oneMovie = moviesArray.getJSONObject(i);

                        String title = oneMovie.getString("original_title");
                        String posterURL = VolleySingleton.IMAGES_URL + oneMovie.getString("poster_path");
                        String releaseDate = oneMovie.getString("release_date");
                        String overview = oneMovie.getString("overview");
                        float voteAverage = (float) oneMovie.getDouble("vote_average");

                        int id = oneMovie.getInt("id");

                        Movie movie = new Movie(id, title, posterURL, overview, releaseDate, voteAverage);
                        movies.add(movie);
                    }


                    if (popularTopRatedAdapter == null)
                        popularTopRatedAdapter = new MyRecyclerViewAdapter(movies);

                    recyclerView.swapAdapter(popularTopRatedAdapter, true);

                    progressDialog.dismiss();

                    popularTopRatedAdapter.updateSelected(mSelected , false);
                    recyclerView.scrollToPosition(mSelected);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.buildConnectionErrorDialog(getActivity()).show(); // Show the connection error dialog
            }
        });

        requestQueue.add(stringRequest);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.mainMenuMostPopular:
                if (!currentSortingBy.equals(MyPreferencesManager.POPULAR_VALUE)) {
                    MyPreferencesManager.setSortingSetting(getActivity(), MyPreferencesManager.POPULAR_VALUE); // Update the sorting method stored in the ShredPreferences
                    getMoviesFromAPI(MyPreferencesManager.POPULAR_VALUE); // Get the movies from the API with the new selected sorting method
                    currentSortingBy = MyPreferencesManager.POPULAR_VALUE; // Update the current selected sorting method
                    mSelected = 0 ;
                }
                break;

            case R.id.mainMenuTopRated:
                if (!currentSortingBy.equals(MyPreferencesManager.TOP_RATED_VALUE)) {
                    MyPreferencesManager.setSortingSetting(getActivity(), MyPreferencesManager.TOP_RATED_VALUE); // Update the sorting method stored in the ShredPreferences
                    getMoviesFromAPI(MyPreferencesManager.TOP_RATED_VALUE); // Get the movies from the API with the new selected sorting method
                    currentSortingBy = MyPreferencesManager.TOP_RATED_VALUE; // Update the current selected sorting method
                    mSelected = 0 ;
                }
                break;


            case R.id.mainMenuFavorite:
                if (!currentSortingBy.equals(MyPreferencesManager.FAVORITE_VALUE)) {

                    MyPreferencesManager.setSortingSetting(getActivity(), MyPreferencesManager.FAVORITE_VALUE);
                    currentSortingBy = MyPreferencesManager.FAVORITE_VALUE;
                    getFavoriteMoviesOnly();
                    mSelected = 0 ;
                }
                break;
        }

        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {




        progressDialog.dismiss();

        favoriteOnly = cursor;

        favoriteAdapter = new CursorRecyclerViewAdapter(favoriteOnly);

        recyclerView.swapAdapter(favoriteAdapter, true);


        if (cursor.getCount() == 0 ){
            txtNoFavorite.setVisibility(View.VISIBLE);
        }else{
            txtNoFavorite.setVisibility(View.GONE);
        }

        if (cursor.getCount() > 0) {
            favoriteAdapter.updateSelected(mSelected, false);

            recyclerView.scrollToPosition(mSelected);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int selected = ((MyRecyclerViewAdapter)recyclerView.getAdapter()).getSelectedPosition();

        outState.putInt(SELECTED_POSITION_KEY , selected);

    }


    public interface ChangeDetails{
        void showDetails(Movie movie , boolean byClick);
    }






    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        public MyViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView;
        }
    }


    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {


        ArrayList<Movie> moviesData;
        int selectedPosition = 0 ;


        public MyRecyclerViewAdapter() {

        }

        public MyRecyclerViewAdapter(ArrayList<Movie> movies) {
            moviesData = movies;
        }





        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

            return new MyViewHolder(view);
        }


        public void loadImage(int position, ImageView into) {


            Picasso.with(getActivity())
                    .load(moviesData.get(position).getPosterURL())
                    .placeholder(Util.getDrawable(getActivity() , R.drawable.ic_image_black_48dp))
                    .error(Util.getDrawable(getActivity() , R.drawable.ic_error_black_48dp))
                    .into(into);
        }

        public Movie getMovie(int position) {
            return moviesData.get(position);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {



            if (position == selectedPosition){
                holder.poster.setAlpha(.5f);
            }else{
                holder.poster.setAlpha(1.0f);
            }


            loadImage(position, holder.poster);

            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // Go to the details activity when the poster is clicked
                    updateSelected(position , true );
                }
            });


        }


        @Override
        public int getItemCount() {
            return moviesData.size();
        }



        public void updateSelected(int newSelected , boolean byClick){

            int oldSelected = selectedPosition ;
            selectedPosition = newSelected ;

            notifyItemChanged(oldSelected);
            notifyItemChanged(selectedPosition);

            ((ChangeDetails) getActivity()).showDetails(getMovie(newSelected) , byClick);

        }

        public int getSelectedPosition() {
            return selectedPosition;
        }
    }


    class CursorRecyclerViewAdapter extends MyRecyclerViewAdapter {

        Cursor mCursor;

        public CursorRecyclerViewAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public Movie getMovie(int position) {
            if (mCursor.moveToPosition(position))
                return new Movie(mCursor);
            return null;
        }

        @Override
        public void loadImage(int position, ImageView into) {
            if (mCursor.moveToPosition(position)) {

                String imageFileName = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_URL));

                Bitmap bitmap = Util.loadImageFromImagesDir(getActivity(), imageFileName);

                if (bitmap != null)
                    into.setImageBitmap(bitmap);
                else
                    into.setImageDrawable(Util.getDrawable(getActivity(),R.drawable.ic_error_black_48dp));

            }
        }

        public void swapCursor(Cursor newCursor) {
            if (mCursor == newCursor)
                return;

            mCursor.close(); // Close the old cursor ..

            mCursor = newCursor;

        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

    }

}
