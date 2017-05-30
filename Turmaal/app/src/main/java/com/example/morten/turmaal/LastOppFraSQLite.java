package com.example.morten.turmaal;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class LastOppFraSQLite extends AsyncTask<String,String,Long> {

    public LastOppFraSQLite(Context context) {
        this.context = context;
    }

    Context context;
    HttpURLConnection connection = null;



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
