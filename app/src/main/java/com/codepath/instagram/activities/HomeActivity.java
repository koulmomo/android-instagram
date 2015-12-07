package com.codepath.instagram.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.fragments.PostsFragment;
import com.codepath.instagram.fragments.ProfileFragment;
import com.codepath.instagram.fragments.SearchFragment;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    public static final String TAB_POSITION_SAVE_KEY = "POSITION";

    private class HomeFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {
        private final int[] imageResId = new int[] {
                R.drawable.ic_home,
                R.drawable.ic_search,
                R.drawable.ic_capture,
                R.drawable.ic_notifs,
                R.drawable.ic_profile
        };

        private final int NUM_ITEMS = imageResId.length;

        private Context mContext;

        public HomeFragmentStatePagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PostsFragment.newInstance();
                case 1:
                    return SearchFragment.newInstance();

                case 4:
                    return ProfileFragment.newInstance("self");
                default:
                    return PostsFragment.newInstance();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position

            Drawable image = ContextCompat.getDrawable(mContext, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

    @Bind(R.id.vpHomePager) ViewPager mHomeViewPager;
    @Bind(R.id.tlHomeTabs) TabLayout mHomeTabsLayout;

    private SmartFragmentStatePagerAdapter mAdapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mAdapterViewPager = new HomeFragmentStatePagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        mHomeViewPager.setAdapter(mAdapterViewPager);

        // Give the TabLayout the ViewPager
        mHomeTabsLayout.setupWithViewPager(mHomeViewPager);

        mHomeViewPager.setCurrentItem(0);
        mAdapterViewPager.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt(TAB_POSITION_SAVE_KEY, mHomeTabsLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHomeViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION_SAVE_KEY));
        mAdapterViewPager.notifyDataSetChanged();
    }
}
