package com.exmp.ziv24.mapsproject.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.PowerConnectionReceiver;
import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.db.Constants;
import com.exmp.ziv24.mapsproject.model.MyPlaces;
import com.exmp.ziv24.mapsproject.model.Result;
import com.exmp.ziv24.mapsproject.model.Results;
import com.exmp.ziv24.mapsproject.network.DownloadPlacesService;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DialogDetailFragment.OnPlaceMarkerClick, DialogDetailFragment.OnFavoriteClick {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private MyLocalReceiver receiver;
    private PowerConnectionReceiver connectionReceiver;

    MapHolderFragment mapHolderFragment;
    PlaceFragment placeFragment;
    FavoriteFragment favoriteFragment;
    SettingFragment settingFragment;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 911;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        if (hasLocationAccess()) {
            createActivity();
        } else {
            requestLocationPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DownloadPlacesService.FILTER);
        receiver = new MyLocalReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilterConnect = new IntentFilter();
        IntentFilter intentFilterDisconnect = new IntentFilter();
        intentFilterConnect.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilterDisconnect.addAction(Intent.ACTION_POWER_DISCONNECTED);
        connectionReceiver = new PowerConnectionReceiver();
        registerReceiver(connectionReceiver, intentFilterConnect);
        registerReceiver(connectionReceiver, intentFilterDisconnect);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }

    private boolean hasLocationAccess() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createActivity();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createActivity() {
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.smallDevice) != null) {

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
            viewPager = (ViewPager) findViewById(R.id.viewPager_id);
            mapHolderFragment = new MapHolderFragment();
            placeFragment = new PlaceFragment();
            favoriteFragment = new FavoriteFragment();
            settingFragment = new SettingFragment();

            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(placeFragment, getResources().getString(R.string.search));
            adapter.addFragment(mapHolderFragment, getResources().getString(R.string.map));
            adapter.addFragment(favoriteFragment, getResources().getString(R.string.favorite));
            adapter.addFragment(settingFragment, getResources().getString(R.string.setting));

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }

    }

    @Override
    public void onPlaceMarker(Place place) {
        if (findViewById(R.id.smallDevice) != null) {
            viewPager.setCurrentItem(1);
            MapHolderFragment mapsFragment = (MapHolderFragment) adapter.getItem(1);
            if (mapsFragment != null) {
                mapsFragment.placeMarker(place, false);
            }
        } else {
            MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
            mapsFragment.placeMarker(place, false);
        }
    }

    @Override
    public void onFavoriteClick() {
        if (findViewById(R.id.smallDevice) != null) {
            viewPager.setCurrentItem(2);
        } else {
            TabletFragment tabletFragment = (TabletFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tablet);
            tabletFragment.viewPager.setCurrentItem(1);
        }
    }

    public void onBlankFavList() {
        if (findViewById(R.id.smallDevice) != null) {
            viewPager.setCurrentItem(0);
        } else {
            TabletFragment tabletFragment = (TabletFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tablet);
            tabletFragment.viewPager.setCurrentItem(0);

        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_SETTING, Activity.MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_SETTING, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.MY_LANG, "en");
        setLocale(language);
    }

    public void nearbyPlaceClicked(String json) {
        List<Place> placesList = getNearbyList(json);
        if (findViewById(R.id.smallDevice) != null) {
            viewPager.setCurrentItem(1);
            MapHolderFragment fragment = (MapHolderFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager_id + ":" + viewPager.getCurrentItem());
            for (int i = 0; i < placesList.size(); i++) {
                fragment.placeMarker(placesList.get(i), true);
            }
        } else {

            MapHolderFragment holderFragment = (MapHolderFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_tablet);
            TabletFragment tabletFragment = (TabletFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tablet);
            for (int i = 0; i < placesList.size(); i++) {
                holderFragment.placeMarker(placesList.get(i), true);
            }
            PlaceFragment fragment = tabletFragment.getPlaceFragment();

            if (tabletFragment.viewPager.getCurrentItem() == 0) {
                if (fragment.mContext != null) {
                    fragment.getPlaceList(json);
                }
            } else {
                tabletFragment.viewPager.setCurrentItem(0);
                fragment.getPlaceList(json);
            }
        }
    }

    public void loadToPlaceFragment(String json) {
        if (findViewById(R.id.smallDevice) != null) {

            viewPager.setCurrentItem(0);

            PlaceFragment fragment = (PlaceFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager_id + ":" + viewPager.getCurrentItem());

            if (fragment.mContext != null) {
                fragment.getPlaceList(json);
            }
        } else {
            TabletFragment tabletFragment = (TabletFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tablet);
            tabletFragment.viewPager.setCurrentItem(0);
            PlaceFragment fragment = tabletFragment.getPlaceFragment();
            if (fragment.mContext != null) {
                fragment.getPlaceList(json);
            }
        }
    }

    private Place getPlaceDetails(String json) {

        MyPlaces place = new Gson().fromJson(json, MyPlaces.class);
        Result placeDetails = place.getResult();
        Place fullDetailsPlace = new Place();
        fullDetailsPlace.setName(placeDetails.getName());
        fullDetailsPlace.setFormatted_address(placeDetails.getFormatted_address());
        if (placeDetails.getPhotos() != null) {
            fullDetailsPlace.setPhoto_reference(placeDetails.getPhotos()[0].getPhoto_reference());
        }
        fullDetailsPlace.setLatitude(Double.parseDouble(String.valueOf(placeDetails.getGeometry().getLocation().getLat())));
        fullDetailsPlace.setLongitude(Double.parseDouble(String.valueOf(placeDetails.getGeometry().getLocation().getLng())));
        if (placeDetails.getOpening_hours() != null) {
            fullDetailsPlace.setOpening_hours(placeDetails.getOpening_hours());
        }
        fullDetailsPlace.setWebsite_url(placeDetails.getWebsite());
        fullDetailsPlace.setPhone_number(placeDetails.getFormatted_phone_number());
        fullDetailsPlace.setRating(placeDetails.getRating());
        fullDetailsPlace.setType(placeDetails.getTypes()[0]);
        return fullDetailsPlace;
    }

    private List<Place> getNearbyList(String json) {

        Gson gson = new GsonBuilder().create();
        MyPlaces places = gson.fromJson(json, MyPlaces.class);
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
        return listOfPlaces;
    }


    private class MyLocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String json = intent.getStringExtra("TAG");
            if (json.equals("")) {
                Toast.makeText(MainActivity.this, "Please connect to the network", Toast.LENGTH_LONG).show();
            } else {

                if (intent.getIntExtra("FLAG", 0) == 1) {
                    SharedPreferences.Editor editor = getSharedPreferences("lastSearch", Context.MODE_PRIVATE).edit();
                    editor.putString("lastSearch", json);
                    editor.apply();
                    loadToPlaceFragment(json);

                } else if (intent.getIntExtra("FLAG", 0) == 2) {

                    Place placeDetails = getPlaceDetails(json);
                    DialogFragment dialog = DialogDetailFragment.newInstance(placeDetails, json);

                    dialog.show(getSupportFragmentManager(), "dialog");

                } else if (intent.getIntExtra("FLAG", 0) == 3) {
                    SharedPreferences.Editor editor = getSharedPreferences("lastSearch", Context.MODE_PRIVATE).edit();
                    editor.putString("lastSearch", json);
                    editor.apply();
                    nearbyPlaceClicked(json);


                }
            }
        }
    }

}
