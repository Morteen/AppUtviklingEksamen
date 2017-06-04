package com.example.morten.turmaal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;


public class ValgtTurmaalFragment extends Fragment {

    ImageView ivIcon;
    TextView tvNavn, tvType, tvHoyde, tvRegAnsvarlig, tvBeskrivelse, tAvstand;

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
        View view = inflater.inflate(R.layout.maalrad, container, false);

        ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        tvNavn = (TextView) view.findViewById(R.id.tvNavn);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvHoyde = (TextView) view.findViewById(R.id.tHoyde);
        tAvstand = (TextView) view.findViewById(R.id.tAvstand);
        tvRegAnsvarlig = (TextView) view.findViewById(R.id.tvRegAnsvarlig);
        tvBeskrivelse = (TextView) view.findViewById(R.id.tvBeskrivelse);

        tvNavn.setText(MainActivity.curTm.getNavn());
        tvType.setText("Type:  " + MainActivity.curTm.getType());
        tvHoyde.setText("Høyde:  " + Integer.toString(MainActivity.curTm.getHoyde()) + " meter");
        tvRegAnsvarlig.setText("Registret av:  " + MainActivity.curTm.getRegAnsvarlig());
        tvBeskrivelse.setText(MainActivity.curTm.getBeskrivelse());
        if (MainActivity.curTm.getAvstand() / 1000 < 1) {
            tAvstand.setText("Avstand:  " + Integer.toString(MainActivity.curTm.getAvstand()) + " m");

        } else {
            tAvstand.setText("Avstand:  " + Integer.toString(MainActivity.curTm.getAvstand() / 1000) + " km");

        }


        new DownloadImageTask((ImageView) view.findViewById(R.id.ivIcon)).execute(MainActivity.curTm.getBilde_URL());


        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {


            bmImage.setImageBitmap(result);
        }
    }


}
