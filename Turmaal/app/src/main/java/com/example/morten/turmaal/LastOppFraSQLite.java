package com.example.morten.turmaal;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by morten on 30.05.2017.
 */

public class LastOppFraSQLite extends AsyncTask<String, String, Integer> {

    public LastOppFraSQLite(Context context, ArrayList<Turmaal> tm) {
        this.context = context;
        this.tm = tm;
    }

    ArrayList<Turmaal> tm;
    Context context;


    @Override
    protected Integer doInBackground(String... params) {
        int teller = 0;

        for (int i = 0; i < tm.size(); i++) {
            teller = lastOppEn(tm.get(i));
        }

        return teller;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (tm.size() > 0) {
            DatabaseOperasjoner dbOp = new DatabaseOperasjoner(context);
            dbOp.slettSqliteBase(dbOp);
        }


        Log.d("Resultat opplasting ", result + " " + tm.size());


    }


    private static int lastOppEn(Turmaal maal) {
        HttpURLConnection connection = null;

        String insert_URI = "http://itfag.usn.no/~210144/api.php/Turmaal";
        try {
            URL insertUrl = new URL(insert_URI);
            connection = (HttpURLConnection) insertUrl.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("Content- Type","application/json; charset=UTF -8" ");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());


            JSONObject JsonMaal = maal.toJSONObject();
            out.write(JsonMaal.toString());


            out.close();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String responseString;
                StringBuilder sb = new StringBuilder();
                while ((responseString = reader.readLine()) != null) {
                    sb = sb.append(responseString);
                    reader.close();
                    if (sb.toString().equals("0")) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            } else {
                return 1;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return 1;
    }


}
