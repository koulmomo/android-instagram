package com.codepath.instagram.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.fragments.PostsFragment;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private SmartFragmentStatePagerAdapter adapterViewPager;

    private class HomeFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {
        private final int NUM_ITEMS = 5;
        private final String[] tabTitles = new String[] {"Home", "Search", "Capture", "Notifications", "Profile"};

        public HomeFragmentStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PostsFragment.newInstance();
                default:
                    return PostsFragment.newInstance();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpHomePager);
        adapterViewPager = new HomeFragmentStatePagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlHomeTabs);
        tabLayout.setupWithViewPager(vpPager);
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
}
