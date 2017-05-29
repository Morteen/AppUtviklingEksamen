package com.example.morten.turmaal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ValgtTurmaalFragment extends Fragment {

    ImageView ivIcon;
    TextView tvNavn,tvType,tvHoyde,tvRegAnsvarlig,tvBeskrivelse;
    public ValgtTurmaalFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_valgt_turmaal, container, false);

        ivIcon=(ImageView)view.findViewById(R.id.ivIcon) ;
        tvNavn=(TextView) view.findViewById(R.id.tvNavn);
        tvType=(TextView)view.findViewById(R.id.tvType);
        tvHoyde=(TextView)view.findViewById(R.id.tvHoyde);
        tvRegAnsvarlig=(TextView)view.findViewById(R.id.tvRegAnsvarlig) ;
        tvBeskrivelse=(TextView)view.findViewById(R.id.tvBeskrivelse) ;

        tvNavn.setText(MainActivity.curTm.getNavn());
        tvType.setText(MainActivity.curTm.getType());
        tvHoyde.setText(Integer.toString(MainActivity.curTm.getHoyde()));
        tvRegAnsvarlig.setText(MainActivity.curTm.getRegAnsvarlig());
        tvBeskrivelse.setText(MainActivity.curTm.getBeskrivelse());


        final ProgressBar progressBar; progressBar=(ProgressBar)view.findViewById(R.id.progressBar) ;

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




        return view;
    }


}
