package com.example.morten.turmaal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Beskrivelse;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Breddegrad;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Hoyde;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Lengdegrad;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Navn;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_RegAnsvarlig;
import static com.example.morten.turmaal.Turmaal.KOL_NAVN_Type;
import static com.example.morten.turmaal.Turmaal.TABELL_NAVN;


/**
 * Created by morten on 30.05.2017.
 */

public class DatabaseOperasjoner extends SQLiteOpenHelper {

    public static final int databaseVersjon = 1;
    public String Lag_QUERY = "CREATE TABLE " + TABELL_NAVN + "("+
            KOL_NAVN_Navn + " Text," +
            Turmaal.KOL_NAVN_Hoyde + " Text" +
            Turmaal.KOL_NAVN_Type + " Text" +
            Turmaal.KOL_NAVN_Beskrivelse + " Text" +
            Turmaal.KOL_NAVN_Bilde_URL + " Text" +
            Turmaal.KOL_NAVN_RegAnsvarlig + " Text" +
            Turmaal.KOL_NAVN_Breddegrad + " Text" +
            Turmaal.KOL_NAVN_Lengdegrad + "  Text);";

    public DatabaseOperasjoner(Context context) {
        super(context, Turmaal.TABELL_NAVN, null, databaseVersjon);
        Log.d("Database operations", "Databasen er opprettet");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Lag_QUERY);
        Log.d("Database operations", "Tabellene er opprettet");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void putInformation(DatabaseOperasjoner dop, Turmaal tm) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Turmaal.KOL_NAVN_Navn, tm.getNavn());
        cv.put(Turmaal.KOL_NAVN_Hoyde, Integer.toString(tm.getHoyde()));
        cv.put(Turmaal.KOL_NAVN_Type, tm.getType());
        cv.put(Turmaal.KOL_NAVN_Beskrivelse, tm.getBeskrivelse());
        cv.put(Turmaal.KOL_NAVN_Bilde_URL, tm.getBilde_URL());
        cv.put(Turmaal.KOL_NAVN_RegAnsvarlig, tm.getRegAnsvarlig());
        cv.put(Turmaal.KOL_NAVN_Lengdegrad, Float.valueOf(tm.getLengdegrad()));
        cv.put(Turmaal.KOL_NAVN_Breddegrad, Float.valueOf(tm.getBreddegrad()));


        Long K = SQ.insert(Turmaal.TABELL_NAVN, null, cv);


        Log.d("Database operations", "En rad er lagt til");

    }


    public  Cursor getInformation(DatabaseOperasjoner dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] culoums = {
                KOL_NAVN_Navn,
                Turmaal.KOL_NAVN_Hoyde,
                Turmaal.KOL_NAVN_Type,
                Turmaal.KOL_NAVN_Beskrivelse,
                Turmaal.KOL_NAVN_Bilde_URL,
                Turmaal.KOL_NAVN_RegAnsvarlig,
                Turmaal.KOL_NAVN_Breddegrad,
                Turmaal.KOL_NAVN_Lengdegrad
        };
        Cursor CR = SQ.query(Turmaal.TABELL_NAVN, culoums, null, null, null, null, null);//null verdien er forskjellig sorteringer having orderby osv
        Log.d("Database operations", "login info er funnet");
        return CR;
    }
    public List<Turmaal> getAlleTurmaal() {
        List<Turmaal> tm= new ArrayList<Turmaal>();
        String selectQuery = "SELECT  * FROM " + TABELL_NAVN;

        Log.e("LOG", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
              Turmaal t = new Turmaal();
                t.setNavn(c.getString((c.getColumnIndex(KOL_NAVN_Navn))));
                t.setType((c.getString(c.getColumnIndex(KOL_NAVN_Type))));
                t.setBeskrivelse(c.getString(c.getColumnIndex(KOL_NAVN_Beskrivelse)));
                 t.setHoyde((c.getInt(c.getColumnIndex(KOL_NAVN_Hoyde))));
                t.setHoyde((c.getInt(c.getColumnIndex(KOL_NAVN_Hoyde))));
                t.setRegAnsvarlig((c.getString(c.getColumnIndex(KOL_NAVN_RegAnsvarlig))));
                t.setLengdegrad((c.getFloat(c.getColumnIndex(KOL_NAVN_Lengdegrad))));
                t.setBreddegrad((c.getFloat(c.getColumnIndex(KOL_NAVN_Breddegrad))));
                // Legget til tm listen
                tm.add(t);
            } while (c.moveToNext());
        }

        return tm;
    }

    public Cursor getRegAnsvarligNavn(DatabaseOperasjoner DOP, String navn) {
        SQLiteDatabase SQ = DOP.getReadableDatabase();
        String selection = Turmaal.KOL_NAVN_RegAnsvarlig + " LIKE ?";
        String colums[] = {Turmaal.KOL_NAVN_RegAnsvarlig};
        String args[] = {navn};
        Cursor CR = SQ.query(Turmaal.TABELL_NAVN, colums, selection, args, null, null, null);
        return CR;
    }




}
