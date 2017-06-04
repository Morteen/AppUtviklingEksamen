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

    /***
     * Starter et nytt fragment som tilsvarer posisjonen man velger
     * @param position
     * @return fragmentet man blar til
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ValgtTurmaalFragment();
        } else {

            return new MapFragment();
        }


    }

    /***
     * Gir hvor mange fragmenter man kan bla i, i dette tilfelle bare 2
     * @return
     */
    @Override
    public int getCount() {

        return 2;
    }
}
