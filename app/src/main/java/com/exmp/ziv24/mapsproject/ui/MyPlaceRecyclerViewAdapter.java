package com.exmp.ziv24.mapsproject.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exmp.ziv24.mapsproject.GlideApp;
import com.exmp.ziv24.mapsproject.db.Constants;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.objects.Place;
import com.exmp.ziv24.mapsproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyPlaceRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaceRecyclerViewAdapter.ViewHolder> {

    private OpenPlaceDetailsDialog mListenerDialog;
    private List<Place> placesList;
    private Context mContext;
    public static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public SharedPreferences distancePref;


    public interface OpenPlaceDetailsDialog {
        void onPlaceSelect(String place_id);
    }


    public void onOpenPlaceDetailsDialog(OpenPlaceDetailsDialog listener) {
        mListenerDialog = listener;
    }


    public MyPlaceRecyclerViewAdapter(Context mContext, List<Place> placesList) {
        this.placesList = placesList;
        this.mContext = mContext;
        distancePref = mContext.getSharedPreferences(Constants.PREF_SETTING, Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Place currentPlace = placesList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListenerDialog.onPlaceSelect(currentPlace.getPlace_id());
            }
        });
        holder.place_name_card.setText(currentPlace.getName());
        holder.ratingBar_card.setRating(currentPlace.getRating());
        holder.vicinity_card.setText(currentPlace.getFormatted_address());
        holder.imageview_card.setClipToOutline(true);
        setPhoto(currentPlace.getPhoto_reference(), holder);
        if (currentPlace.isOpen_now() != null) {
            if (currentPlace.isOpen_now().equals("true")) {
                holder.textView_isOpen.setTextColor(mContext.getResources().getColor(R.color.openColor));
                holder.textView_isOpen.setText(mContext.getResources().getString(R.string.open));
            } else if (currentPlace.isOpen_now().equals("false")) {
                holder.textView_isOpen.setTextColor(mContext.getResources().getColor(R.color.closeColor));
                holder.textView_isOpen.setText(mContext.getResources().getString(R.string.close));
            }
        } else {
            holder.textView_isOpen.setVisibility(View.GONE);
        }
        holder.textView_dis.setText(getDistance(currentPlace.getLatitude(), currentPlace.getLongitude()));
    }

    private String getDistance(double latitude, double longitude) {
        SharedPreferences preferences = mContext.getSharedPreferences("myLocation", Context.MODE_PRIVATE);
        Location myLocation = new Location("myLocation");
        myLocation.setLatitude(Double.parseDouble(preferences.getString("lat", "0")));
        myLocation.setLongitude(Double.parseDouble(preferences.getString("lng", "0")));
        Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(latitude);
        placeLocation.setLongitude(longitude);
        double distanceBetween = myLocation.distanceTo(placeLocation);
        if (distanceBetween > 1000) {
            distanceBetween = distanceBetween / 1000;
            int fixedDistance = (int) distanceBetween;
            if (distancePref.getString(Constants.DISTANCE_POS, Constants.KM).equals(Constants.KM)) {
                return String.valueOf(fixedDistance) + " " + mContext.getString(R.string.km);
            } else if (distancePref.getString(Constants.DISTANCE_POS, Constants.MILE).equals(Constants.MILE)) {
                fixedDistance = (int) (fixedDistance * Constants.KM2MILES);
                return String.valueOf(fixedDistance) + " " + mContext.getString(R.string.miles);
            }
        } else {
            int fixedDistance = (int) distanceBetween;
            if (distancePref.getString(Constants.DISTANCE_POS, Constants.KM).equals(Constants.KM)) {

                return String.valueOf(fixedDistance) + mContext.getString(R.string.meter);
            } else if (distancePref.getString(Constants.DISTANCE_POS, Constants.MILE).equals(Constants.MILE)) {
                fixedDistance = (int) (fixedDistance * Constants.M2FT);
                return String.valueOf(fixedDistance) + mContext.getString(R.string.foot);
            }
        }
        return "";
    }

    private void setPhoto(String photoRef, final ViewHolder holder) {
        if (photoRef == null) {
            holder.imageview_card.setImageResource(R.drawable.google_office);
        } else {
            GlideApp.with(holder.itemView.getContext()).asBitmap().load(Builders.builtPhotoURL(photoRef))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.imageview_card.setImageBitmap(resource);
                        }
                    });
        }
    }

    public static String getPhotoUrl(String photoRef, Context context) {
        String url = PHOTO_URL + photoRef + "&key=" + context.getResources().getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageview_card)
        ImageView imageview_card;
        @BindView(R.id.place_name_card)
        TextView place_name_card;
        @BindView(R.id.vicinity_card)
        TextView vicinity_card;
        @BindView(R.id.ratingBar_card)
        RatingBar ratingBar_card;
        @BindView(R.id.textView_isOpen)
        TextView textView_isOpen;
        @BindView(R.id.textView_dis)
        TextView textView_dis;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
