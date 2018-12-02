package com.exmp.ziv24.mapsproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.db.DBHandler;
import com.exmp.ziv24.mapsproject.objects.Place;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends Fragment implements DialogDetailFragment.OnDeleteClick {

    @BindView(R.id.favorite_recycler)
    RecyclerView favorite_recycler;
    @BindView(R.id.textViewBlankFavorite)
    TextView textViewBlankFavorite;

    private ArrayList<Place> placesList;
    private MyFavoriteRecyclerViewAdapter adapter;
    private Context mContext;
    private MainActivity mainActivity;


    public FavoriteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        ButterKnife.bind(this, view);
        DBHandler handler = new DBHandler(getContext());

        placesList = handler.getAllPlaces();
        if (placesList.size() > 0) {
            favorite_recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
            adapter = new MyFavoriteRecyclerViewAdapter(placesList, getContext(), getFragmentManager());
            favorite_recycler.setAdapter(adapter);


        } else {
            textViewBlankFavorite.setVisibility(View.VISIBLE);
            textViewBlankFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onBlankFavList();
                }
            });
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mainActivity = (MainActivity) context;
    }


    @Override
    public void onDeleteClick() {


    }
}
