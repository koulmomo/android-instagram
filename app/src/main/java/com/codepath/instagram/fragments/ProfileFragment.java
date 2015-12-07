package com.codepath.instagram.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramUser;
import com.codepath.instagram.services.LoadHomeFeedService;
import com.codepath.instagram.services.LoadUserProfileService;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    @Bind(R.id.ivAvatar)
    ImageView mAvatarIV;

    @Bind(R.id.tvMediaPostCount)
    TextView mPostCountTV;

    @Bind(R.id.tvFollowerCount)
    TextView mFollowerCountTV;

    @Bind(R.id.tvFollowCount)
    TextView mFollowCountTV;

    @Bind(R.id.tvFullName)
    TextView mFullNameTv;

    @Bind(R.id.tvBio)
    TextView mBioTV;

    private String mUserId;
    private InstagramUser mUser;
    private boolean isMounted = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case LoadUserProfileService.FETCH_USER_PROFILE_OK:
                    mUser = (InstagramUser) intent.getSerializableExtra("user");
                    render();
            }
        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args == null) {
            return;
        }

        mUserId = args.getString("userId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(mReceiver, new IntentFilter(LoadUserProfileService.ACTION));

        fetchUserProfile();

        return view;
    }

    public void fetchUserProfile() {
        Intent i = new Intent(getContext(), LoadUserProfileService.class);
        i.putExtra("userId", mUserId);

        getActivity().startService(i);
    }

    private void renderProfilePic() {
        if (mAvatarIV == null) {
            return;
        }

        Picasso.with(this.getContext())
                .load(mUser.profilePictureUrl)
                .fit()
                .into(mAvatarIV);

    }

    private void renderTextView(TextView view, CharSequence text) {
        if (view == null) {
            return;
        }

        view.setText(text);
    }

    public void render() {

        renderProfilePic();

        renderTextView(mPostCountTV, Utils.formatNumberForDisplay(mUser.mediaCount));
        renderTextView(mFollowerCountTV, Utils.formatNumberForDisplay(mUser.followerCount));
        renderTextView(mFollowCountTV, Utils.formatNumberForDisplay(mUser.followCount));
        renderTextView(mBioTV, mUser.bio);
        renderTextView(mFullNameTv, mUser.fullName);
    }
}
