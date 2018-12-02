package com.exmp.ziv24.mapsproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapHolderFragment extends Fragment {
    @BindView(R.id.fab_my_location)
    FloatingActionButton fab_my_location;
    @BindView(R.id.fab_clear)
    FloatingActionButton fab_clear;
    @BindView(R.id.fab_restaurant)
    FloatingActionButton fab_restaurant;
    @BindView(R.id.fab_school)
    FloatingActionButton fab_school;
    @BindView(R.id.fab_hospital)
    FloatingActionButton fab_hospital;
    @BindView(R.id.fab_shopping)
    FloatingActionButton fab_shopping;
    private Place place = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        final MapsFragment mapsFragment = new MapsFragment();
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.mapFragmentContainer, mapsFragment, "MAPHOLD").commit();
        fab_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsFragment.goToMyLocation();
            }
        });
        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapsFragment.clearMap();
            }
        });

        fab_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsFragment.hospitalClick();
            }
        });
        fab_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsFragment.restaurantClick();
            }
        });
        fab_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsFragment.schoolClick();
            }
        });
        fab_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsFragment.shoppingMallClick();
            }
        });
        return view;
    }

    public void placeMarker(Place place, boolean isNearbyClick) {
        try {
            MapsFragment mapsFragment = (MapsFragment) getChildFragmentManager().findFragmentByTag("MAPHOLD");
            if (mapsFragment != null) {
                mapsFragment.placeMarker(place, isNearbyClick);
            } else {
                this.place = place;
            }
        } catch (IllegalStateException e) {
            this.place = place;
        }
    }
}
