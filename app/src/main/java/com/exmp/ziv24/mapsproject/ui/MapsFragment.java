package com.exmp.ziv24.mapsproject.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.network.DownloadPlacesService;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {
    private GoogleMap mMap;
    private Context mContext;
    private Location mLocation;
    double latitude;
    double longitude;
    private static final int REQUEST_CODE = 1;
    private FusedLocationProviderClient mfuse;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void clearMap() {
        mMap.clear();
    }

    public void goToMyLocation() {
        SharedPreferences preferences = mContext.getSharedPreferences("lastLocation", Context.MODE_PRIVATE);
        LatLng myLocation = new LatLng(Double.parseDouble(preferences.getString("lat", "1"))
                , Double.parseDouble(preferences.getString("lng", "1")));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f));
    }


    @Override
    public void onStart() {
        super.onStart();
        getMapAsync(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mfuse = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnPoiClickListener(this);
    }


    public void placeMarker(Place place, boolean isNearbyClick) {

        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
        String placeName = place.getName();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(placeName);
        markerOptions.position(latLng);
        if (place.getType()!=null) {
            switch (place.getType()) {
                case "hospital":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    break;
                case "restaurant":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    break;
                case "school":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    break;
                case "shopping_mall":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    break;
                default:
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
        }else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        }

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(place);

        if (isNearbyClick) {
            goToMyLocation();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Place markerPlace = (Place) marker.getTag();
                    DownloadPlacesService.startActionPlaceDetails(mContext, Builders.buildPlaceDetailsURL(markerPlace.getPlace_id(), mContext));
                }
            });
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 120.0f));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Place markerPlace = (Place) marker.getTag();
                    DialogDetailFragment dialog = DialogDetailFragment.newInstance(markerPlace, "marker");
                    dialog.show(getFragmentManager(), "dialog");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(mContext, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }

        mfuse.getLastLocation().addOnCompleteListener((Activity) mContext, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLocation = task.getResult();
                    latitude = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("lastLocation", Context.MODE_PRIVATE).edit();
                    editor.putString("lat", String.valueOf(latitude));
                    editor.putString("lng", String.valueOf(longitude));
                    editor.apply();
                }
            }
        });
    }


    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        DownloadPlacesService.startActionPlaceDetails(mContext,
                Builders.buildPlaceDetailsURL(pointOfInterest.placeId, mContext));
    }


    public void hospitalClick() {
        String hospitalURL =
                Builders.buildNearbyURL(String.valueOf(latitude),
                        String.valueOf(longitude),
                        5000,
                        "hospital",
                        mContext);
        DownloadPlacesService.startActionNearbyPlaces(mContext, hospitalURL);
    }

    public void schoolClick() {
        String schoolURL =
                Builders.buildNearbyURL(String.valueOf(latitude),
                        String.valueOf(longitude),
                        5000,
                        "school", mContext);
        DownloadPlacesService.startActionNearbyPlaces(mContext, schoolURL);
    }

    public void shoppingMallClick() {
        String shoppingURL =
                Builders.buildNearbyURL(String.valueOf(latitude),
                        String.valueOf(longitude),
                        5000,
                        "shopping_mall", mContext);
        DownloadPlacesService.startActionNearbyPlaces(mContext, shoppingURL);
    }

    public void restaurantClick() {
        String restaurantURL =
                Builders.buildNearbyURL(String.valueOf(latitude),
                        String.valueOf(longitude),
                        5000,
                        "restaurant", mContext);
        DownloadPlacesService.startActionNearbyPlaces(mContext, restaurantURL);

    }

}
