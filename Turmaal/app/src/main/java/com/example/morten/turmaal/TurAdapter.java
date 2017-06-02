package com.example.morten.turmaal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by morten on 29.05.2017.
 * Dette er et listview adapter slik at alle turmålene kan ses i et listview
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
            viewHolder.background = (RelativeLayout) convertView.findViewById(R.id.backgrund);
            //viewHolder.visURl=(TextView)convertView.findViewById(R.id.url) ;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Turmaal currentMaal = mineTuraal.get(position);


        viewHolder.tvStartNavn.setText(currentMaal.getNavn());
        viewHolder.tvStartType.setText(currentMaal.getType());
        viewHolder.tvStartHoyde.setText(Integer.toString(currentMaal.getHoyde()) + " meter over havet");

        Log.d("BildeURL", currentMaal.getBilde_URL());


        new DownloadImageTask(bakgrundsbilde).execute("http://static.panoramio.com/photos/large/91778790.jpg");

        convertView.setBackground(bakgrundsbilde.getDrawable());
        return convertView;
    }


    ///Dette må tilpasses XML filen
    private static class ViewHolder {
        public TextView tvStartNavn;
        public TextView tvStartType;
        public TextView tvStartHoyde;
        public TextView visURl;
        public RelativeLayout background;


    }

    //Fra læreboken
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
