package com.example.morten.turmaal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by morten on 29.05.2017.
 * Dette er en adapter klasse for å kunne svipe mellom flere fragmenter
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

            //return new ForeldreFragment();
            return new MapFragment();
        }


    }

    @Override
    public int getCount() {
        return 2;
    }
}
