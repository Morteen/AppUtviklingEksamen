package com.example.morten.turmaal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class RegTurmaalActivity extends AppCompatActivity {
Button regturknp;
    LocationManager locationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    public final static int MY_REQUEST_LOCATION = 1;
    Location myLocation=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_turmaal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        regturknp=(Button)findViewById(R.id.regKnp);
        regturknp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////
                // Bruk LocationManager for å finne siste kjente posisjon
                locationManager = (LocationManager) RegTurmaalActivity.this.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(locationProvider)) {
                    //** Må sjekke om bruker har gitt tillatelse til å bruke GPS
                    // Denne fungerer også før API 23 med AppCompatActivity:
                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                    // Denne fungerer bare fra og med  API 23 med Activity:
                    //int permissionCheck = getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName());
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        //** Hvis tillatelse ikke er gitt må programmet spørre brukeren
                        // Denne fungerer også før API 23 med AppCompatActivity:
                        ActivityCompat.requestPermissions(RegTurmaalActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);
                        // Denne fungerer bare fra og med  API 23 med Activity:
                        //this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);
                    } else {
                        // Hent siste kjente posisjon
                        myLocation = locationManager.getLastKnownLocation(locationProvider);
                      double lengdeGrad=myLocation.getLongitude();
                        double breddeGrad=myLocation.getLatitude();
                        double hoyde=myLocation.getAltitude();
                        Geocoder geocoder = new Geocoder(RegTurmaalActivity.this);
                        List<Address> adressList = null;
                        try {
                            adressList = geocoder.getFromLocation(breddeGrad, lengdeGrad, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String start = "Start sted: " + adressList.get(0).getLocality() + " -";
                        start += adressList.get(0).getCountryName();
                        Toast.makeText(getApplicationContext(),
                                "Lengdegrad:"+lengdeGrad+"+\nBreddegrad:"+breddeGrad+"\nHøyde:"+hoyde+"\nAdresse:"+start, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Aktiver " + locationProvider + " under Location i Settings", Toast.LENGTH_LONG).show();
                }

                ///////////////////////




            }
        });


    }

}
