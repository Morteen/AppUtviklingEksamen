package com.example.morten.turmaal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by morten on 29.05.2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new ValgtTurmaalFragment();
        }else{
            Fragment map=(Fragment)new MapFragment();
            return map;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
