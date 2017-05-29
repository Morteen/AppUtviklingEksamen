package com.example.morten.turmaal;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by morten on 29.05.2017.
 */

public class TurAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Turmaal> mineTuraal;
    LayoutInflater mInflater;




    public TurAdapter(Context mContext, ArrayList<Turmaal> mineTuraal) {
        this.mContext = mContext;
        this.mineTuraal = mineTuraal;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mineTuraal.size();
    }

    @Override
    //Finner Turmaal basert på posisjon i listen
    public Object getItem(int position) {
        return mineTuraal.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView bakgrundsbilde = new ImageView(mContext);
        //Dette er for å vise bilder fra JsonObjektet
        // Create global configuration and initialize ImageLoader with this config
        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
      /*  DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().init(config);*/



        ViewHolder viewHolder;
        if (convertView == null) {

            //XML fil-navnet
            convertView = mInflater.inflate(R.layout.turmaal_liste, null);
            viewHolder = new ViewHolder();
            //convertView.setBackgroundResource(R.drawable.test);

            ///Dette må tilpasses XML filen

            viewHolder.tvStartNavn = (TextView) convertView.findViewById(R.id.tvStartNavn);

            viewHolder.tvStartType = (TextView) convertView.findViewById(R.id.tvStartType);

            viewHolder.tvStartHoyde = (TextView) convertView.findViewById(R.id.tvStartHoyde);
            viewHolder.background=(RelativeLayout) convertView.findViewById(R.id.backgrund);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


       Turmaal currentMaal = mineTuraal.get(position);


        /*if (position == 0) {
            viewHolder.tvStartNavn.setText("Navn\n\n" + currentMaal.getNavn());
            viewHolder.tvStartType.setText("Type\n\n" + currentMaal.getType());
            viewHolder.tvStartHoyde.setText("Høyde\n\n" +Integer.toString(currentMaal.getHoyde()));
        } else {
            viewHolder.tvStartNavn.setText(currentMaal.getNavn());
            viewHolder.tvStartType.setText(currentMaal.getType());
            viewHolder.tvStartHoyde.setText(Integer.toString(currentMaal.getHoyde()));
        }*/

        viewHolder.tvStartNavn.setText(currentMaal.getNavn());
        viewHolder.tvStartType.setText(currentMaal.getType());
        viewHolder.tvStartHoyde.setText(Integer.toString(currentMaal.getHoyde())+" meter over havet");
        Log.d("BildeURL",currentMaal.getBilde_URL());
        //String url="https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/images/avengers.jpg";//currentMaal.getBilde_URL();
        String url="https://peakbook.org/gfx/pbes/e1/4f/e14f2c0c9152f1c78681652ff1189f2b/1.jpg";



        ImageLoader.getInstance().displayImage( url,bakgrundsbilde , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }

        });

        convertView.setBackground(bakgrundsbilde.getDrawable());
        return convertView;
    }





    ///Dette må tilpasses XML filen
    private static class ViewHolder {
        public TextView tvStartNavn;
        public TextView tvStartType;
        public TextView tvStartHoyde;
        public RelativeLayout background;


    }


}
