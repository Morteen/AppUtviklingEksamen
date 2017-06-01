package com.example.morten.turmaal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class RegTurmaalActivity extends AppCompatActivity {
    Button  kamera, visBilde;
    static String bildeNavn;

    ImageView bildeView;
    private String mCurrentPhotoPath = null; // Objektvariabel med fullstendig filnavn til bildet
    private static final String FOTO_MAPPE = "MineBilder"; // Katalognavn for bildene tatt med denne appen
    private static final int Ta_bilde_V2 = 2;

    Bitmap testFoto;
    File bildeFil;
    Uri uri;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_turmaal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        kamera = (Button) findViewById(R.id.kameraKnp);
        bildeView = (ImageView) findViewById(R.id.imageView);
        visBilde = (Button) findViewById(R.id.Visbilde);



        if (!harKamera()) {
            kamera.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Telefonen har ikke kamera", Toast.LENGTH_LONG).show();
        }
        kamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Lag File objektet som bildet skal lagres på
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) { // Feil ved oppretting av fil

                        Toast.makeText(RegTurmaalActivity.this, "Feil ved oppretting av fil for bilde.", Toast.LENGTH_LONG).show();
                    }
                    // Fortsett hvis File objektet ble laget
                    if (photoFile != null) {
                        Uri PhotoUri = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoUri);
                        startActivityForResult(takePictureIntent, Ta_bilde_V2);
                    }
                }


            }
        });


        visBilde.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.FROYO)
            @Override
            public void onClick(View v) {
                visBildeSkalert(mCurrentPhotoPath);
                Intent opplysningerIntent = new Intent(RegTurmaalActivity.this,OpplysningerActivity.class);
                startActivity(opplysningerIntent);

            }
        });


    }


    private boolean harKamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private File createImageFile() throws IOException {
// Lag et unikt filnavn for bildet
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
// Finn mappe for bilder under /sdcard/Pictures
        File photoStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", photoStorageDir);
// Lagrer fullstendig filnavn i en objektvariabel for bruk i andre metoder
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private File getPhotoDir() {
// Finn/lag undermappe for bilder under Pictures mappen på felles eksternt lager
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), FOTO_MAPPE);
// Opprett undermappen hvis den ikke alt finnes
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }
        return mediaStorageDir;
    }

    private void visBildeSkalert(String photoPath) {
        // Finn høyde og bredde på ImageViewet
       /* int targetW = bildeView.getWidth();
        int targetH = bildeView.getHeight();
        // Les dimensionene til bildet
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Beregn hvor mye bildet må nedskaleres for å passe i ImageViewet
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Les bildefilen inn i et Bitmap objekt med valgt skalering
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
       // Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);*/
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        bildeView.setImageBitmap(bitmap);
        // bildeView.setVisibility(View.VISIBLE);

    }

    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        bildeFil = f;
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ta_bilde_V2 && resultCode == RESULT_OK) {
            galleryAddPic(mCurrentPhotoPath);

           bildeNavn=mCurrentPhotoPath;


            new FileUpload(RegTurmaalActivity.this, bildeFil);
        }
    }


}
