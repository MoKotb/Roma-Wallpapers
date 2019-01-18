package com.yomko.romawallpapers.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yomko.romawallpapers.Category.CategoryFragment;
import com.yomko.romawallpapers.Recent.RecentFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RecentFragment recentFragment = new RecentFragment();
                return recentFragment;
            case 1:
                CategoryFragment categoryFragment = new CategoryFragment();
                return categoryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
