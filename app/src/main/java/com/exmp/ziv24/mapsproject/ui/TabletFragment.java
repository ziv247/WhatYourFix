package com.exmp.ziv24.mapsproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exmp.ziv24.mapsproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TabletFragment extends Fragment {

    @BindView(R.id.tablayout_tablet_id)
    TabLayout tabLayout;
    @BindView(R.id.viewPager_tablet_id)
    ViewPager viewPager;

    private Context mContext;
    public ViewPagerAdapter adapter;

    public TabletFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tablet, container, false);
        ButterKnife.bind(this, view);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PlaceFragment(), mContext.getResources().getString(R.string.search));
        adapter.addFragment(new FavoriteFragment(), mContext.getResources().getString(R.string.favorite));
        adapter.addFragment(new SettingFragment(), mContext.getResources().getString(R.string.setting));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public PlaceFragment getPlaceFragment() {
        PlaceFragment placeFragment = (PlaceFragment) getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager_tablet_id + ":" + viewPager.getCurrentItem());
        return placeFragment;
    }
}
