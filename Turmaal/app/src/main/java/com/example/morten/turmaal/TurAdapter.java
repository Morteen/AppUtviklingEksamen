package com.example.morten.turmaal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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


        ViewHolder viewHolder;
        if (convertView == null) {

            //XML fil-navnet
            convertView = mInflater.inflate(R.layout.turmaal_liste, null);
            viewHolder = new ViewHolder();


            viewHolder.tvStartNavn = (TextView) convertView.findViewById(R.id.tvStartNavn);
            viewHolder.tvStartType = (TextView) convertView.findViewById(R.id.tvStartType);
            viewHolder.tvStartHoyde = (TextView) convertView.findViewById(R.id.tvStartHoyde);
            viewHolder.tvAvstand = (TextView) convertView.findViewById(R.id.tAvstand);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Turmaal currentMaal = mineTuraal.get(position);
        //Setter verdien til  turmaal objektet  i denne posisjonen i riktig textview
        viewHolder.tvStartNavn.setText(currentMaal.getNavn());
        viewHolder.tvStartType.setText(currentMaal.getType());
        viewHolder.tvStartHoyde.setText(Integer.toString(currentMaal.getHoyde()) + " meter");
        if(currentMaal.getAvstand()/1000<1){
            viewHolder.tvAvstand.setText(Integer.toString(currentMaal.getAvstand()) + " m");
        }else{
        viewHolder.tvAvstand.setText(Integer.toString(currentMaal.getAvstand()/1000) + " Km");
        }


        return convertView;
    }



    private static class ViewHolder {
        public TextView tvStartNavn;
        public TextView tvStartType;
        public TextView tvStartHoyde;
        public TextView tvAvstand;


    }


}
