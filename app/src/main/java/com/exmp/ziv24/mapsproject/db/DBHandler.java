package com.exmp.ziv24.mapsproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.exmp.ziv24.mapsproject.model.MyPlaces;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DBHandler {
    private DBHelper helper;

    public DBHandler(Context context) {
        helper = new DBHelper(context, Constants.DATABASE_NAME, null, 1);
    }

    public boolean addPlace(String json) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_NAME_PLACE_JSON, json);
            int id = (int) db.insert(Constants.TABLE_NAME, null, values);
            return true;

        } catch (Exception e) {
            return false;
        } finally {

            if (db.isOpen())
                db.close();
        }
    }

    public boolean deletePlace(String id) {


        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            db.delete(Constants.TABLE_NAME, "_id=?", new String[]{id});
        } catch (SQLiteException e) {
            return false;
        } finally {
            if (db.isOpen())
                db.close();
        }
        return true;
    }

    public boolean deleteAllPlaces() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constants.TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            return false;
        } finally {
            if (db.isOpen())
                db.close();
        }
        return true;
    }

    public boolean updatePlace(String json, String _id) {


        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_NAME_PLACE_JSON, json);
            db.update(Constants.TABLE_NAME, values, "_id=?", new String[]{_id});
        } catch (SQLiteException e) {
            return false;
        } finally {
            if (db.isOpen())
                db.close();
        }
        return true;
    }

    public ArrayList<Place> getAllPlaces() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = db.query(Constants.TABLE_NAME, null, null, null, null, null, null);

        } catch (SQLiteException e) {
            e.getCause();
        }

        ArrayList<Place> placeList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String place_json = cursor.getString(1);

            MyPlaces myPlaces = new Gson().fromJson(place_json, MyPlaces.class);

            if (place_json != null) {


                Place place = new Place();
                place.setName(myPlaces.getResult().getName());
                place.setFormatted_address(myPlaces.getResult().getFormatted_address());
                place.setRating(myPlaces.getResult().getRating());
                place.setPhone_number(myPlaces.getResult().getFormatted_phone_number());
                place.setWebsite_url(myPlaces.getResult().getWebsite());
                place.setLatitude(Double.parseDouble(myPlaces.getResult().getGeometry().getLocation().getLat()));
                place.setLongitude(Double.parseDouble(myPlaces.getResult().getGeometry().getLocation().getLng()));
                if (myPlaces.getResult().getOpening_hours() != null) {
                    place.setOpening_hours(myPlaces.getResult().getOpening_hours());
                }
                place.setDb_id(String.valueOf(id));
                place.setPhoto_reference(myPlaces.getResult().getPhotos()[0].getPhoto_reference());


                placeList.add(place);
            }
        }
        return placeList;
    }

    public Place selectNote(String _id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = db.query(Constants.TABLE_NAME, null, "_id=?", new String[]{_id}, null, null, null);

        } catch (SQLiteException e) {
            e.getCause();
        }

        cursor.moveToFirst();
        String id = "" + cursor.getInt(0);
        String place_json = cursor.getString(1);

        MyPlaces myPlaces = new Gson().fromJson(place_json, MyPlaces.class);


        Place place = new Place(
                myPlaces.getResult().getName(),
                myPlaces.getResult().getFormatted_address(),
                Float.parseFloat(myPlaces.getResult().getRating()),
                myPlaces.getResult().getFormatted_phone_number(),
                myPlaces.getResult().getWebsite(),
                Double.parseDouble(myPlaces.getResult().getGeometry().getLocation().getLat()),
                Double.parseDouble(myPlaces.getResult().getGeometry().getLocation().getLng()),
                myPlaces.getResult().getOpening_hours(), id, myPlaces.getResult().getPhotos()[0].getPhoto_reference());

        return place;
    }
}
