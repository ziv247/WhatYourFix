package com.exmp.ziv24.mapsproject.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.GlideApp;
import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.db.DBHandler;
import com.exmp.ziv24.mapsproject.network.Builders;
import com.exmp.ziv24.mapsproject.objects.Place;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogDetailFragment extends DialogFragment {


    public static DialogDetailFragment newInstance(Place place, String json) {
        DialogDetailFragment dialogDetailFragment = new DialogDetailFragment();
        dialogDetailFragment.setPlaceWithDetails(place);
        dialogDetailFragment.setPlaceJson(json);


        return dialogDetailFragment;
    }


    @BindView(R.id.imageView_dialog)
    ImageView imageView_dialog;
    @BindView(R.id.textView_name_dialog)
    TextView textView_name_dialog;
    @BindView(R.id.textView_vicinity_dialog)
    TextView textView_vicinity_dialog;
    @BindView(R.id.textView_open_time)
    TextView textView_open_time;
    @BindView(R.id.textView_phone_num)
    TextView textView_phone_num;
    @BindView(R.id.textView_website)
    TextView textView_website;
    @BindView(R.id.textView_week_time)
    TextView textView_week_time;
    @BindView(R.id.fab_place_marker)
    FloatingActionButton fab_place_marker;
    @BindView(R.id.fab_favorite)
    FloatingActionButton fab_favorite;
    @BindView(R.id.fab_back)
    FloatingActionButton fab_back;
    @BindView(R.id.ratingBar_dialog)
    RatingBar ratingBar_dialog;
    @BindView(R.id.fab_share)
    FloatingActionButton fab_share;

    private static final int REQUEST_CALL = 1;
    private Place placeWithDetails;
    private String placeJson;
    private boolean isClicked = false;
    private OnPlaceMarkerClick mListenerMap;
    private OnFavoriteClick mListenerFav;
    private OnDeleteClick mListenerDelete;
    private DBHandler handler;
    private Context mContext;

    public interface OnPlaceMarkerClick {
        void onPlaceMarker(Place place);
    }

    public interface OnFavoriteClick {
        void onFavoriteClick();
    }

    public interface OnDeleteClick {
        void onDeleteClick();
    }

    public void setOnDeleteClickListener(OnDeleteClick listener) {
        mListenerDelete = listener;
    }


    public DialogDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mListenerMap = (OnPlaceMarkerClick) mContext;
        mListenerFav = (OnFavoriteClick) mContext;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_details_dialog, container, false);
        ButterKnife.bind(this, view);
        handler = new DBHandler(mContext);
        if (placeJson == null) {
            fab_favorite.setImageResource(R.drawable.ic_delete);
            fab_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.deletePlace(placeWithDetails.getDb_id());

                    mListenerDelete.onDeleteClick();
                    dismiss();

                }
            });
            GlideApp.with(this).load(Builders.builtPhotoURL(placeWithDetails.getPhoto_reference())).into(imageView_dialog);
        } else if (placeJson.equals("marker")) {
            fab_favorite.setVisibility(View.GONE);
            GlideApp.with(this).load(Builders.builtPhotoURL(placeWithDetails.getPhoto_reference())).into(imageView_dialog);
        } else {
            fab_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.addPlace(placeJson);
                    mListenerFav.onFavoriteClick();
                    dismiss();
                }
            });
        }

        textView_name_dialog.setText(placeWithDetails.getName());
        textView_vicinity_dialog.setText(placeWithDetails.getFormatted_address());
        if (placeWithDetails.getOpening_hours() != null) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[6]);
                    break;
                case Calendar.MONDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[0]);
                    break;
                case Calendar.TUESDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[1]);
                    break;
                case Calendar.WEDNESDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[2]);
                    break;
                case Calendar.THURSDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[3]);
                    break;
                case Calendar.FRIDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[4]);
                    break;
                case Calendar.SATURDAY:
                    textView_open_time.setText(placeWithDetails.getOpening_hours().getWeekday_text()[5]);
                    break;
            }
            textView_open_time.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if (!isClicked) {
                        textView_open_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_open, 0, R.drawable.ic_up, 0);
                        isClicked = true;
                        textView_week_time.setVisibility(View.VISIBLE);
                        textView_week_time.setText(
                                placeWithDetails.getOpening_hours().getWeekday_text()[6] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[0] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[1] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[2] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[3] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[4] + '\n'
                                        + placeWithDetails.getOpening_hours().getWeekday_text()[5]);
                    } else {
                        textView_open_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_open, 0, R.drawable.ic_down, 0);
                        isClicked = false;
                        textView_week_time.setVisibility(View.GONE);

                    }
                }
            });
        } else {
            textView_open_time.setVisibility(View.GONE);
        }
        if (placeWithDetails.getPhoto_reference() != null && placeJson != null && placeJson != "marker") {
            String photoURL = MyPlaceRecyclerViewAdapter.getPhotoUrl(placeWithDetails.getPhoto_reference(), mContext);
            GlideApp.with(mContext).load(photoURL).into(imageView_dialog);
            imageView_dialog.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        textView_phone_num.setText(placeWithDetails.getPhone_number());
        textView_phone_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToPlace(textView_phone_num.getText());
            }
        });
        if (placeWithDetails.getWebsite_url() != null) {
            textView_website.setText(placeWithDetails.getWebsite_url());
            textView_website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(textView_website.getText().toString()));
                    startActivity(openWeb);
                }
            });
        } else {
            textView_website.setVisibility(View.GONE);
        }
        ratingBar_dialog.setRating(placeWithDetails.getRating());
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        fab_place_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListenerMap.onPlaceMarker(placeWithDetails);
                dismiss();
            }
        });
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShareIntent();
            }
        });


        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    private void openShareIntent() {
        double latitude = placeWithDetails.getLatitude();
        double longitude = placeWithDetails.getLongitude();
        String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = "Here is " + placeWithDetails.getName() + " location";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void callToPlace(CharSequence placeNum) {
            String dial = "tel:" + placeNum;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callToPlace(textView_phone_num.getText());
            } else {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setPlaceWithDetails(Place placeWithDetails) {
        this.placeWithDetails = placeWithDetails;
    }

    public void setPlaceJson(String placeJson) {
        this.placeJson = placeJson;
    }
}
