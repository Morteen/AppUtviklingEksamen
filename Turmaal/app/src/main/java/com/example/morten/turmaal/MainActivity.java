package com.example.morten.turmaal;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

        DatabaseOperasjoner dbOp = new DatabaseOperasjoner(MainActivity.this);


        if (isOnline()) {
            LastOppFraSQLite lastOpp= new LastOppFraSQLite(MainActivity.this);
            lastOpp.execute();
        }

        //Dette er for å vise bilder fra JsonObjektet
        // Create global configuration and initialize ImageLoader with this config
        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().init(config);


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

     class LastOppFraSQLite2 extends AsyncTask<String,String,Long> {

         public LastOppFraSQLite2() {

         }

         HttpURLConnection connection = null;
Context context=MainActivity.this;


        DatabaseOperasjoner dbOpersjoner= new DatabaseOperasjoner(context);
        private Long result;



        @Override
        protected Long doInBackground(String... params) {
            Cursor cursor=dbOpersjoner.getInformation(dbOpersjoner);
            ArrayList<Turmaal> tmList=Turmaal.lagTurListeFraSqlite(cursor);

            String insert_URI = "http://itfag.usn.no/~210144/api.php/Turmaal";
            try {
                URL insertUrl= new URL(insert_URI);
                connection=(HttpURLConnection) insertUrl.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                //connection.setRequestProperty("Content- Type","application/json; charset=UTF -8" ");
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                for (int i=0;i<tmList.size();i++){

                    JSONObject JsonMaal= tmList.get(i).toJSONObject();
                    out.write(JsonMaal.toString());

                }
                out.close();
                int status=connection.getResponseCode();
                if(status==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                    String responseString ;
                    StringBuilder sb= new StringBuilder();
                    while ((responseString=reader.readLine())!=null){
                        sb=sb.append(responseString);
                        reader.close();
                        if(sb.toString().equals("0")){
                            return 0l;
                        }else{
                            return 1l;
                        }
                    }
                }else {return 1l;}


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Long result) {

            if(result==0){
                Toast.makeText(context,"Tror det virket",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Noe er galt",Toast.LENGTH_LONG).show();
            }
        }



    }


}
