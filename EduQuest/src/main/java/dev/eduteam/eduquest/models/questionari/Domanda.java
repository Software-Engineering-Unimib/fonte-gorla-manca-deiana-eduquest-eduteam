package dev.eduteam.eduquest.models.questionari;

import java.util.ArrayList;

public abstract class Domanda {

    public enum Type {
        DOMANDA_MULTIPLA,
        DOMANDA_MULTIPLE_RISPOSTE,
        DOMANDA_VERO_FALSO
    }

    protected Type tipoDomanda;

    // VARIABILI COMUNI A TUTTE LE DOMANDE

    protected int ID; // GLI ID SARANNO LEGATI ALLE DOMANDE SALVATI SUL DB
    private String testo;
    protected int numeroRisposte;
    protected ArrayList<Risposta> elencoRisposte = new ArrayList<>();

    // METODI COMUNI A TUTTE LE DOMANDE

    public static Domanda createDomandaOfType(Type tipoDomanda) {
        return switch (tipoDomanda) {
            case DOMANDA_MULTIPLA -> new DomandaMultipla("");
            case DOMANDA_MULTIPLE_RISPOSTE -> new DomandaMultipleRisposte("");
            case DOMANDA_VERO_FALSO -> new DomandaVeroFalso("");
            default -> null;
        };
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        if (testo == null) {
            throw new IllegalArgumentException("Il testo di una domanda non puo' essere nullo");
        }
        this.testo = testo;
    }

    public int getNumeroRisposte() {
        return numeroRisposte;
    }

    public void setNumeroRisposte(int numeroRisposte) {

        if (numeroRisposte < 0) {
            throw new IllegalArgumentException("Il numero di risposte non puo' essere negativo");
        }

        this.numeroRisposte = numeroRisposte;
    }

    public ArrayList<Risposta> getElencoRisposte() {
        return elencoRisposte;
    }

    public void addRisposta(Risposta risposta) {
        elencoRisposte.add(risposta);
    }

    public Type getTipoDomanda() {
        return tipoDomanda;
    }

    public abstract void setRispostaCorretta(Risposta risposta);
}
