package com.exmp.ziv24.mapsproject.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exmp.ziv24.mapsproject.GlideApp;
import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.objects.Place;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFavoriteRecyclerViewAdapter extends RecyclerView.Adapter<MyFavoriteRecyclerViewAdapter.ViewHolder> {
    private List<Place> placeList;
    private Context mContext;
    private FragmentManager fragmentManager;


    public MyFavoriteRecyclerViewAdapter(List<Place> placeList, Context mContext, FragmentManager fragmentManager) {
        this.placeList = placeList;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Place place = placeList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDetailFragment dialog = DialogDetailFragment.newInstance(place, null);
                dialog.setOnDeleteClickListener(new DialogDetailFragment.OnDeleteClick() {
                    @Override
                    public void onDeleteClick() {
                        placeList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                dialog.show(fragmentManager, "dialog");
            }
        });


        holder.textView_name_favorite.setText(place.getName());
        holder.textView_distance_favorite.setText(getDistance(place.getLatitude(), place.getLongitude()));
        GlideApp.with(mContext).load(Builders.builtPhotoURL(place.getPhoto_reference())).into(holder.imageView_favorite);
    }

    private String getDistance(double latitude, double longitude) {
        SharedPreferences preferences = mContext.getSharedPreferences("myLocation", Context.MODE_PRIVATE);
        Location myLocation = new Location("mylocation");
        myLocation.setLatitude(Double.parseDouble(preferences.getString("lat", "0")));
        myLocation.setLongitude(Double.parseDouble(preferences.getString("lng", "0")));
        Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(latitude);
        placeLocation.setLongitude(longitude);
        double distanceBetween = myLocation.distanceTo(placeLocation);
        if (distanceBetween > 1000) {
            distanceBetween = distanceBetween / 1000;
            int fixedDistance = (int) distanceBetween;
            return String.valueOf(fixedDistance) + " KM";
        } else {
            int fixedDistance = (int) distanceBetween;
            return String.valueOf(fixedDistance) + " M";
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView_favorite)
        ImageView imageView_favorite;
        @BindView(R.id.textView_name_favorite)
        TextView textView_name_favorite;
        @BindView(R.id.textView_distance_favorite)
        TextView textView_distance_favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
