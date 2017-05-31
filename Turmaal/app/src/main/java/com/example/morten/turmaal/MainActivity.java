package com.example.morten.turmaal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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
    private boolean tilgang = false;

    public static final String REGANSVARLIGINFO = "com.example.morten.turmaal";
    public static final String FIRST_USE_SETTING = "com.example.morten.turmaal";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Turmaal maal = new Turmaal();
        maal.setNavn("Test");
        maal.setType("Topp");
        maal.setBeskrivelse("test");
        maal.setRegAnsvarlig("test");
        maal.setHoyde(10);
        maal.setLengdegrad(9.3322444f);
        maal.setBreddegrad(59.634343f);
        maal.setBilde_URL("http//Test.no");
        DatabaseOperasjoner dbOp = new DatabaseOperasjoner(MainActivity.this);

        dbOp.putInformation(dbOp, maal);


        if (isOnline()) {
            if (DatabaseOperasjoner.doesDatabaseExist(MainActivity.this, "TUR.DB")) {

                Toast.makeText(this, "SQLI basen ER opprettet!!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "SQLI basen er ikke opprettet", Toast.LENGTH_LONG).show();
            }


            Cursor CR = dbOp.getInformation(dbOp);
            if (CR != null) {

                ArrayList<Turmaal> list = Turmaal.lagTurListeFraSqlite(CR);
                new LastOppFraSQLite(getApplicationContext(), list).execute();

              
            }


        }


        //sjekker om brukeren er online
        if (isOnline()) {

            new JsonStartTask().execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regTurmaalIntent = new Intent(MainActivity.this, RegTurmaalActivity.class);
                startActivity(regTurmaalIntent);
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
        final Dialog dialog = new Dialog(MainActivity.this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_RegNavn) {
            // Henter delte preferanser
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.layout_dialogbox, null);

            final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE_SETTING, false);


            final EditText mNavn = (EditText) mView.findViewById(R.id.editNavn);
            final EditText mPassword = (EditText) mView.findViewById(R.id.editPass);
            Button endre = (Button) mView.findViewById(R.id.login_btn);
            Button avbryt = (Button) mView.findViewById(R.id.avslutt_btn);
            TextView tekst = (TextView) mView.findViewById(R.id.dialogBoxTekst);
            tekst.setText("Legg inn navn og passord");
            boolean firstUse = preferences.getBoolean(FIRST_USE_SETTING, true);
            if (firstUse) {
                endre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!mNavn.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()) {
                            regAnsvarligNavn = mNavn.getText().toString();
                            editor.putString("regAnsvarligNavn", regAnsvarligNavn);
                            editor.putString("Passord", mPassword.getText().toString());
                            tilgang = true;
                            editor.apply();
                            Toast.makeText(MainActivity.this, "Du er registrert og lagret", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else {
                            Toast.makeText(MainActivity.this, "Fyll inn feltet", Toast.LENGTH_SHORT).show();
                          

                        }

                    }
                });


            } else {
                tekst.setText("Hei legg inn passord");
                mNavn.setEnabled(false);
                regAnsvarligNavn = preferences.getString("regAnsvarligNavn", "");


                endre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!mPassword.getText().toString().isEmpty()&&preferences.getString("Passord", "").toString().equals(mPassword.getText().toString())) {


                                Toast.makeText(MainActivity.this, preferences.getString("Passord", "") + "  " + mPassword.getText().toString(), Toast.LENGTH_SHORT).show();
                                tilgang = true;


                            Toast.makeText(MainActivity.this, "Du kan gå videre", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else {
                            Toast.makeText(MainActivity.this, "Passordet er feil", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }

                    }

                });


            }

            ab.setView(mView);
            AlertDialog a = ab.create();
            a.show();


            return true;


        } else if (id == R.id.action_Endre) {


            // Henter delte preferanser
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.layout_dialogbox, null);

            final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE_SETTING, false);


            final EditText mNavn = (EditText) mView.findViewById(R.id.editNavn);
            final EditText mPassword = (EditText) mView.findViewById(R.id.editPass);
            Button endre = (Button) mView.findViewById(R.id.login_btn);
            Button avbryt = (Button) mView.findViewById(R.id.avslutt_btn);
            TextView tekst = (TextView) mView.findViewById(R.id.dialogBoxTekst);

            // Read the FIRST_USE_SETTING. Default value=true
            boolean firstUse = preferences.getBoolean(FIRST_USE_SETTING, true);
            if (!firstUse) {
                tekst.setText("Endre navn eller Passord");


                endre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!mNavn.getText().toString().isEmpty()) {
                            regAnsvarligNavn = mNavn.getText().toString();
                            editor.putString("regAnsvarligNavn", regAnsvarligNavn);
                        }
                        if (!mPassword.getText().toString().isEmpty()) {
                            editor.putString("Passord", mPassword.getText().toString());
                            tilgang = true;
                            editor.apply();
                            Toast.makeText(MainActivity.this, "Dine endringer er registrert og lagret", Toast.LENGTH_SHORT).show();
                        }
                        if (mNavn.getText().toString().isEmpty() && mPassword.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Du har ikke gjort noen endringer", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }


                });


            } else {
                Toast.makeText(MainActivity.this, "Du må først registrer deg", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            ab.setView(mView);
            AlertDialog a = ab.create();
            a.show();


        } else if (id == R.id.action_Slett) {


            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Er du sikker");


            //Legger til en positiv reaksjon knapp for å legge inn tekst i tekstviewet
            ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final SharedPreferences preferences = getSharedPreferences(REGANSVARLIGINFO, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
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


    class JsonStartTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

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

            if (result != null) {

                progressDialog.cancel();


                try {

                    startListView = (ListView) findViewById(R.id.startList);
                    tmListe = Turmaal.lagTurListe(result);
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


}
