package com.example.morten.turmaal;
/**
 * Dette fragmentet viser turmålets plassering på kartet etter brukeren har trykket på et mål i listen
 * */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.activity_maps, container, false);


        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()//getSupportFragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        //Flytter markøren og kamera til valgte posisjon
        LatLng valgteTM = new LatLng(MainActivity.curTm.getBreddegrad(), MainActivity.curTm.getLengdegrad());
        mMap.addMarker(new MarkerOptions().position(valgteTM).title(MainActivity.curTm.getNavn()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(valgteTM));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(valgteTM, 10.2f));//10.2 er zoom level

    }
}
