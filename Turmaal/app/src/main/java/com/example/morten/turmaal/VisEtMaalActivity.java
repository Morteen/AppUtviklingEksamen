package com.example.morten.turmaal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VisEtMaalActivity extends AppCompatActivity {

ImageView ivIcon;
    TextView tvNavn,tvType,tvHoyde,tvRegAnsvarlig,tvBeskrivelse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_et_maal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ivIcon=(ImageView)findViewById(R.id.ivIcon) ;
        tvNavn=(TextView) findViewById(R.id.tvNavn);
        tvType=(TextView) findViewById(R.id.tvType);
        tvHoyde=(TextView)findViewById(R.id.tvHoyde);
        tvRegAnsvarlig=(TextView)findViewById(R.id.tvRegAnsvarlig) ;
        tvBeskrivelse=(TextView)findViewById(R.id.tvBeskrivelse) ;

        tvNavn.setText(MainActivity.curTm.getNavn());
        tvType.setText(MainActivity.curTm.getType());
        tvHoyde.setText(Integer.toString(MainActivity.curTm.getHoyde()));
        tvRegAnsvarlig.setText(MainActivity.curTm.getRegAnsvarlig());
        tvBeskrivelse.setText(MainActivity.curTm.getBeskrivelse());


        final ProgressBar progressBar; progressBar=(ProgressBar)findViewById(R.id.progressBar) ;

        ImageLoader.getInstance().displayImage(MainActivity.curTm.getBilde_URL(),ivIcon , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
