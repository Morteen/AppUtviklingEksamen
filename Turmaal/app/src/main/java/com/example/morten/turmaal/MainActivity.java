package com.example.morten.turmaal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
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
    static String regAnsvarligNavn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
Turmaal maal= new Turmaal();
        maal.setNavn("Test");
        maal.setType("Topp");
        maal.setBeskrivelse("test");
        maal.setRegAnsvarlig("test");
        maal.setHoyde(10);
        maal.setLengdegrad(9.3322444f);
        maal.setBreddegrad(59.634343f);
       maal.setBilde_URL("http//Test.no");
        DatabaseOperasjoner dbOp = new DatabaseOperasjoner(MainActivity.this);

        dbOp.putInformation(dbOp,maal);


        if (isOnline()) {
            if(DatabaseOperasjoner.doesDatabaseExist(MainActivity.this,"TUR.DB")){

                Toast.makeText(this, "SQLI basen ER opprettet!!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "SQLI basen er ikke opprettet", Toast.LENGTH_LONG).show();
            }


            Cursor CR =dbOp.getInformation(dbOp);
           if(CR!=null){
              CR.moveToFirst();
                   Toast.makeText(getApplicationContext(),CR.getString(6).toString(),Toast.LENGTH_LONG).show();
               ArrayList<Turmaal>list=Turmaal.lagTurListeFraSqlite(CR);
               Toast.makeText(getApplicationContext(),list.size()+" Liste size",Toast.LENGTH_LONG).show();
               for(int i=0;i<list.size();i++){
               new LastOppFraSQLite(getApplicationContext(),list.get(i)).execute();
               }
           }
           //dbOp.slettSqliteBase(dbOp);
            Cursor CR2 =dbOp.getInformation(dbOp);
            if(CR2!=null){
                ArrayList<Turmaal>list2=Turmaal.lagTurListeFraSqlite(CR2);
                              Toast.makeText(getApplicationContext(), list2.size()+ " Etter sletting av SQLIte DB",Toast.LENGTH_LONG).show();

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
                Intent regTurmaalIntent= new Intent(MainActivity.this,RegTurmaalActivity.class);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Legg inn et navn for du vil \n registrere turmålet under");
            final EditText etNavn = new EditText(MainActivity.this);
            ab.setView(etNavn);

            //Legger til en positiv reaksjon knapp for å legge inn tekst i tekstviewet
            ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (!etNavn.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Du er registrert", Toast.LENGTH_SHORT).show();
                       regAnsvarligNavn=etNavn.getText().toString();
                    } else {
                        Toast.makeText(MainActivity.this, "Fyll inn feltet", Toast.LENGTH_SHORT).show();

                    }

                }
            });


            ab.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            AlertDialog a = ab.create();
            a.show();


            return true;
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
                            Toast.makeText(getApplicationContext(), "Posisjon " + position, Toast.LENGTH_LONG).show();
                            curTm = tmListe.get(position);
                            Intent VisCurIntent = new Intent(getApplicationContext(), VisEtMaalActivity.class);
                            startActivity(VisCurIntent);
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
