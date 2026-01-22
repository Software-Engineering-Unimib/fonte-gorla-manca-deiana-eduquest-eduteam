package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class Domanda {

    private int ID; // GLI ID SARANNO LEGATI ALLE DOMANDE SALVATI SUL DB
    private String testo;

    private int numeroRisposte;
    private ArrayList<Risposta> elencoRisposte;
    private Risposta rispostaCorretta;

    public Domanda(String testo) {

        setNumeroRisposte(0);
        elencoRisposte = new ArrayList<Risposta>();
        setTesto(testo);
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getTesto() { return testo; }

    public void setTesto(String testo) {
        if (testo == null) {
            throw new IllegalArgumentException("Il testo di una domanda non puo' essere nullo");
        }
        this.testo = testo;
    }

    public int getNumeroRisposte() { return numeroRisposte; }

    public void setNumeroRisposte(int numeroRisposte) {

        if (numeroRisposte < 0) {
            throw new IllegalArgumentException("Il numero di risposte non puo' essere negativo");
        }

        this.numeroRisposte = numeroRisposte;
    }

    public ArrayList<Risposta> getElencoRisposte() { return elencoRisposte; }

    public Risposta getRispostaCorretta() { return rispostaCorretta; }

    public void setRispostaCorretta(Risposta risposta) { rispostaCorretta = risposta; }
}
