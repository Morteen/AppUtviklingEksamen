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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.List;

public class OpplysningerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    Button lagreTm;
    EditText mType,mBeskrivelse,mNavn;
    public final static int MY_REQUEST_LOCATION = 1;

    Turmaal maal;
    GoogleApiClient mGoogleApiClient=null;
    private Location minPosisjon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opplysninger);


        // Create an instance of GoogleAPIClient.


        if (mGoogleApiClient == null) {
            GoogleApiClient.Builder apiBuilder = new GoogleApiClient.Builder(this);
            apiBuilder.addConnectionCallbacks(this);        /* ConnectionCallbacks-objekt */
            apiBuilder.addOnConnectionFailedListener(this); /* OnConnectionFailedListener-objekt */
            apiBuilder.addApi(LocationServices.API);        /* Velg Play Service API */
            mGoogleApiClient = apiBuilder.build();
        }



        maal = new Turmaal();


        mNavn=(EditText)findViewById(R.id.eNavn);
        mBeskrivelse=(EditText)findViewById(R.id.eBeskrivelse);
        mType=(EditText)findViewById(R.id.eType);
        lagreTm = (Button) findViewById(R.id.lagreTm);

        lagreTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Toast.makeText(getApplicationContext(),
                        "Det er liv i knappen", Toast.LENGTH_LONG).show();
                /////////////////////////
                // Bruk LocationManager for å finne siste kjente posisjon


                        // Hent siste kjente posisjon

                        double lengdeGrad = minPosisjon.getLongitude();
                        double breddeGrad = minPosisjon.getLatitude();
                        int hoyde = (int)minPosisjon.getAltitude();


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






                ///////////////////////


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the ApiClient to Google Services
        mGoogleApiClient.connect();
    }


    // Implementasjon av metode fra interface GoogleApiClient.ConnectionCallbacks
    // Kalles når Api-klienten har fått kontakt

    @Override
    public void onConnected(Bundle connectionHint) {
        // Denne fungerer også før API 23 med AppCompatActivity:
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // Denne fungerer bare fra og med  API 23 med Activity:
        //int permissionCheck = this.getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName());
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //** Spør bruker om å gi appen tillatelsen ACCESS_FINE_LOCATION
            // Denne fungerer også før API 23 med AppCompatActivity:
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);
            // Denne fungerer bare fra og med  API 23 med Activity:
            //this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // OK: Appen har tillatelsen ACCESS_FINE_LOCATION. Finn siste posisjon
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.visPosisjon(lastLocation);
        }
    }










    public String storForBokstav(String orginal){
        if(orginal.isEmpty())
            return orginal;
        return orginal.substring(0,1).toUpperCase()+orginal.substring(1).toLowerCase();


    }

    // Callbackmetode som kalles etter at bruker har svart på spørsmål om rettigheter
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    minPosisjon = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    this.visPosisjon(minPosisjon);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(),
                        "Kan ikke vise posisjon uten tillatelse", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Får ikke kontakt med Google Play Services", Toast.LENGTH_LONG).show();
    }
}
