package com.exmp.ziv24.mapsproject.objects;

import com.exmp.ziv24.mapsproject.model.Opening_hours;
import com.google.android.gms.maps.model.LatLng;

public class Place {
    private String name;
    private String formatted_address;
    private double latitude;
    private double longitude;
    private String photo_reference;
    private String open_now;
    private float rating;
    private String place_id;
    private int price_level;
    private String phone_number;
    private String website_url;
    private LatLng place_latlng;
    private Opening_hours opening_hours;
    private String db_id;
    private String type;

    public Place(String name, String formatted_address, float rating, String phone_number, String website_url, double latitude, double longitude, Opening_hours opening_hours, String db_id, String bitStr) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.rating = rating;
        this.phone_number = phone_number;
        this.website_url = website_url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.opening_hours = opening_hours;
        this.db_id = db_id;
        this.photo_reference = bitStr;
    }


    public Place(String name, String formatted_address) {
        this.name = name;
        this.formatted_address = formatted_address;
    }

    public Place(String name, String formatted_address, double latitude, double longitude, String photo_reference, float rating, int price_level, String phone_number, String website_url, Opening_hours opening_hours) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_reference = photo_reference;
        this.rating = rating;
        this.price_level = price_level;
        this.phone_number = phone_number;
        this.website_url = website_url;
        this.opening_hours = opening_hours;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public int getPrice_level() {

        return price_level;
    }

    public void setPrice_level(int price_level) {
        this.price_level = price_level;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public LatLng getPlace_latlng() {
        return place_latlng;
    }

    public void setPlace_latlng(LatLng place_latlng) {
        this.place_latlng = place_latlng;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Place() {
    }

    public Place(String name, String formatted_address, double latitude, double longitude, String photo_reference, String open_now, float rating) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_reference = photo_reference;
        this.open_now = open_now;
        this.rating = rating;
    }

    public Opening_hours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Opening_hours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        if (photo_reference != null) {
            this.photo_reference = photo_reference;
        } else {
            this.photo_reference = "CnRvAAAAwMpdHeWlXl-lH0vp7lez4znKPIWSWvgvZFISdKx45AwJVP1Qp37YOrH7sqHMJ8C-vBDC546decipPHchJhHZL94RcTUfPa1jWzo-rSHaTlbNtjh-N68RkcToUCuY9v2HNpo5mziqkir37WU8FJEqVBIQ4k938TI3e7bf8xq-uwDZcxoUbO_ZJzPxremiQurAYzCTwRhE_V0";
        }
    }

    public float getRating() {
        return rating;
    }

    public void setRating(String rating) {
        if (rating != null) {
            this.rating = Float.parseFloat(rating);
        } else {
            this.rating = 0;
        }
    }

    public String isOpen_now() {
        return open_now;
    }

    public void setOpen_now(String open_now) {
        if (open_now != null) {
            this.open_now = open_now;
        } else {
            this.open_now = "false";
        }
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", formatted_address='" + formatted_address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", photo_reference='" + photo_reference + '\'' +
                ", open_now=" + open_now +
                ", rating=" + rating +
                '}';
    }
}
