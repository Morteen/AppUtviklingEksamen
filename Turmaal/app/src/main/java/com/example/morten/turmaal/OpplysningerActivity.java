package com.example.morten.turmaal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
/*
* Den aktiviteten skaffer posisjonen til turmalet og brukeren kan legge inn tekst  som beskriver stedet
 * */

public class OpplysningerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Button lagreTm;
    EditText mType, mBeskrivelse, mNavn;
    public final static int REQUEST_LOCATION = 1;
    double hoyde;
    Turmaal maal;
    GoogleApiClient mGoogleApiClient = null;
    private Location minPosisjon = null;
    String start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opplysninger);


        // lager et objekt av GoogleAPIClient.
        if (mGoogleApiClient == null) {
            GoogleApiClient.Builder apiBuilder = new GoogleApiClient.Builder(this);
            apiBuilder.addConnectionCallbacks(this);
            apiBuilder.addOnConnectionFailedListener(this);
            apiBuilder.addApi(LocationServices.API);
            mGoogleApiClient = apiBuilder.build();
        }


        maal = new Turmaal();
        mNavn = (EditText) findViewById(R.id.eNavn);
        mBeskrivelse = (EditText) findViewById(R.id.eBeskrivelse);
        mType = (EditText) findViewById(R.id.eType);
        lagreTm = (Button) findViewById(R.id.lagreTm);

        lagreTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /////////////////////////

                if (RegTurmaalActivity.bildeNavn != null) {
                    maal.setBilde_URL(RegTurmaalActivity.bildeNavn);
                    RegTurmaalActivity.bildeNavn = null;
                }
                if (!mNavn.getText().toString().isEmpty()) {
                    String temp=storForBokstav(mNavn.getText().toString());
                    if(!start.isEmpty()){
                      temp+= " -" +start;

                    }
                    maal.setNavn(temp);

                }
                if (!mBeskrivelse.getText().toString().isEmpty()) {
                    maal.setBeskrivelse(storForBokstav(mBeskrivelse.getText().toString()));
                }
                if (!mType.getText().toString().isEmpty()) {
                    maal.setType(storForBokstav(mType.getText().toString()));
                }
                if (RegTurmaalActivity.bildeNavn != null) {
                    maal.setBilde_URL(RegTurmaalActivity.bildeNavn);
                }

                ///Lagrer opplysningene i SQLITE basen
                DatabaseOperasjoner dbOp = new DatabaseOperasjoner(OpplysningerActivity.this);
                dbOp.putInformation(dbOp, maal);

                //Intent for å gå tilbake til Main med bundel med boolean true for ikke
                //bli logget ut i Main
                Bundle b = new Bundle();
                b.putBoolean("Tilgang", true);

                Intent tilBakeTilMain = new Intent(OpplysningerActivity.this, MainActivity.class);
                tilBakeTilMain.putExtras(b);
                startActivity(tilBakeTilMain);
                finish();

                ///////////////////////


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.visMinPos(lastLocation);
        }
    }


    private String storForBokstav(String orginal) {
        if (orginal.isEmpty())
            return orginal;
        return orginal.substring(0, 1).toUpperCase() + orginal.substring(1).toLowerCase();


    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    minPosisjon = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    this.visMinPos(minPosisjon);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(getApplicationContext(),
                        "Kan ikke vise posisjon uten tillatelse", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Får ikke kontakt med Google Play Services", Toast.LENGTH_LONG).show();
    }

    /***
     * Lagrer posisjon i et Turmaal objekt, legger og så på landet man er i
     * @param posisjon
     */
    private void visMinPos(Location posisjon) {
        if (posisjon != null) {
            double lengdeGrad = posisjon.getLongitude();
            double breddeGrad = posisjon.getLatitude();
            hoyde = posisjon.getAltitude();


            if (MainActivity.regAnsvarligNavn != null) {
                maal.setHoyde((int) hoyde);
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
                start = null;
                if (adressList != null) {

                    start = adressList.get(0).getCountryName();

                    maal.setNavn(start);
                }


            }

            lengdeGrad = 0.0;
            breddeGrad = 0.0;

        }
    }
}
