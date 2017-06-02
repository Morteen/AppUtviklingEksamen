package com.example.morten.turmaal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class OpplysningerActivity extends AppCompatActivity {
    Button lagreTm;
    EditText mType,mBeskrivelse,mNavn;
    public final static int MY_REQUEST_LOCATION = 1;
    Turmaal maal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opplysninger);


        maal = new Turmaal();
        final LocationManager locationManager;
        locationManager = (LocationManager) OpplysningerActivity.this.getSystemService(Context.LOCATION_SERVICE);

        final String locationProvider = LocationManager.GPS_PROVIDER;

        mNavn=(EditText)findViewById(R.id.eNavn);
        mBeskrivelse=(EditText)findViewById(R.id.eBeskrivelse);
        mType=(EditText)findViewById(R.id.eType);
        lagreTm = (Button) findViewById(R.id.lagreTm);

        lagreTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location myLocation = null;

                Toast.makeText(getApplicationContext(),
                        "Det er liv i knappen", Toast.LENGTH_LONG).show();
                /////////////////////////
                // Bruk LocationManager for å finne siste kjente posisjon

                if (locationManager.isProviderEnabled(locationProvider)) {


                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

                    //int permissionCheck = getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName());
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        //** Hvis tillatelse ikke er gitt må programmet spørre brukeren
                        // Denne fungerer også før API 23 med AppCompatActivity:
                        ActivityCompat.requestPermissions(OpplysningerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);

                    } else {
                        // Hent siste kjente posisjon
                        myLocation = locationManager.getLastKnownLocation(locationProvider);
                        double lengdeGrad = myLocation.getLongitude();
                        double breddeGrad = myLocation.getLatitude();
                        int hoyde = (int) myLocation.getAltitude();


                        if (MainActivity.regAnsvarligNavn != null) {
                            maal.setHoyde(hoyde);
                            maal.setLengdegrad((float) lengdeGrad);
                            maal.setBreddegrad((float) breddeGrad);
                            maal.setRegAnsvarlig(storForBokstav(MainActivity.regAnsvarligNavn));
                            Geocoder geocoder = new Geocoder(OpplysningerActivity.this);
                            List<Address> adressList = null;
                            try {
                                adressList = geocoder.getFromLocation(breddeGrad, lengdeGrad, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (RegTurmaalActivity.bildeNavn != null) {
                                maal.setBilde_URL(RegTurmaalActivity.bildeNavn);
                                RegTurmaalActivity.bildeNavn = null;
                            }
                            if(mNavn.getText().toString().isEmpty()){
                            String start = adressList.get(0).getLocality() + " -";
                            start += adressList.get(0).getCountryName();
                            maal.setNavn(start);
                            }else{
                                maal.setNavn(storForBokstav(mNavn.getText().toString())+" -"+adressList.get(0).getCountryName());
                            }
                            if(!mBeskrivelse.getText().toString().isEmpty()){
                                maal.setBeskrivelse(storForBokstav(mBeskrivelse.getText().toString()));
                            }
                            if(!mType.getText().toString().isEmpty()){
                                maal.setType(storForBokstav(mType.getText().toString()));
                            }







                            ///Lagrer opplysningene i SQLITE basen
                            DatabaseOperasjoner dbOp = new DatabaseOperasjoner(OpplysningerActivity.this);
                            dbOp.putInformation(dbOp, maal);

                        }

                        Intent tilBakeTilMain = new Intent(OpplysningerActivity.this,MainActivity.class);
                        startActivity(tilBakeTilMain);


                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Aktiver " + locationProvider + " under Location i Settings", Toast.LENGTH_LONG).show();
                }


                ///////////////////////


            }
        });


    }
    public String storForBokstav(String orginal){
        if(orginal.isEmpty())
            return orginal;
        return orginal.substring(0,1).toUpperCase()+orginal.substring(1).toLowerCase();


    }

}
