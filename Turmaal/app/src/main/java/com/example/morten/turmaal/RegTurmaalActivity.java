package com.example.morten.turmaal;

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


public class RegTurmaalActivity extends AppCompatActivity {
    Button  kamera, lagre;
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


        kamera = (Button) findViewById(R.id.kameraKnp);
       lagre= (Button) findViewById(R.id.lagre);
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
// Sjekk om det finnes en app som vil behandle Intent'en
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



       lagre.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.FROYO)
            @Override
            public void onClick(View v) {

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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
// Finn mappe for bilder under /sdcard/Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir);
// Lagrer fullstendig filnavn i en objektvariabel for bruk i andre metoder
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private File getPhotoDir() {
//// Finn/lag undermappe for bilder under Pictures mappen på felles eksternt lager
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), FOTO_MAPPE);
// Opprett undermappen hvis den ikke alt finnes
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
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
        // Beregn hvor mye bildet må nedskaleres for å passe i ImageViewet
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Les bildefilen inn i et Bitmap objekt med valgt skalering
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        //Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        Bitmap snudd= snuBilde(bitmap, photoPath);
        bildeView.setImageBitmap( snudd);
        // bildeView.setVisibility(View.VISIBLE);

    }




    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Ta_bilde_V2&& resultCode == RESULT_OK) {
            Toast.makeText(RegTurmaalActivity.this,requestCode+" og "+resultCode,Toast.LENGTH_LONG).show();
            galleryAddPic(mCurrentPhotoPath);
            visBildeSkalert(mCurrentPhotoPath);



            //new FileUpload(RegTurmaalActivity.this, bildeFil);
        }
    }
    public String storForBokstav(String orginal){
        if(orginal.isEmpty())
            return orginal;
        return orginal.substring(0,1).toUpperCase()+orginal.substring(1).toLowerCase();


    }



    private Bitmap snuBilde(Bitmap bitmap, String filesti)
    {
        Bitmap resultBitmap = bitmap;

        try
        {
            ExifInterface exifInterface = new ExifInterface(filesti);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();


            matrix.postRotate(270);
            // Snu bitmap
            resultBitmap = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        catch (Exception exception)
        {
            Log.d("Rotate","Kunne ikke snu bildet");
        }
        return resultBitmap;
    }


}
