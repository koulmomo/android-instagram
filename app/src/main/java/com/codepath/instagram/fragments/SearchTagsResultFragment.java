package com.codepath.instagram.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTagsResultFragment extends Fragment {

    
    public SearchTagsResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    public static Fragment newInstance() {
        SearchTagsResultFragment fragment = new SearchTagsResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
