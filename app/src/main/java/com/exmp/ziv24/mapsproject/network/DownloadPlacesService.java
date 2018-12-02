package com.exmp.ziv24.mapsproject.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.exmp.ziv24.mapsproject.objects.DownloadUrl;

import java.io.IOException;


public class DownloadPlacesService extends IntentService {

    private static final String ACTION_PLACES_DATA = "com.example.ziv24.mapsproject.network.action.FOO";
    private static final String ACTION_PLACES_DETAILS = "com.example.ziv24.mapsproject.network.action.BAZ";
    private static final String ACTION_NEARBY_PLACES = "com.example.ziv24.mapsproject.network.action.NEAR";
    public static String FILTER = "com.example.ziv24.mapsproject.network.DownloadPlacesService";

    private static final String EXTRA_PARAM1 = "com.example.ziv24.mapsproject.network.extra.PARAM1";

    public DownloadPlacesService() {
        super("DownloadPlacesService");
    }


    public static void startActionPlaceList(Context context, String url) {
        Intent intent = new Intent(context, DownloadPlacesService.class);
        intent.setAction(ACTION_PLACES_DATA);
        intent.putExtra(EXTRA_PARAM1, url);
        context.startService(intent);
    }


    public static void startActionPlaceDetails(Context context, String param1) {
        Intent intent = new Intent(context, DownloadPlacesService.class);
        intent.setAction(ACTION_PLACES_DETAILS);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    public static void startActionNearbyPlaces(Context context, String param1) {
        Intent intent = new Intent(context, DownloadPlacesService.class);
        intent.setAction(ACTION_NEARBY_PLACES);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLACES_DATA.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_PARAM1);
                try {
                    handleActionFoo(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_PLACES_DETAILS.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_PARAM1);
                try {
                    handleActionBaz(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_NEARBY_PLACES.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_PARAM1);
                try {
                    handleActionNEAR(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void handleActionFoo(String url) throws IOException {
        DownloadUrl downloadUrl = new DownloadUrl();
        String json = downloadUrl.readUrl(url);
        Intent intentF = new Intent(FILTER);
        intentF.putExtra("FLAG", 1);
        intentF.putExtra("TAG", json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentF);


    }


    private void handleActionBaz(String url) throws IOException {
        DownloadUrl downloadUrl = new DownloadUrl();
        String json = downloadUrl.readUrl(url);
        Intent intentF = new Intent(FILTER);
        intentF.putExtra("FLAG", 2);
        intentF.putExtra("TAG", json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentF);
    }

    private void handleActionNEAR(String url) throws IOException {
        DownloadUrl downloadUrl = new DownloadUrl();
        String json = downloadUrl.readUrl(url);
        Intent intentF = new Intent(FILTER);
        intentF.putExtra("FLAG", 3);
        intentF.putExtra("TAG", json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentF);
    }
}
