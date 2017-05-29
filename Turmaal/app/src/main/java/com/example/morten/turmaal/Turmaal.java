package com.example.morten.turmaal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by morten on 29.05.2017.
 * /**
 * Dette er en klasse som bygger et Turmål-objekt fra JSON opplysninger fra databasen
 */
 

public class Turmaal {

    private String navn;
    private String type;
    private String beskrivelse;
    private Float breddegrad;
    private Float Lengdegrad;
    private int hoyde;
    private String regAnsvarlig;
    private String bilde_URL;


    public Turmaal() {
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public Float getBreddegrad() {
        return breddegrad;
    }

    public void setBreddegrad(Float breddegrad) {
        this.breddegrad = breddegrad;
    }

    public Float getLengdegrad() {
        return Lengdegrad;
    }

    public void setLengdegrad(Float lengdegrad) {
        Lengdegrad = lengdegrad;
    }

    public int getHoyde() {
        return hoyde;
    }

    public void setHoyde(int hoyde) {
        this.hoyde = hoyde;
    }

    public String getRegAnsvarlig() {
        return regAnsvarlig;
    }

    public void setRegAnsvarlig(String regAnsvarlig) {
        this.regAnsvarlig = regAnsvarlig;
    }

    public String getBilde_URL() {
        return bilde_URL;
    }

    public void setBilde_URL(String bilde_URL) {
        this.bilde_URL = bilde_URL;
    }

    static final String TABELL_NAVN = "Turmaal";
    static final String KOL_NAVN_Navn = "Navn";
    static final String KOL_NAVN_Hoyde = "Hoyde";
    static final String KOL_NAVN_Type = "Type";
    static final String KOL_NAVN_Beskrivelse = "Beskrivelse";
    static final String KOL_NAVN_Breddegrad = "Breddegrad";
    static final String KOL_NAVN_Lengdegrad = "Lengdegrad";
    static final String KOL_NAVN_RegAnsvarlig = "RegAnsvarlig";
    static final String KOL_NAVN_Bilde_URL = "BildeURL";



    // Konstruktør som bygger Turmaalobjekt basert på et JSONObject-objekt
    public Turmaal(JSONObject jsonTurmaal) throws JSONException {
        this.setNavn( jsonTurmaal.optString(KOL_NAVN_Navn));
        this.setType(jsonTurmaal.optString(KOL_NAVN_Type));
        this.setBeskrivelse(jsonTurmaal.optString(KOL_NAVN_Beskrivelse)) ;
        this.setHoyde(jsonTurmaal.optInt(KOL_NAVN_Hoyde)); ;
        this.setBreddegrad(Float.valueOf(jsonTurmaal.getString(KOL_NAVN_Breddegrad)));
        this.setLengdegrad(Float.valueOf(jsonTurmaal.getString(KOL_NAVN_Lengdegrad))) ;
        this.setRegAnsvarlig(jsonTurmaal.optString(KOL_NAVN_RegAnsvarlig));
        this.setBilde_URL(jsonTurmaal.optString(KOL_NAVN_Bilde_URL));  ;



    }

    // Metode som lager en ArrayList med Turmaal-objekter basert på en streng med JSONdata
    public static ArrayList<Turmaal> lagTurListe(String jsonTurmaalString)
            throws JSONException, NullPointerException {
        ArrayList<Turmaal> turmaalListe = new ArrayList<Turmaal>();
        JSONObject jsonData = new JSONObject(jsonTurmaalString);
        JSONArray jsonTurmaalTabell = jsonData.optJSONArray(TABELL_NAVN);
        for (int i = 0; i < jsonTurmaalTabell.length(); i++) {
            JSONObject jsonTurmaal = (JSONObject) jsonTurmaalTabell.get(i);
            Turmaal thisMaal = new Turmaal(jsonTurmaal);
            turmaalListe.add(thisMaal);
        }
        return turmaalListe;
    }



    public JSONObject toJSONObject() {
        JSONObject jsonTurmaal = new JSONObject();
        try {
            jsonTurmaal.put(KOL_NAVN_Navn, this.navn);
            jsonTurmaal.put(KOL_NAVN_Type, this.type);
            jsonTurmaal.put(KOL_NAVN_Hoyde, this.hoyde);
            jsonTurmaal.put( KOL_NAVN_Beskrivelse, this.beskrivelse);
            jsonTurmaal.put( KOL_NAVN_Breddegrad, this.breddegrad);
            jsonTurmaal.put( KOL_NAVN_Lengdegrad, this.Lengdegrad);
            jsonTurmaal.put( KOL_NAVN_RegAnsvarlig, this.regAnsvarlig);
            jsonTurmaal.put( KOL_NAVN_Bilde_URL, this.bilde_URL);


        } catch (JSONException e) {
            return null;
        }
        return jsonTurmaal;
    }






}