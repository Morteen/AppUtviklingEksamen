package com.example.morten.turmaal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by morten on 30.05.2017.
 */

public class DatabaseOperasjoner extends SQLiteOpenHelper {

    public static final int DATABASEVERSJON = 1;
    public static final String DATABASE_NAME = "TUR.DB";
    public static final String TABLE_NAVN = "Tur_Tabell";

    public static final String TABELL_NAVN = Turmaal.TABELL_NAVN;

    public static final String Col_1 = "ID";
    public static final String NAVN = Turmaal.KOL_NAVN_Navn;
    public static final String TYPE = Turmaal.KOL_NAVN_Type;
    public static final String HOYDE = Turmaal.KOL_NAVN_Hoyde;
    public static final String BESKRIVELSE = Turmaal.KOL_NAVN_Beskrivelse;
    public static final String REGANSVARLIG = Turmaal.KOL_NAVN_RegAnsvarlig;
    public static final String LENGDEGRAD = Turmaal.KOL_NAVN_Lengdegrad;
    public static final String BREDDEGRAD = Turmaal.KOL_NAVN_Breddegrad;
    public static final String BILDEURL = Turmaal.KOL_NAVN_Bilde_URL;



    public DatabaseOperasjoner(Context context) {
        super(context, DATABASE_NAME, null, DATABASEVERSJON);
        Log.d("Database operations", "Databasen er opprettet");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABELL_NAVN + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAVN TEXT,TYPE TEXT,HOYDE TEXT,BESKRIVELSE TEXT,REGANSVARLIG TEXT,LENGDEGRAD TEXT,BREDDEGRAD TEXT,BILDEURL TEXT )");
        //db.execSQL(Lag_QUERY);
        Log.d("Database operations", "Tabellene er opprettet");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void putInformation(DatabaseOperasjoner dop, Turmaal tm) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAVN, tm.getNavn());
        cv.put(HOYDE, Integer.toString(tm.getHoyde()));
        cv.put(TYPE, tm.getType());
        cv.put(BESKRIVELSE, tm.getBeskrivelse());
        cv.put(BILDEURL, tm.getBilde_URL());
        cv.put(REGANSVARLIG, tm.getRegAnsvarlig());
        cv.put(LENGDEGRAD, Float.valueOf(tm.getLengdegrad()));
        cv.put(BREDDEGRAD, Float.valueOf(tm.getBreddegrad()));


        Long K = SQ.insert(TABELL_NAVN, null, cv);


        Log.d("Database operations", "En rad er lagt til");

    }


    public Cursor getInformation(DatabaseOperasjoner dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] culoums = {
                NAVN,
                HOYDE,
                TYPE,
                BESKRIVELSE,
                BILDEURL,
                REGANSVARLIG,
                BREDDEGRAD,
                LENGDEGRAD
        };
        Cursor CR = SQ.query(TABELL_NAVN, culoums, null, null, null, null, null);//null verdien er forskjellig sorteringer having orderby osv
        Log.d("Database operations", "Det er data i basen");
        return CR;
    }

    public  List<Turmaal> getAlleTurmaal(DatabaseOperasjoner dop) {
        List<Turmaal> tm = new ArrayList<Turmaal>();
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] culoums = {
                NAVN,
                HOYDE,
                TYPE,
                BESKRIVELSE,
                BILDEURL,
                REGANSVARLIG,
                BREDDEGRAD,
                LENGDEGRAD
        };
        Cursor c = SQ.query(TABELL_NAVN, culoums, null, null, null, null, null);//null verdien er forskjellig sorteringer having orderby osv

        // går gjennom listen og legger til objekter
        if (c.moveToFirst()) {
            do {
                Turmaal t = new Turmaal();
                t.setNavn(c.getString((c.getColumnIndex(NAVN))));
                t.setType((c.getString(c.getColumnIndex(TYPE))));
                t.setBeskrivelse(c.getString(c.getColumnIndex(BESKRIVELSE)));
                t.setHoyde((c.getInt(c.getColumnIndex(HOYDE))));
                t.setRegAnsvarlig((c.getString(c.getColumnIndex(REGANSVARLIG))));
                t.setLengdegrad((c.getFloat(c.getColumnIndex(LENGDEGRAD))));
                t.setBreddegrad((c.getFloat(c.getColumnIndex(BREDDEGRAD))));
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
        Cursor CR = SQ.query(TABELL_NAVN, colums, selection, args, null, null, null);
        return CR;
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    public  void slettSqliteBase(DatabaseOperasjoner DOP)
    {
        SQLiteDatabase SQ = DOP.getWritableDatabase();

        SQ.delete(TABELL_NAVN,null,null);

    }

}
