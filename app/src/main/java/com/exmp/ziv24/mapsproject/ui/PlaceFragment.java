package com.exmp.ziv24.mapsproject.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.model.MyPlaces;
import com.exmp.ziv24.mapsproject.model.Results;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.network.DownloadPlacesService;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceFragment extends Fragment {

    String lastJson;
    Context mContext;
    MainActivity mActivity;
    MyPlaceRecyclerViewAdapter adapter;
    Location mLocation;
    double latitude;
    double longitude;
    private FusedLocationProviderClient mFuse;

    @BindView(R.id.searchView2)
    SearchView searchView;
    @BindView(R.id.radius_seekBar)
    SeekBar radius_seekBar;
    @BindView(R.id.radius_textView)
    TextView radius_TextView;
    @BindView(R.id.searchRecycler)
    RecyclerView searchRecycler;

    public PlaceFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        getLocation();
        ButterKnife.bind(this, view);
        radius_seekBar.setVisibility(View.GONE);
        radius_TextView.setVisibility(View.GONE);
        radius_TextView.setText(mContext.getString(R.string.radius) + ": " + 1000);
        radius_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius_TextView.setText(mContext.getString(R.string.radius) + ": " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        searchView.setFocusable(false);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    radius_seekBar.setVisibility(View.VISIBLE);
                    radius_TextView.setVisibility(View.VISIBLE);
                } else {
                    radius_seekBar.setVisibility(View.GONE);
                    radius_TextView.setVisibility(View.GONE);
                }
            }
        });

        SharedPreferences preferences = mContext.getSharedPreferences("lastSearch", Context.MODE_PRIVATE);
        if (preferences.getString("lastSearch", "") != "") {
            lastJson = preferences.getString("lastSearch", "");
            getPlaceList(lastJson);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getLocation();
                String radius = String.valueOf(radius_seekBar.getProgress());
                String builtURL = Builders.buildURL(query, latitude, longitude, radius, mContext);
                DownloadPlacesService.startActionPlaceList(mContext, builtURL);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mFuse.getLastLocation().addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                SharedPreferences.Editor editor = mActivity.getSharedPreferences("myLocation", Context.MODE_PRIVATE).edit();
                mLocation = task.getResult();
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                editor.putString("lat", String.valueOf(latitude));
                editor.putString("lng", String.valueOf(longitude));
                editor.apply();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFuse = LocationServices.getFusedLocationProviderClient(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void loadJson(List<Place> placeList) {

        searchRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new MyPlaceRecyclerViewAdapter(mContext, placeList);
        searchRecycler.setAdapter(adapter);
        adapter.onOpenPlaceDetailsDialog(new MyPlaceRecyclerViewAdapter.OpenPlaceDetailsDialog() {
            @Override
            public void onPlaceSelect(String place_id) {
                String builtDetailsURL = Builders.buildPlaceDetailsURL(place_id, mContext);
                DownloadPlacesService.startActionPlaceDetails(mContext, builtDetailsURL);
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void getPlaceList(String json) {

        Gson gson = new GsonBuilder().create();
        MyPlaces places = gson.fromJson(json, MyPlaces.class);
        if (places.getStatus().equals("ZERO_RESULTS")) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.search_failed), Toast.LENGTH_LONG).show();
        }
        List<Place> listOfPlaces = new ArrayList<>();

        for (int i = 0; i < places.getResults().length; i++) {
            Results googlePlace = places.getResults()[i];
            Place place = new Place();
            place.setName(googlePlace.getName());
            place.setFormatted_address(googlePlace.getFormatted_address());
            place.setLatitude(Double.parseDouble(googlePlace.getGeometry().getLocation().getLat()));
            place.setLongitude(Double.parseDouble(googlePlace.getGeometry().getLocation().getLng()));
            place.setPlace_id(googlePlace.getPlace_id());
            if (googlePlace.getPhotos() != null) {
                place.setPhoto_reference(googlePlace.getPhotos()[0].getPhoto_reference());
            }
            if (googlePlace.getOpening_hours() != null) {
                place.setOpen_now(googlePlace.getOpening_hours().getOpen_now());
            }
            place.setRating(googlePlace.getRating());
            place.setType(googlePlace.getTypes()[0]);
            listOfPlaces.add(place);
        }
        loadJson(listOfPlaces);
    }
}
