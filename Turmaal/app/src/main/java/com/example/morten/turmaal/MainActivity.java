package com.example.morten.turmaal;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView startListView;
    static ArrayList<Turmaal> tmListe;
    static Turmaal curTm;
    static String regAnsvarligNavn;
    private boolean tilgang;

    public static final String REGANSVARLIGINFO = "com.example.morten.turmaal";
    public static final String FIRST_USE_SETTING = "com.example.morten.turmaal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tilgang = false;


        DatabaseOperasjoner dbOp = new DatabaseOperasjoner(MainActivity.this);


        if (isOnline()) {
            if (DatabaseOperasjoner.doesDatabaseExist(MainActivity.this, "TUR.DB")) {


            } else {
                Toast.makeText(this, "SQLI basen er ikke opprettet", Toast.LENGTH_LONG).show();
            }

//Henter data fra SQLite databasen
            Cursor CR = dbOp.getInformation(dbOp);
            if (CR != null) {

                ArrayList<Turmaal> list = Turmaal.lagTurListeFraSqlite(CR);
                new LastOppFraSQLite(getApplicationContext(), list).execute();


            }


        }


        //sjekker om brukeren er online
        //og overfører SQLITE data hvis den er på nett
        if (isOnline()) {

            new JsonStartTask().execute();
        } else {
            Toast.makeText(this, "Telefonene er ikke på nett", Toast.LENGTH_LONG).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilgang) {
                    Intent regTurmaalIntent = new Intent(MainActivity.this, RegTurmaalActivity.class);
                    startActivity(regTurmaalIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Du må logge inn først", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //final Dialog dialog = new Dialog(MainActivity.this);
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_Legg_til) {
            if (tilgang) {
                Intent regTurmaalIntent = new Intent(MainActivity.this, RegTurmaalActivity.class);
                startActivity(regTurmaalIntent);
            } else {
                Toast.makeText(MainActivity.this, "Du må logge inn først", Toast.LENGTH_LONG).show();
            }

        }
        if (id == R.id.action_LogIn) {

            final Dialog dialog = new Dialog(MainActivity.this);

            dialog.setContentView(R.layout.layout_dialogbox);
            final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE_SETTING, false);


            final EditText mNavn = (EditText) dialog.findViewById(R.id.editNavn);
            final EditText mPassword = (EditText) dialog.findViewById(R.id.editPass);
            Button endre = (Button) dialog.findViewById(R.id.login_btn);
            Button avbryt = (Button) dialog.findViewById(R.id.avslutt_btn);
            TextView tekst = (TextView) dialog.findViewById(R.id.dialogBoxTekst);

            tekst.setText("Hei legg inn passord");
            regAnsvarligNavn = preferences.getString("regAnsvarligNavn", "");
            mNavn.setHint(regAnsvarligNavn);
            mNavn.setEnabled(false);


            endre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!mPassword.getText().toString().isEmpty() && preferences.getString("Passord", "").toString().equals(mPassword.getText().toString())) {


                        Toast.makeText(MainActivity.this, preferences.getString("Passord", "") + "  " + mPassword.getText().toString(), Toast.LENGTH_SHORT).show();
                        tilgang = true;


                        Toast.makeText(MainActivity.this, "Du kan gå videre", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Toast.makeText(MainActivity.this, "Passordet er feil", Toast.LENGTH_SHORT).show();
                        tilgang = false;
                        dialog.dismiss();

                    }

                }

            });
            avbryt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tilgang = false;
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


        if (id == R.id.action_RegNavn) {

            final Dialog dialog = new Dialog(MainActivity.this);

            dialog.setContentView(R.layout.layout_dialogbox);
            final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE_SETTING, false);


            final EditText mNavn = (EditText) dialog.findViewById(R.id.editNavn);
            final EditText mPassword = (EditText) dialog.findViewById(R.id.editPass);
            Button endre = (Button) dialog.findViewById(R.id.login_btn);
            Button avbryt = (Button) dialog.findViewById(R.id.avslutt_btn);
            TextView tekst = (TextView) dialog.findViewById(R.id.dialogBoxTekst);
            tekst.setText("Legg inn navn og passord");


            boolean firstUse = preferences.getBoolean(FIRST_USE_SETTING, true);
            if (firstUse) {
                endre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!mNavn.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()) {
                            regAnsvarligNavn = storForBokstav(mNavn.getText().toString());
                            editor.putString("regAnsvarligNavn", regAnsvarligNavn);
                            editor.apply();
                            editor.putString("Passord", mPassword.getText().toString());
                            editor.apply();
                            tilgang = true;
                            Toast.makeText(MainActivity.this, "Du er registrert og lagret", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Fyll inn feltet", Toast.LENGTH_SHORT).show();


                        }

                    }
                });
                avbryt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tilgang = false;
                        dialog.cancel();
                    }
                });
                dialog.show();

            } else {


                Toast.makeText(MainActivity.this, "Bruk login knappen", Toast.LENGTH_SHORT).show();

            }


            return true;


        } else if (id == R.id.action_Endre) {


            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.layout_dialogbox);

            final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE_SETTING, false);


            final EditText mNavn = (EditText) dialog.findViewById(R.id.editNavn);
            final EditText mPassword = (EditText) dialog.findViewById(R.id.editPass);
            Button endre = (Button) dialog.findViewById(R.id.login_btn);
            Button avbryt = (Button) dialog.findViewById(R.id.avslutt_btn);
            TextView tekst = (TextView) dialog.findViewById(R.id.dialogBoxTekst);

            // Read the FIRST_USE_SETTING. Default value=true
            boolean firstUse = preferences.getBoolean(FIRST_USE_SETTING, true);
            if (!firstUse) {
                tekst.setText("Endre navn eller Passord");

                endre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mNavn.getText().toString().isEmpty() && mPassword.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Du har ikke gjort noen endringer", Toast.LENGTH_SHORT).show();
                        } else {

                            if (!mNavn.getText().toString().isEmpty()) {
                                regAnsvarligNavn = storForBokstav(mNavn.getText().toString());
                                editor.putString("regAnsvarligNavn", regAnsvarligNavn);
                                editor.apply();
                            }
                            if (!mPassword.getText().toString().isEmpty()) {
                                editor.putString("Passord", mPassword.getText().toString());
                                tilgang = true;
                                editor.apply();


                            }
                            Toast.makeText(MainActivity.this, "Dine endringer er registrert og lagret", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }


                    }


                });


            } else {
                Toast.makeText(MainActivity.this, "Du må først registrer deg", Toast.LENGTH_SHORT).show();

            }
            avbryt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        } else if (id == R.id.action_Slett) {


            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Er du sikker");


            //Legger til en positiv reaksjon knapp for å kunne slette opplysningene
            ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    tilgang = false;
                    dialog.dismiss();

                }
            });


            ab.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog a = ab.create();
            a.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Henter data fra databasen, finner posisjonen hvor appen blir startet og bruker  Turmaal.sorterListe til å legge turmålen i rekkefølge
    class JsonStartTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        public final static int MY_REQUEST_LOCATION = 1;


        String turData = null;

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        public JsonStartTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Vent litt nå da..");
            progressDialog.setTitle("Kobler opp...");
            progressDialog.show();
            progressDialog.setProgressStyle(3);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {


            // HttpURLConnection connection= null;
            BufferedReader reader = null;
            String line = "";
            String result = "";
            InputStream is = null;
            String turmaalURL = "http://itfag.usn.no/~210144/api.php/Turmaal?transform=1";

            HttpURLConnection connection = null;
            try {
                URL actorUrl = new URL(turmaalURL);

                connection = (HttpURLConnection) actorUrl.openConnection();
                connection.connect();
                int status = connection.getResponseCode();
                Log.d("connection", "status " + status);

                is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                String responseString;
                StringBuilder sb = new StringBuilder();
                while ((responseString = reader.readLine()) != null) {
                    sb = sb.append(responseString);
                }
                turData = sb.toString();

                Log.d("connection", turData);


                return turData;


            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                System.out.println("IOException");
                e.printStackTrace();

            } catch (NullPointerException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                connection.disconnect();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String result) {


            final LocationManager locationManager;
            locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
            final String locationProvider = LocationManager.GPS_PROVIDER;
            Location myLocation = null;


            if (result != null) {

                progressDialog.cancel();


                if (locationManager.isProviderEnabled(locationProvider)) {


                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

                    //int permissionCheck = getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName());
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        //** Hvis tillatelse ikke er gitt må programmet spørre brukeren
                        // Denne fungerer også før API 23 med AppCompatActivity:
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);

                    } else {
                        // Hent siste kjente posisjon
                        myLocation = locationManager.getLastKnownLocation(locationProvider);
                    }
                }


                try {

                    startListView = (ListView) findViewById(R.id.startList);
                    tmListe = Turmaal.lagTurListe(result);
                    if (myLocation != null) {
                        Turmaal.sorterListe(tmListe, myLocation);
                    }
                    TurAdapter adapter = new TurAdapter(getApplicationContext(), tmListe);
                    startListView.setAdapter(adapter);
                    startListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (!tilgang) {
                                Toast.makeText(getApplicationContext(), "Du må logge inn ", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Posisjon " + position, Toast.LENGTH_LONG).show();
                                curTm = tmListe.get(position);
                                Intent VisCurIntent = new Intent(getApplicationContext(), VisEtMaalActivity.class);
                                startActivity(VisCurIntent);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

                Toast.makeText(getApplicationContext(), "Fikk ikke hentet noen data", Toast.LENGTH_SHORT).show();

                progressDialog.cancel();

            }

        }
    }


    /**
     * Sjekker om  det er nettverkstilgang
     *
     * @return true or  false
     */
    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    ////////////////////////////////////////////////////////////////

    public String storForBokstav(String orginal) {
        if (orginal.isEmpty())
            return orginal;
        return orginal.substring(0, 1).toUpperCase() + orginal.substring(1).toLowerCase();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("RegAnsv",regAnsvarligNavn);
        outState.putBoolean("Tilgang",tilgang);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        regAnsvarligNavn=savedInstanceState.getString("RegAnsv");
        tilgang=savedInstanceState.getBoolean("Tilgang");

    }
}
