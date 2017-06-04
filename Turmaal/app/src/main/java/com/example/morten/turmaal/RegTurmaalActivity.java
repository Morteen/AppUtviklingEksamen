package com.example.morten.turmaal;
/*Etter mye problemer endte jeg opp med å bruke det jeg lagde under forelesningen om kamera
* */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.createBitmap;

/*
* Denne aktiviteten tar bilder av turmålet og lagrer til fil
* Jeg hadde så mye problemer her at jeg endte opp med å kopier det jeg gjorde under forelesningen om emnet
* */
public class RegTurmaalActivity extends AppCompatActivity {
    Button kamera, lagre;
    static String bildeNavn;

    ImageView bildeView;
    private String mCurrentPhotoPath = null;
    private static final String FOTO_MAPPE = "MineBilder";
    private static final int Ta_bilde_V2 = 2;

    File bildeFil;
    Uri uri;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_turmaal);


        kamera = (Button) findViewById(R.id.kameraKnp);
        lagre = (Button) findViewById(R.id.lagre);
        bildeView = (ImageView) findViewById(R.id.imageView);


        if (!harKamera()) {
            kamera.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Telefonen har ikke kamera", Toast.LENGTH_LONG).show();
        }
        kamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent for å starte standard kamera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) { // Feil ved oppretting av fil

                        Toast.makeText(RegTurmaalActivity.this, "Feil ved oppretting av fil for bilde.", Toast.LENGTH_LONG).show();
                    }

                    if (photoFile != null) {
                        Uri PhotoUri = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoUri);
                        startActivityForResult(takePictureIntent, Ta_bilde_V2);
                    }
                }


            }
        });


        lagre.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.FROYO)
            @Override
            public void onClick(View v) {
                //Starter en intent for lagre valgte bilde
                Intent opplysningerIntent = new Intent(RegTurmaalActivity.this, OpplysningerActivity.class);
                startActivity(opplysningerIntent);

            }
        });


    }

    /***
     * Sjekker om det finnes kamera
     * @return om appen har kamera eller ikke
     */
    private boolean harKamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private File createImageFile() throws IOException {
        // Lag et unikt filnavn for bildet
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private File getPhotoDir() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), FOTO_MAPPE);
        // Opprett undermappen hvis den ikke alt finnes
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("BildeEksempel", "Klarte ikke å lage mappe");
                return null;
            }
        }
        return mediaStorageDir;
    }

    private void visBildeSkalert(String photoPath) {
        // Finn høyde og bredde på ImageViewet
        int targetW = bildeView.getWidth();
        int targetH = bildeView.getHeight();
        // Les dimensionene til bildet
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Les bildefilen inn i et Bitmap objekt med valgt skalering
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        Bitmap snudd = snuBilde(bitmap, photoPath);
        bildeView.setImageBitmap(snudd);


    }


    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ta_bilde_V2 && resultCode == RESULT_OK) {

            galleryAddPic(mCurrentPhotoPath);
            visBildeSkalert(mCurrentPhotoPath);

            bildeNavn = mCurrentPhotoPath;

        }
    }


    /***
     * Dette er en metode som snur bildet.
     Da denne appen tar landskaps bilder blir bilde tatt fra deg riktig. Selfi-mode blir oppned
     * @param bitmap
     * @param filesti
     * @return Bitmap med bildet i riktig posisjon
     */


    private Bitmap snuBilde(Bitmap bitmap, String filesti) {
        Bitmap resultBitmap = bitmap;

        try {
            ExifInterface exifInterface = new ExifInterface(filesti);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            resultBitmap = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception exception) {
            Log.d("Rotate", "Kunne ikke snu bildet");
        }
        return resultBitmap;
    }


}
