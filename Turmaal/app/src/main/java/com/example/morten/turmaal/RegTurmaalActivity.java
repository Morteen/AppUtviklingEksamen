package com.example.morten.turmaal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;


public class RegTurmaalActivity extends AppCompatActivity {
    Button regturknp, kamera, visBilde;
    String bildeNavn;
    ImageView bildeView;
    static final int KAMERA_REQUEST = 1;
    static final int BILDE_REQUEST= 2;
    LocationManager locationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    public final static int MY_REQUEST_LOCATION = 1;
    Location myLocation = null;
    Bitmap testFoto;
    File bildeFil;
    Uri uri;
    Context context;
    Turmaal maal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_turmaal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      maal= new Turmaal();
        maal.setRegAnsvarlig(MainActivity.regAnsvarligNavn);


        regturknp = (Button) findViewById(R.id.regKnp);
        kamera = (Button) findViewById(R.id.kameraKnp);
        bildeView = (ImageView) findViewById(R.id.imageView);
        visBilde = (Button) findViewById(R.id.lagre);

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
                        double lengdeGrad = myLocation.getLongitude();
                        double breddeGrad = myLocation.getLatitude();
                       int hoyde = (int)myLocation.getAltitude();
                        maal.setHoyde(hoyde);
                        maal.setLengdegrad((float)lengdeGrad);
                        maal.setBreddegrad((float)breddeGrad);
                        Geocoder geocoder = new Geocoder(RegTurmaalActivity.this);
                        List<Address> adressList = null;
                        try {
                            adressList = geocoder.getFromLocation(breddeGrad, lengdeGrad, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String start = "Start sted: " + adressList.get(0).getLocality() + " -";
                        start += adressList.get(0).getCountryName();
                        maal.setNavn(start);
                       // Toast.makeText(getApplicationContext(),
                               // "Lengdegrad:" + lengdeGrad + "\nBreddegrad:" + breddeGrad + "\nHøyde:" + hoyde + "\nAdresse:" + start, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Aktiver " + locationProvider + " under Location i Settings", Toast.LENGTH_LONG).show();
                }

                ///////////////////////


            }
        });
        if (!harKamera()) {
            kamera.setEnabled(false);
            Toast.makeText(getApplicationContext(),
                    "Har ikke kamera", Toast.LENGTH_LONG).show();
        }
        kamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent kameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File bildeMappe = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

               bildeNavn = getBildeNavn();
                bildeFil = new File(bildeMappe, bildeNavn);
                uri = Uri.fromFile(bildeFil);
                maal.setBilde_URL(uri.toString());
                Toast.makeText(getApplicationContext(),maal.getRegAnsvarlig()+"\n"+maal.getNavn()+"\n"+maal.getHoyde()+"\n"+maal.getBilde_URL()+"\n"+maal.getBreddegrad(), Toast.LENGTH_LONG).show();
                Log.d("bildeadresse",uri.toString());
                //Toast.makeText(getApplicationContext(), bildeFil.toString(), Toast.LENGTH_LONG).show();
                kameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(kameraIntent, KAMERA_REQUEST);//

            }
        });


        visBilde.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.FROYO)
            @Override
            public void onClick(View v) {

 String test="/DCIM/100ANDRO/DSC_0067.JPG";
                String tempAdr=" /storage/emulated/0";
                File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                                Uri file=Uri.fromFile(new File(downloadsFolder,test));
                if(file.toString() != null && file.toString().length()>0)
                {
                    //Toast.makeText(getApplicationContext(),file.toString(), Toast.LENGTH_LONG).show();

                    Picasso.with(RegTurmaalActivity.this).load(file).placeholder(R.drawable.common_google_signin_btn_icon_dark_normal).into(bildeView);

          context=RegTurmaalActivity.this;
                    DatabaseOperasjoner DB = new DatabaseOperasjoner(context);
                    DB.putInformation(DB,maal);
                    Toast.makeText(getBaseContext(),"Registring suksessfull",Toast.LENGTH_LONG).show();


                }else
                {
                    Toast.makeText(RegTurmaalActivity.this, "Tom URI", Toast.LENGTH_SHORT).show();
                }





               Intent fotoFinnerIntent = new Intent(Intent.ACTION_PICK);
                //Android finner riktig mappe og fil adresse
                File bildeMappe=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String mappeSti= bildeMappe.getPath();
                   //String tempAdr=" file:///storage/emulated/0/Pictures/TurBilde2017_05_30.jpg";
               // String tempAdr=" /storage/emulated/0/Pictures/TurBilde2017_05_30.jpg";

                Uri data =Uri.parse(tempAdr);
                fotoFinnerIntent.setDataAndType(uri,"image/*");

                startActivityForResult(fotoFinnerIntent,BILDE_REQUEST);


            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getBildeNavn() {
        SimpleDateFormat tidsStamp = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String tidsPunkt = tidsStamp.format(new Date());
        return "TurBilde" + tidsPunkt + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == KAMERA_REQUEST) {
            Toast.makeText(getApplicationContext(), RESULT_OK + "  " + resultCode + "Ok Ok " + requestCode, Toast.LENGTH_LONG).show();
            //Bitmap foto = (Bitmap)  data.getExtras().get("data");
            // Bundle extra = data.getExtras();
            //Bitmap foto = (Bitmap) extra.get("data");
            //bildeView.setImageBitmap(foto);


        } else if(resultCode==RESULT_OK&&requestCode==BILDE_REQUEST){


            Toast.makeText(getApplicationContext(),
                   "Det virker så langt"+ resultCode + " " + requestCode, Toast.LENGTH_LONG).show();

            Uri bildeUri= data.getData();
            //Deklarere en stream for å lese fra disken
            InputStream inputStream;
            try {
                inputStream=getContentResolver().openInputStream(bildeUri);
                //Får et bilde fra strømmen
               Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                bildeView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Toast.makeText(this,"Kan ikke åpne bildet",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


    }

    private boolean harKamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void loadImageFromStorage(File path) {


        File f = path;
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //ImageView img=(ImageView)findViewById(R.id.imgPicker);
        bildeView.setImageBitmap(b);


    }


}
