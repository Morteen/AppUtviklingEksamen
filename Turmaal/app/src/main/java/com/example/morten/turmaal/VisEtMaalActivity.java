package com.example.morten.turmaal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

public class VisEtMaalActivity extends FragmentActivity {

ViewPager sidevender;//Det finnes ikke noe godt norsk navn,ville vanligvis brukt 'pager'
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_et_maal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        sidevender=(ViewPager)findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        sidevender.setAdapter(adapter);

    }

}
