package com.codepath.instagram.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {
    private class SearchFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {
        private final String[] TAB_ITEMS = new String[] {"Users", "Tags"};

        public SearchFragmentStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SearchUsersResultFragment.newInstance();
                case 1:
                    return SearchTagsResultFragment.newInstance();

                default:
                    return SearchUsersResultFragment.newInstance();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_ITEMS[position];
        }

        @Override
        public int getCount() {
            return TAB_ITEMS.length;
        }
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.vpSearchPager)
    ViewPager mSearchVP;

    @Bind(R.id.tlSearchTabs)
    TabLayout mSearchTL;

    private SmartFragmentStatePagerAdapter mSearchPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        mSearchPagerAdapter = new SearchFragmentStatePagerAdapter(getChildFragmentManager());
        mSearchVP.setAdapter(mSearchPagerAdapter);
        mSearchTL.setupWithViewPager(mSearchVP);

        return view;
    }

}
