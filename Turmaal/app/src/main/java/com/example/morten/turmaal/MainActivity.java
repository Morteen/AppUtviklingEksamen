package com.example.morten.turmaal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        if(isOnline()){
            new JsonStartTask().execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class JsonStartTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

String turData=null;
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
            String turmaalURL="http://itfag.usn.no/~210144/api.php/Turmaal?transform=1";

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
        protected void onPostExecute(String  result) {

            if (result!=null) {

                progressDialog.cancel();


                try {
                    /* DeltarListView = (ListView) view.findViewById(R.id.list_DeltarKurs);
        KursAdapter adapter = new KursAdapter(getContext(), deltarkursList);*/
                    startListView=(ListView)findViewById(R.id.startList);
                    tmListe=Turmaal.lagTurListe(result);
                  TurAdapter adapter= new TurAdapter(getApplicationContext(), tmListe);
                    startListView.setAdapter(adapter);
                    startListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getApplicationContext(),"Posisjon "+ position,Toast.LENGTH_LONG).show();
                             curTm= tmListe.get(position);
                            Intent VisCurIntent= new Intent(getApplicationContext(),VisEtMaalActivity.class);
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



}
