package com.exmp.ziv24.mapsproject.network;


import android.content.Context;
import com.exmp.ziv24.mapsproject.R;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Builders {

    private static final String PLACE_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    private static final String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private static final String NEARBY_PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    private static final String API_KEY = "AIzaSyCMjF3uy5OKHWqzX2XaoFMeIDG-Kw6JV64";



    public static String buildPlaceDetailsURL(String place_id , Context context) {

        return PLACE_DETAILS_URL + place_id +"&"+context.getString(R.string.url_lang)+"&key=" + API_KEY;
    }

    public static String buildURL(String query, double latitude, double longitude, String radius,Context context) {
        String queryFixed = "";
        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);
        try {
            queryFixed = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return PLACE_SEARCH_URL +
                queryFixed +
                "&location=" +
                lat + "," +
                lng +
                "&radius=" +
                radius +
                "&" +
                context.getString(R.string.url_lang) +
                "&key=" +
                API_KEY;
    }

    public static String buildNearbyURL(String latitude, String longitude, int radius, String type,Context context) {
        return NEARBY_PLACE_URL + latitude + "," + longitude + "&radius=" + radius + "&type=" + type + "&" + context.getString(R.string.url_lang) + "&key=" + API_KEY;
    }

    public static String builtPhotoURL(String photo_ref) {
        return PHOTO_URL + photo_ref + "&key=" + API_KEY;

    }
}
